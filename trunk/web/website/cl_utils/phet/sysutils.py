#!/usr/bin/python

import os
import os.path as path
import re
import shutil

from properties import Properties

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

def update_jar_properties_file(jar_file, updates, jar_cmd):
    """Extracts the properties from jar_file (if it exists), adds or
    modifies the properties, then updates jar_file with the updates.
    Parameter 'updates' should be a dictionary:
        {'filename.properties' : {'key' : 'value1', etc}

    Note: all this happens in the current directory"""

    # Extract the file
    command = '%s xf %s %s' % (jar_cmd, jar_file, ' '.join(updates.keys()))
    run(command.split(' '))

    # Update the properties
    for file, properties in updates.items():
        p = Properties()
        if path.exists(file):
            p.load(open(file, 'r'))
        for key, value in properties.items():
            p.setProperty(key, value)
        p.store(open(file, 'w'))

    # Update the JAR
    command = '%s uf %s -C . %s' % (jar_cmd, jar_file, ' '.join(updates.keys()))
    run(command.split(' '))

    # Get rid of the evidence
    for text_file in updates.keys():
        os.remove(text_file)

