# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4
import inspect
import os
import sys
import logging
import uuid
import argparse

from jinja2 import Template

curdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
pardir = os.path.dirname(curdir)
sys.path.insert(0, pardir)

from lib.cmd import exec_cmd2
from lib.ssh import SshExec
from lib.template import tmplt_load_file, tmplt_save_file

import lib.settings

logging.config.dictConfig(lib.settings.LOGGING)

log = logging.getLogger('main')
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
log.addHandler(ch)



class DbExec():
    def __init__(self, script, dbcon='postgres', db='appdb', n = 'localhost',  nu = 'root', np = '1q2w3e', nk = None, du = 'postgres', dp = '1q2w3eQAZ', vsid = 0, su = 'appuser', sup = '1q2w3es5'):
        self.n = n
        self.nu = nu
        self.np = np
        self.nk = nk
        self.du = du
        self.dp = dp
        self.vsid = vsid
        self.su = su
        self.sup = sup
        self.script = script
        self.db = db
        self.dbcon = dbcon
    def run(self): 
        ssh = None
        r_script = None
        r_sql_expect = None
        tmp_script = None
        try:
            ssh = SshExec(log, self.n, self.nu, passwd = self.np, key_file = self.nk)
            t = tmplt_load_file(os.path.abspath(self.script))
            tmp_script = os.path.abspath(os.path.basename(self.script) + "_" + str(uuid.uuid4()))
            tmplt_save_file(t.render(vsid=self.vsid, usr_pass=self.sup, usr=self.su, db=self.db), tmp_script)
            r_script = os.path.join(ssh.rdir, os.path.basename(tmp_script))
            ssh.file_put(tmp_script, r_script)
            r_sql_expect = os.path.join(ssh.rdir, "psql_expect.sh")
            ssh.file_put(os.path.join(curdir, "psql_expect.sh"), r_sql_expect)
            ssh.cmd("chmod +x " + r_sql_expect)
            ssh.cmd(r_sql_expect + " " + self.du + " " + self.dp + " " + r_script + " " + self.dbcon, throw = True)
        except Exception as e:
            log.exception(str(e))
        finally:
            if tmp_script != None:
                exec_cmd2("rm -r -f " + tmp_script)
            if ssh != None:
                if r_script != None:
                    ssh.cmd("rm -r -f " + r_script)
              
if __name__=="__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-n", "--node", help="ssh host", type=str)
    parser.add_argument("-nu", "--node_user", help="ssh host user", type=str)
    parser.add_argument("-np", "--node_user_password", help="ssh host password", type=str)
    parser.add_argument("-nk", "--node_key", help="ssh host key", type=str)
    parser.add_argument("-du", "--db_user", help="db user", type=str)
    parser.add_argument("-dp", "--db_user_password", help="db user password", type=str)

    parser.add_argument("-svi", "--shard_id", help="db shard virtual id", type=int)
    parser.add_argument("-sup", "--shard_user_password", help="db shard user password", type=str)

    parser.add_argument("script", help="path of script to execute", type=str)

    args = parser.parse_args()
    if not args.node:
        args.node = 'localhost'
    if not args.node_user:
        args.node_user = 'root'
    if not args.node_user_password:
        args.node_user_password = '1q2w3e'
    if not args.shard_id:
        args.shard_id = 0
    if not args.shard_user_password:
        args.shard_user_password = '1q2w3e'
    if not args.db_user:
        args.db_user = 'postgres'
    if not args.db_user_password:
        args.db_user_password = '1q2w3eQAZ'
    if not args.node_key:
        args.node_key = None
    else:
        args.node_key = os.path.abspath(args.node_key)

    c = DbExec(args.script, n = args.node, nu = args.node_user, np = args.node_user_password,
        vsid = args.shardid, du = args.db_user, dp = args.db_user_password, nk = args.node_key, sup = args.shard_user_password)
    c.run()

