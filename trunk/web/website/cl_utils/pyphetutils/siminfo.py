#!/usr/bin/python

import os
import os.path as path
import re
from commands import run

PHP_RUNNER = 'phprunner.sh'
GET_ALL_SIM_INFO = 'get_all_sim_info.php'

def coerce_data(data):
    """Process the database data to native forms, if possible"""
    # Empty string becomes None
    if data == '':
        return None

    try:
        # Try to make it an int
        return int(data)
    except ValueError:
        # It won't become an int, return the original
        return data

def get_sim_info():
    """Run 'phprunner.sh get_sim_info.php' which will dump all
    info about simulations from the database to a pipe.  Parse
    this into lists that Python can use"""

    # Look for phprunner.sh in the same directory as this script
    phprunner = path.abspath(path.join(path.dirname(__file__), PHP_RUNNER))
    get_all_sim_info = path.abspath(path.join(path.dirname(__file__),
                                              GET_ALL_SIM_INFO))
    try:
        os.stat(phprunner)
        os.stat(get_all_sim_info)
    except OSError:
        raise RuntimeError('Cannot find "%s" or "%s",\nsearched in "%s"' % \
                               (PHP_RUNNER,
                                GET_ALL_SIM_INFO,
                                path.dirname(__file__)))

    # Get the sim info
    raw_sim_info = run([phprunner, get_all_sim_info])
    raw_sim_info = ''.join(raw_sim_info)

    # First check to see if there was an error
    error_match = re.search('(Database error:.*)', raw_sim_info, re.I)
    if error_match:
        raise RuntimeError(error_match.group(1))

    # Proess the sim info.  Records are separated by \0\0\0
    # Fields are separated by \0
    sim_info = []

    # Loop through each row/record
    for raw_row in raw_sim_info.split('\0\0\0'):
        # Split the row into columns/fields and process
        sim = {}
        for field in raw_row.split('\0'):
            # The first ':' separets the column/field title from the data
            field_title_end = field.find(':')
            if field_title_end == -1:
                raise RuntimeError('Bad data in get_all_sim_info.php:' + field)

            # Parse the info
            field_title = field[0:field_title_end]
            field_data = coerce_data(field[field_title_end + 1:])
            sim[field_title] = field_data

        # Append this sim to the list
        sim_info.append(sim)

    # All done, share the results
    return sim_info

