#!/usr/bin/python

import os.path as path
import sys

import phet.utils as utils

def main():
    """Run from command line"""

    if len(sys.argv) != 3:
        print 'usage: jnlp_grep.py basedir text'
        print 'Find all .jnlp files under basedir and grep for text'
        return 1

    basedir = sys.argv[1]
    grep_text = sys.argv[2]

    files = utils.recursive_get_files_by_extension(basedir, ['.jnlp'])
    for filename in files:
        print 'Processing', filename
        dirname = path.basename(path.dirname((path.abspath(filename))))
        utils.jnlp_grep(filename, grep_text)

if __name__ == '__main__':
    retcode = main()
    sys.exit(retcode)
