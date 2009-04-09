#!/usr/bin/python

import re
import os
import os.path as path
import sys

from phet.utils import *

FILE_TYPES = ['.php', '.css', '.js']

def usage():
    print 'usage: %s website_root' % (sys.argv[0])
    print
    print '   Removes any trailing whitespace on all lines in these file types:'
    print '     ', FILE_TYPES
    print
    print '   website_root - the root directory of the website'

def main():
    """Run from the command line"""

    if len(sys.argv) != 2:
        usage()
        return 1

    website_root = sys.argv[1]
    files = recursive_get_files_by_extension(website_root, FILE_TYPES, ['.svn'])
    re_site = re.compile(' +$')
    for file in files:
        print 'Processing', file
        out_lines = []
        for line in open(file, 'r'):
            out_lines.append(re_site.sub('', line))

        open(file, 'w').writelines(out_lines)

if __name__ == '__main__':
    retval = main()
    sys.exit(retval)
