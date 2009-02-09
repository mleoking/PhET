#!/usr/bin/python

import os
import os.path as path
import re
import shutil
import tempfile
import stat

from commands import *

# Define types of sims, these should match those in include/sim-utils.php
SIM_TYPE_JAVA = 0
SIM_TYPE_FLASH = 1
SIM_TYPE = ['Java', 'Flash']

# Default locale, this should match 'include/locale-utils.php'
DEFAULT_LOCALE = 'en'

# Regular expressions to find the locales for a sim type
REGEX_JAVA_LOCALE = '^%s(_(([a-z][a-z])(_([A-Z][A-Z]))?))?.jnlp$'
REGEX_FLASH_LOCALE = '^%s_([a-z][a-z](_([A-Z][A-Z]))?).html$'

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
# JAR utilities section
#
#

def update_jar_text_file(jar_file, updates, jar_cmd):
    """Extracts the text_file from jar_file (if it exists), appends text
    it, then updates jar_file with the updates.  Note: all this
    happens in the current directory"""

    # Extract the file
    command = '%s xf %s %s' % (jar_cmd, jar_file, ' '.join(updates.keys()))
    run(command.split(' '))

    # Append the text
    for text_file, text in updates.items():
        open(text_file, 'a').write(text)

    # Update the JAR
    command = '%s uf %s -C . %s' % (jar_cmd, jar_file, ' '.join(updates.keys()))
    run(command.split(' '))

    # Get rid of the evidence
    for text_file in updates.keys():
        os.remove(text_file)


#
#
# Java section
#
#

def get_java_locales(project, simulation, sim_root):
    # Get the absolute path to the project
    project_dir = path.abspath(path.join(sim_root, project))

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

def process_java_sim(project, simulation, sim_root,
                     verbose, jar_cmd):
    # Get the absolute path to the project and sim root
    project_dir = path.abspath(path.join(sim_root, project))
    sim_abs_dir = path.abspath(sim_root)

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
    locales = get_java_locales(project, simulation, sim_abs_dir)
    for locale, language, country in locales:
        if verbose:
            print '   Creating locale "%s"...' % (locale)

        if locale == DEFAULT_LOCALE:
            jar_file = '%s.jar' % (simulation,)
        else:
            jar_file = '%s_%s.jar' % (simulation, locale)

        # Copy the source JAR file to this directory
        shutil.copyfile(source_jar_path, jar_file)

        updates = {}
        updates['options.properties'] = 'locale=%s\n' % (language,)
        updates['main-flavor.properties'] = 'main.flavor=%s\n' % (simulation,)
        updates['flavor.properties'] = 'flavor=%s\n' % (simulation,)
        text = 'language=%s\n' % (language,)
        if country is not None:
            text = text + 'country=%s\n' % (country,)
        updates['locale.properties'] = text

        updates['args.properties'] = updates['flavor.properties'] + \
            updates['locale.properties']

        try:
            update_jar_text_file(jar_file, updates, jar_cmd)
        except Exception, e:
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


#
#
# Flash section
#
#

def process_flash_jar(project, simulation, locale, sim_root,
                      template_location, jar_cmd):
    # Get an absolute path to the project
    project_dir = path.abspath(path.join(sim_root, project))

    # Create temp directory
    tdir = tempfile.mkdtemp(prefix='phet_')

    # Add extra files needed
    tt = path.join(tdir, 'flash-launcher-args.txt')
    open(tt, 'w').write('%s %s' % (simulation, locale))

    # Make JAR
    jar_name = '%s_%s.jar' % (simulation, locale)

    # Grab all the files from the project directory for this sim
    files = os.listdir(project_dir)

    # Get all the localized string files
    string_files = [file for file in files \
                        if -1 != file.find('%s-strings' % (simulation,))]

    # Prepare the arguments for the jar command
    args = ['-C %s flash-launcher-args.txt' % (tdir,), 
            '-C %s %s.properties' % (project_dir, simulation),
            '-C %s flash-launcher-template.html' % (template_location,),
            '-C %s %s.swf' % (project_dir, simulation),
            '-C %s edu' % (template_location,)
            ]
    args.extend(['-C %s %s' % (project_dir, string_file) 
                 for string_file in string_files])


    manifest = path.join(template_location, 'META-INF', 'MANIFEST.MF')
    command = '%s cmf %s %s %s' % (jar_cmd, manifest, jar_name, ' '.join(args))
    run(command.split(' '))

    # Move JAR into place
    locale_jar = path.join(project_dir, '%s_%s.jar' % (simulation, locale))
    try:
        super_move(jar_name, locale_jar)
    except OSError:
        print 'ERROR: cannot overwrite original file "%s"' % (locale_jar,)
        os.remove(jar_name)
        raise

    # Delete temp directory
    os.remove(tt)
    os.rmdir(tdir)

def get_flash_locales(project, simulation, sim_root):
    # Grab all the files from the project directory for this sim
    files = os.listdir(path.join(sim_root, project))

    # Get locales from HTML files
    flash_locale_extract = re.compile(REGEX_FLASH_LOCALE % (simulation,))
    locales = []
    for file in files:
        match = flash_locale_extract.search(file)
        if match:
            locales.append(match.group(1))
            
    return locales

def process_flash_sim(project, simulation, sim_root, template_location,
                      verbose, jar_cmd):
    locales = get_flash_locales(project, simulation, sim_root)

    # If the triple exist (HTML, XML, SWF) exists, create the jar
    for locale in locales:
        html_file = path.join(sim_root, project,
                              '%s_%s.html' % (simulation, locale))
        xml_file = path.join(sim_root, project,
                             '%s-strings_%s.xml' % (simulation, locale))
        swf_file = path.join(sim_root, project,
                             '%s.swf' % (simulation))

        try:
            os.stat(html_file)
            os.stat(xml_file)
            os.stat(swf_file)
        except OSError, e:
            message = e.__str__() + '\n' \
                '' \
                'Missing one of 3 needed files to create ' \
                'the downloadable "' + locale + '" Flash sim:\n' \
                '   ' + html_file + '\n' \
                '   ' + xml_file + '\n' \
                '   ' + swf_file + '\n'
            raise RuntimeError(message)

        # Create the JAR file
        if verbose:
            print '   Creating %s_%s.jar' % (simulation, locale)
        process_flash_jar(project, simulation, locale,
                        sim_root, template_location,
                        jar_cmd)

