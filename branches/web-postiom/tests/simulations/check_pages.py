#!/usr/bin/python

import os
from util import *
import sys

def main():
    """Verify the pages still render the same."""

    os.chdir(os.path.abspath(os.path.dirname(__file__)))

    if len(sys.argv) == 1:
        print 'usage: check_pages.py output_dir [sim1 sim2 ...]'
        print '       no sims speciifed will do all'
        return 1

    indir = sys.argv[1]
    if len(sys.argv) > 2:
        sims = sys.argv[2:]
    else:
        sims = SIM_ENCODED_NAMES

    for sim in sims:
        print 'Comparing', sim
        url = BASE_URL + sim
        web_data = get_url(url)
        file_data = open(local_sim_filename(indir, sim), 'r').read()
        if web_data != file_data:
            fn = local_sim_filename(indir, sim+'BAD')
            print 'ERROR: files are different, writing web data to', fn
            open(fn, 'w').write(web_data)

if __name__ == '__main__':
    retval = main()
    if retval is not None:
        sys.exit(retval)

