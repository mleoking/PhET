#!/usr/bin/python

import re
import os
import os.path as path
import sys
from StringIO import StringIO

from phet.utils import *
from sendemil import send_email

# Who the email will be from
FROM = 'daniel.mckagan@gmail.com'

# The list of people to receive email
RCPTS = ['daniel.mckagan@gmail.com']

# The path to the sims dir
SIMS_DIR='/web/htdocs/phet/sims'
#SIMS_DIR='/Users/danielmckagan/Workspaces/PhET/workspace/_webfiles/sims-postiom'

def process_files_into_dict(raw_file_list):
    """Change the raw file list './dir/filename' to a dictonary of this form:
    {dir_name: [file_names], dir_name: [file names], ...}
    """

    map = {}
    for p in raw_file_list:
        s = path.split(p)
        f = s[1]
        d = path.split(s[0])[1]
        try:
            map[d].append(f)
        except KeyError:
            map[d] = [f]

    return map

def main():
    # Change the directory to sims directory
    os.chdir(SIMS_DIR)

    # File types to scan
    file_types = ['.xml', '.html', '.jar', '.jnlp']

    # A map from locale codes that are the same to the language/country name
    duplicate_map = {'bp':    'Brazilian Portuguese',
                     'br_PT': 'Brazilian Portuguese',
                     'tc':    'Taiwanese Chinese',
                     'zh_TW': 'Taiwanese Chinese'}

    # Regex to extract the locale
    re_locale = re.compile('(.*?)_(([a-z]{2})(_([A-Z]{2}))?)')

    # Ready our counters
    duplicates = []
    uniques = []

    # Prepare an output buffer
    output = StringIO()
    output.write('Good morning!\n\n')
    output.write('Here are the results of the scan for duplicate locales.\n')
    output.write('(Seeking locales like bp and br_PT in the same context.)\n\n')
    output.write('Looking in:\n')
    output.write(SIMS_DIR)

    # Do only one file type at a time
    for file_type in file_types:
        output.write('Processing file type: ' + file_type + '\n')

        # Get a dirname to filename list map
        type_files = recursive_get_files_by_extension('.', file_type)
        type_files = process_files_into_map(type_files)

        # Go in alphabetical order
        order = type_files.keys()
        order.sort()

        # Loop through, in order
        for d in order:
            # Get the files
            files = type_files[d]

            # This contains a map from language/country to files
            sum = {}

            # Loop through each file in the directory
            for f in files:

                # Seach for the _xx or _xx_YY locale
                match = re_locale.search(f)

                # If no match, or it is not of interest, continue
                if not match: continue
                if not duplicate_map.has_key(match.group(2)): continue
                
                # It is of interest, add file to the list under the locale type
                # (create the list if necessary)
                if not sum.has_key(duplicate_map[match.group(2)]):
                    sum[duplicate_map[match.group(2)]] = []
                sum[duplicate_map[match.group(2)]].append(f)

            # Add the results to our running total
            for type, found in sum.items():
                if len(found) >= 2:
                    duplicates.extend(found)
                else:
                    uniques.extend(found)

    # Sort the results
    duplicates.sort()
    uniques.sort()

    # Dump the results into the output
    output.write('--- Results ---\n')
    output.write('Duplicates:\n')
    output.write('    ' + '\n    '.join(duplicates) + '\n')
    output.write('Uniques:\n')
    output.write('    ' + '\n    '.join(uniques) + '\n')

    # Add a nice footer
    output.write('\n')
    output.write('- The happy happy locale finding robot\n')
    output.write('/web/htdocs/phet/cl_utils/locale-check.py\n')

    # Email the output
    output.seek(0)
    send_email(FROM_ADDR, RCPTS, subject, output.read())

    
if __name__ == '__main__':
    retval = main()
    sys.exit(retval)
