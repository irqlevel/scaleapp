# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4
import paramiko
import os
import sys
import time
import inspect
import logging
import json
import httplib
import socket

currentdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))

import settings
import cmd
import ssh as sshProvider

logging.config.dictConfig(settings.LOGGING)

log = logging.getLogger('main')


ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
ch.setFormatter(formatter)
log.addHandler(ch)

def http_post(ip, uri, data, content_type, extra_headers, port = 9000, https=False):
    con = None
    result = None

    headers = {"Content-Type" : content_type}
    headers.update(extra_headers)

    try:
        log.info("opening http to host=" + ip)
        if https:
            con = httplib.HTTPSConnection(ip, port=port)
        else:
            con = httplib.HTTPConnection(ip, port=port)

        con.request("POST", uri, data, headers)
        rp = con.getresponse()
        if rp.status != httplib.OK:
            raise Exception("POST " + ip + " : " + uri + " failed with http response code=" + str(rp.status))
        result = json.loads(rp.read())
    except Exception as e:
        log.exception(str(e))
    finally:
        if con != None:
            try:
                con.close()
            except Exception as e:
                log.exception(str(e))
    return result

def user_register(ip, username, password):
    d = {}
    d["email"] = username
    d["password"] = password
    result = http_post(ip, "/api/user/register", json.dumps(d), "application/json", {})
    return result

def user_delete(ip, username, password):
    d = {}
    d["email"] = username
    d["password"] = password
    result = http_post(ip, "/api/user/delete", json.dumps(d), "application/json", {})
    return result

def get_my_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("gmail.com",80))
    ip = s.getsockname()[0]
    s.close()
    return ip

def test_user():
    r = user_register("localhost", "asmetanin", "1q2w3eQAZ")
    print r
    r = user_delete("localhost", "asmetanin", "1q2w3eQAZ")
    print r


def test_start(host_ip, server_ip = None, clientId = None, authId = None, username = "fedorok", password = "1q2w3es5"):
    if server_ip == None:
        server_ip = get_my_ip()
    if clientId == None or authId == None:
        r = user_register(server_ip, username, password)
        if int(r["error"]) != 0:
            user_delete(server_ip, username, password)
            r = user_register(server_ip, username, password)
            if int(r["error"]) != 0:
                raise Exception("user_register failed with err=" + r["error"])
        clientId = r["clientId"]
        authId = r["authId"]
    ssh = sshProvider.SshExec(log, host_ip, "Administrator", "1q2w3eQAZ")
    r_path = "c:\\testdir"
    
    client_n = "kclient.exe"
    client_p = os.path.join(settings.X64_RELEASE_DIR, client_n)
    r_client_p = os.path.join(r_path, client_n)
    r_log_path = "c:\\Windows\\pdata"
    r_kdll_path = "c:\\Windows\\System32\\kdll.dll"

    ssh.cmd("del /f /s /q /a " + r_kdll_path, throw = False)
    ssh.cmd("rmdir /s /q " + r_path, throw = False)
    ssh.cmd("mkdir " + r_path)

    ssh.cmd("rmdir /s /q " + r_log_path, throw = False)
    ssh.cmd("mkdir " + r_log_path)

    ssh.file_put(client_p, r_client_p)
    serverName = server_ip
    serverPort = "9111"

    ssh.cmd(r_client_p + " start " + clientId + " " + authId + " " + serverName + " " + serverPort)
    #ssh.cmd(r_client_p)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        raise Exception("invalid num of args=" + str(len(sys.argv)))
    host_ip = sys.argv[1]
    test_start(host_ip)
