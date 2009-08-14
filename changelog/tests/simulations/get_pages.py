#!/usr/bin/python

from util import *
import os
import sys

def main():
    """Verify the pages still render the same."""

    os.chdir(os.path.abspath(os.path.dirname(__file__)))

    if len(sys.argv) == 1:
        print 'usage: get_pages.py output_dir [sim1 sim2 ...]'
        print '       no sims speciifed will do all'
        return 1

    outdir = sys.argv[1]
    if len(sys.argv) > 2:
        sims = sys.argv[2:]
    else:
        sims = SIM_ENCODED_NAMES

    for sim in sims:
        print 'Getting', sim
        url = BASE_URL + sim
        web_data = get_url(url)
        open(local_sim_filename(outdir, sim), 'w').write(web_data)

    
if __name__ == '__main__':
    retval = main()
    if retval is not None:
        sys.exit(retval)
