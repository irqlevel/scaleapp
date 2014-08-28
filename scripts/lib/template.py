# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4
import inspect
import os
import sys
import logging
import uuid
import argparse

from jinja2 import Template

def tmplt_load_file(fpath):
    with open(fpath, "r") as fp:
        data="".join(line for line in fp)
    t = Template(data)
    return t

def tmplt_save_file(s, fpath):
    f = open(fpath, "w")
    f.write(s)
    f.close()
