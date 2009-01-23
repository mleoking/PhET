#!/usr/bin/python

import os
import re

def run(command):
    """Generic command runner supporting Python2.3 (like on tigercat)
    and above"""

    try:
        import subprocess
        p = subprocess.Popen(command, stdout=subprocess.PIPE)
        return p.stdout.readlines()
    except ImportError:
        # Subprocess doesn't exist, try with popen below
        pass
    except OSError:
        # The command didn't work, try with popen below
        pass

    # Older version of Python need the older popen method
    c = re.sub('\s+', ' ', ' '.join(command))
    pipe = os.popen(c, 'r')
    lines = pipe.readlines()
    retval = pipe.close()
    if (retval != None) :
        message = 'cannot execute "%s"\n' % (command[0])
        if -1 != command[0].find('jar'):
            message = message + 'perhaps you need --jar-cmd=CMD to point ' \
                'to your version of "jar"\n'
        message = message + 'full command:' + ' '.join(command)
        raise RuntimeError(message)

    return lines

