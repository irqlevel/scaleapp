# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4
import inspect
import os
import sys
import logging
import uuid
import argparse
import base64
import json

from db.db_ssh_exec import DbExec 
from jinja2 import Template

curdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
pardir = os.path.dirname(curdir)
confdir = os.path.join(pardir, 'conf')
dbsdir = os.path.join(curdir, 'db')

from lib.cmd import exec_cmd2
from lib.ssh import SshExec
from lib.template import tmplt_load_file, tmplt_save_file


import lib.settings

logging.config.dictConfig(lib.settings.LOGGING)

log = logging.getLogger('main')
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
log.addHandler(ch)

usr = 'appuser'
usr_pass = '1q2w3es5'
db = 'appdb'
key_path = '/home/andrey/Downloads/ec2kp.pem'
node_user = 'ec2-user'

class Cluster():
    def __init__(self):
        self.usr = usr
        self.usr_pass = usr_pass
        self.db = db
        self.key_path = key_path
        self.node_user = node_user
        self.shards = {}

    def shards_path(self):
        return os.path.join(confdir, 'shards.json')

    def load_shards(self):
        fp = open(self.shards_path(), 'r')
        self.shards = json.loads(fp.read())
        fp.close()

    def delete_shards(self):
        exec_cmd2("rm -r -f " + self.shards_path(), throw = True, elog = log)

    def reload(self):
        self.load_shards()
        for n in self.shards.keys():
            vsid = self.shards[n]
            self.conf_delete(vsid)
            self.conf_gen(vsid, n)

    def gen_node_id(self):
        bs = os.urandom(32)#256bits, for SHA-256
        return base64.b64encode(bs)

    def gen_node_ids(self, count):
        ids = []
        for i in xrange(count):
            ids.append(self.gen_node_id())
        return ids

    def conf_fpath(self, vsid):
        return os.path.join(confdir, 'shardconf_' + str(vsid) + '.json')

    def conf_tmplt(self):
        return os.path.join(confdir, 'shardconf_t.json')

    def conf_gen(self, vsid, n):
        t = tmplt_load_file(self.conf_tmplt())   
        tmplt_save_file(t.render(nodeIds = self.gen_node_ids(10), node = n, vsid=vsid, 
            usr_pass=self.usr_pass, usr=self.usr, db=self.db), self.conf_fpath(vsid))

    def conf_delete(self, vsid):
        exec_cmd2("rm -r -f " + self.conf_fpath(vsid), throw = True, elog = log)

    def delete(self):
        self.load_shards()
        for n in self.shards.keys():
            vsid = self.shards[n]
            c = DbExec(os.path.join(dbsdir, 'db_delete.sql'), n = n, nu=self.node_user, nk=self.key_path,
            vsid = vsid, du = 'postgres', dp='1q2w3eQAZ', su = self.usr, db = self.db, sup = self.usr_pass)
            c.run()
            self.conf_delete(vsid)
        self.delete_shards()

    def create(self):
        self.load_shards()
        for n in self.shards.keys():
            vsid = self.shards[n]
            c = DbExec(os.path.join(dbsdir, 'db_create.sql'), n = n, nu=self.node_user, nk=self.key_path,
                vsid = vsid, du = 'postgres', dp='1q2w3eQAZ', su = self.usr, db = self.db, sup = self.usr_pass)
            c.run()

            c = DbExec(os.path.join(dbsdir, 'db_init.sql'), n = n, nu=self.node_user, nk=self.key_path,
                vsid = vsid, du = self.vusr(vsid), dp=self.usr_pass, su = self.usr, db = self.db, dbcon = self.vdb(vsid), sup = self.usr_pass)
            c.run()
            self.reload()

    def vusr(self, vsid):
        return self.usr + '_' + str(vsid)
    def vdb(self, vsid):
        return self.db + '_' + str(vsid)
 
if __name__=="__main__":
    if len(sys.argv) != 2:
        raise Exception("invalid args")
    
    c = Cluster()
    if sys.argv[1] == "reload":
        c.reload()
    elif sys.argv[1] == "create":
        c.create()
    elif sys.argv[1] == "delete":
        c.delete()
    else:
        raise Exception("invalid args")
