#!/usr/bin/python

import os
import os.path as path
import re
import shutil
import tempfile
import stat

from sysutils import *

# Define types of sims, these should match those in include/sim-utils.php
SIM_TYPE_JAVA = 0
SIM_TYPE_FLASH = 1
SIM_TYPE = ['Java', 'Flash']

# Default locale, this should match 'include/locale-utils.php'
DEFAULT_LOCALE = 'en'

# Regular expressions to find the locales for a sim type
REGEX_JAVA_LOCALE = '^%s(_(([a-z][a-z])(_([A-Z][A-Z]))?))?.jnlp$'

#
#
# Database info section
#
#

PHP_RUNNER = 'phprunner.sh'
GET_ALL_SIM_INFO = 'get_all_sim_info.php'

def get_sim_info():
    """Run 'phprunner.sh get_sim_info.php' which will dump all
    info about simulations from the database to a pipe.  Parse
    this into lists that Python can use"""

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

def get_java_sim_info():
    all_sim_info = get_sim_info()
    sim_info = [d for d in all_sim_info
                if d['sim_is_real'] and (d['sim_type'] == 0)]
    return sim_info

#
#
# Sim utilities section
#
#

def detect_sim_type(project, simulation, sim_root):
    """Detect the simulation type"""
    # Get the absolute path to the project
    project_dir = path.abspath(path.join(sim_root, project))
    
    # Compile regex to extract the info
    try:
        jar_file = '%s_all.jar' % (project,)
        #jnlp_file = '%s.jnlp' % (simulation,)
        os.stat(path.join(project_dir, jar_file))
        #os.stat(path.join(project_dir, jnlp_file))
        return SIM_TYPE_JAVA
    except OSError:
        pass

    try:
        html_file = '%s_en.html' % (simulation,)
        xml_file = '%s-strings_en.xml' % (simulation,)
        swf_file = '%s.swf' % (simulation,)
        os.stat(path.join(project_dir, html_file))
        os.stat(path.join(project_dir, xml_file))
        os.stat(path.join(project_dir, swf_file))
        return SIM_TYPE_FLASH
    except OSError:
        raise RuntimeError('Cannot detect sim type!')


#
#
# Java section
#
#

def get_java_locales(project, simulation, project_dir):
    # Compile regex to extract the info
    jnlp_locale = re.compile(REGEX_JAVA_LOCALE % (simulation,), re.IGNORECASE)

    locales = []
    for file in os.listdir(project_dir):
        match = jnlp_locale.search(file)
        if match:
            if match.group(1) is None:
                locales.append((DEFAULT_LOCALE, DEFAULT_LOCALE, None))
            else:
                locales.append(match.group(2, 3, 5))

    return locales

def process_java_sim(project, simulation, project_dir,
                     legacy_support, jar_cmd, verbose):
    # The 'jar' command extracts to the current directory, so create a
    # temporary directory and work from there
    temp_dir = tempfile.mkdtemp(prefix='phet_')
    oldcwd = os.getcwd()
    os.chdir(temp_dir)

    # Source JAR file to modify
    source_jar = '%s_all.jar' % (project,)
    source_jar_path = path.join(project_dir, source_jar)
    try:
        os.stat(source_jar_path)
        open(source_jar_path)
    except OSError, e:
        # Source jar does not exist, return to previous working directory
        os.chdir(oldcwd)
        os.rmdir(temp_dir)
        raise RuntimeError('Cannot find source JAR file "%s"' % (source_jar,))
    except IOError, e:
        os.chdir(oldcwd)
        os.rmdir(temp_dir)
        if e.errno == 13:
            raise RuntimeError('ERROR: insufficient permission to read "%s"' % \
                                   (source_jar_path,))

        # Don't know what happened, propogate the error
        raise

    # Loop through all the locales
    locales = get_java_locales(project, simulation, project_dir)
    for locale, language, country in locales:
        if verbose:
            print '   Creating locale "%s"...' % (locale)

        if locale == DEFAULT_LOCALE:
            jar_file = '%s.jar' % (simulation,)
        else:
            jar_file = '%s_%s.jar' % (simulation, locale)

        # Copy the source JAR file to this directory
        shutil.copyfile(source_jar_path, jar_file)

        main_flavor_properties = {'main.flavor' : simulation}
        locale_properties = {}
        locale_properties['language'] = language
        if country is not None:
            locale_properties['country'] = country

        updates = {}
        updates['jar-launcher.properties'] = main_flavor_properties
        updates['jar-launcher.properties'].update(locale_properties)

        if legacy_support:
            updates['main-flavor.properties'] = main_flavor_properties
            updates['options.properties'] = {'locale' : language }
            updates['flavor.properties'] = {'flavor' : simulation}
            updates['locale.properties'] = locale_properties

        try:
            update_jar_properties_file(jar_file, updates, jar_cmd)
        except Exception, e:
            print "AK!", e
            # TODO: make this a specific exception
            os.chdir(oldcwd)
            os.rmdir(temp_dir)
            raise

        # Explicitly set the permissions of the jar 0664
        os.chmod(jar_file,
                 stat.S_IRUSR | stat.S_IWUSR | 
                 stat.S_IRGRP | stat.S_IWGRP | 
                 stat.S_IROTH)

        # JAR file is ready, move it to where it belongs
        dest = path.join(project_dir, jar_file)
        try:
            super_move(jar_file, dest, verbose)
        except OSError:
            print 'ERROR: cannot overwrite original file "%s", skipping' % \
                (jar_file,)
            os.remove(jar_file)

    # Restore the original working directory
    os.chdir(oldcwd)

    # Cleanup
    os.rmdir(temp_dir)


