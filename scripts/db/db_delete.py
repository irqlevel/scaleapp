# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4
import inspect
import os
import sys
import logging
import uuid
import argparse
from db_ssh_exec import DbExec 
from jinja2 import Template

curdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
pardir = os.path.dirname(curdir)
sys.path.insert(0, pardir)

from lib.cmd import exec_cmd2
from lib.ssh import SshExec

import lib.settings

logging.config.dictConfig(lib.settings.LOGGING)

log = logging.getLogger('main')
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
log.addHandler(ch)
   
if __name__=="__main__":
    usr = 'appuser'
    usr_pass = '1q2w3es5'
    db = 'appdb'
    vsid = 0
    key_path = '/home/andrey/Downloads/ec2kp.pem'
    node = 'ec2-54-77-136-49.eu-west-1.compute.amazonaws.com'
    node_user = 'ec2-user'
    c = DbExec('db_delete.sql', n = node, nu=node_user, nk=key_path,
        vsid = vsid, du = 'postgres', dp='1q2w3eQAZ', su = usr, db = db, sup = usr_pass)
    c.run()

