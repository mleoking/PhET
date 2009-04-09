#!/usr/bin/python

import os.path as path
import sys

import phet.utils as utils

def main():
    """Run from command line"""

    if len(sys.argv) != 3:
        print 'usage: fix_codebase.py basedir codebase'
        print 'Find all .jnlp files under basedir and fix the codebase'
        print 'attribute to be the specified codebase attribute'
        return 1

    basedir = sys.argv[1]
    codebase = sys.argv[2]

    files = utils.recursive_get_files_by_extension(basedir, ['.jnlp'])
    for filename in files:
        print 'Processing', filename
        dirname = path.basename(path.dirname((path.abspath(filename))))
        full_codebase = path.join(codebase, dirname)
        utils.set_jnlp_codebase(filename, full_codebase)

if __name__ == '__main__':
    retcode = main()
    sys.exit(retcode)
