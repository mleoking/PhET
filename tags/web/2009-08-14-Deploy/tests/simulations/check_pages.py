#!/usr/bin/python

import re
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
        orig_web_data = get_url(url)
        orig_file_data = open(local_sim_filename(indir, sim), 'r').read()
        
        #
        # Process thet data so it handles slight differences

        # Change all white space to one space
        web_data = re.sub('\s+', ' ', orig_web_data)
        file_data = re.sub('\s+', ' ', orig_file_data)

        # Remove version info
        file_data = re.sub('Version [.0-9]+ ', 'Version ', file_data)
        web_data = re.sub('Version [.0-9]+ ', 'Version ', web_data)

        # Remove size info
        file_data = re.sub('[.0-9]+ KB', '', file_data)
        web_data = re.sub('[.0-9]+ KB', '', web_data)

        # Remove foreign image
        file_data = re.sub('<img src="http://phet.colorado.edu/sims', '<img src="../../sims', file_data)
        web_data = re.sub('<img src="http://phet.colorado.edu/sims', '<img src="../../sims', web_data)

        # Remove title keywords
        file_data = re.sub('<title>[^-]+-.*</title>', '<title>\1- </title>', file_data)
        web_data = re.sub('<title>[^-]+-.*</title>', '<title>\1- </title>', web_data)

        # Remove the <div id="main"> and end </div> tags from the file
        file_data = re.sub('<div class="main"> </div> ', '', file_data)
        file_data = re.sub('<div class="main"> ', '', file_data)
        web_data = re.sub('<div class="main"> ', '', web_data)
        file_data = re.sub('</div> </div> <div id="footer">',
                           '</div> <div id="footer">',
                           file_data)

        if False:
            for d in range(len(file_data)):
                if file_data[d] != web_data[d]:
                    fn = local_sim_filename(indir, sim+'BAD')
                    print 'ERROR: files are different, writing web data to', fn
                    open(fn, 'w').write(orig_web_data)
                    fn = local_sim_filename(indir, sim+'BAD1')
                    print 'ERROR: files are different, writing processed web data to', fn
                    open(fn, 'w').write(web_data)
                    fn = local_sim_filename(indir, sim+'BAD2')
                    print 'ERROR: files are different, writing processed file data to', fn
                    open(fn, 'w').write(file_data)
                    print 'no match a index=',d
                    return 3

        if web_data != file_data:
            fn = local_sim_filename(indir, sim+'BAD')
            print 'ERROR: files are different, writing web data to', fn
            open(fn, 'w').write(orig_web_data)

if __name__ == '__main__':
    retval = main()
    if retval is not None:
        sys.exit(retval)

