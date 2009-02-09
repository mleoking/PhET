#!/usr/bin/python

import os
import re
import shutil

def run(command, verbose=False):
    """Generic command runner supporting Python2.3 (like on tigercat)
    and above"""

    if verbose:
        print 'Running "%s"' % (' '.join(command),)

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

def super_move(src, dest, verbose=False):
    """Permissions are really important, move the file and preserve
    them.  First try moving the file, and if that doesn't work, try
    removing the original file and moving it.  This works when some
    group permissions are set.  If that doesn't work, let the
    exception go.
    """

    try:
        shutil.move(src, dest)
    except OSError, e:
        if verbose:
            print 'Move failed, trying removing the destination file trick'

        try:
            os.remove(dest)
        except OSError, e:
            # File probably did not exist
            pass
        
        # Try again
        shutil.move(src, dest)

    except IOError:
        if verbose:
            print 'Move failed, trying removing the destination file trick'

        try:
            os.remove(dest)
        except OSError, e:
            # File probably did not exist
            pass

        # Try again
        shutil.move(src, dest)
