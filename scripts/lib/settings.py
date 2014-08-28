# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4
import os
import inspect

currentdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
CURR_DIR = os.path.abspath(currentdir)
LOGGING = {
    'version' : 1,
    'disable_existing_loggers' : False,
    'formatters': {
        'verbose' : {
            'format' : '%(levelname)s %(asctime)s %(module)s %(process)d %(thread)d %(message)s'
        },
        'simple' : {
            'format' : '%(levelname)s %(asctime)s %(module)s %(message)s'
        },
    },
    'handlers' : {
	'file' : {
            'level' : 'DEBUG',
	    'class' : 'logging.FileHandler',
            'formatter' : 'simple',
            'filename' : os.path.join(CURR_DIR, 'pylog.log'),
	},
    },
    'loggers' : {
        'django.request' : {
            'handlers' : ['file'],
            'level' : 'DEBUG',
            'propagate' : True,
        },
        'main' : {
            'handlers' : ['file'],
            'level' : 'DEBUG',
            'propagate' : True,
        },
    },
}

