#!/usr/bin/python

from optparse import OptionParser
import os
import os.path as path
import re
from cStringIO import StringIO
import sys
import tempfile
from pprint import pprint
import shutil

def coerce_data(data):
    # Empty string becomes None
    if data == '':
        return None

    try:
        # Try to make it an int
        return int(data)
    except ValueError:
        # It won't become an int, return the original
        return data

def get_sim_dir_access_info2():
    # Look for phprunner.sh in the same directory as this script
    phprunner = os.asbpath(path.join(path.dirname(__file__), 'phprunner.sh'))
    raw_sim_info = runCommand([phprunner, 'get_sim_info.php'], True)
    raw_sim_info = ''.join(raw_sim_info)

    raw_sim_rows = raw_sim_info.split('\0\0\0')
    sim_data = []
    for raw_row in raw_sim_rows:
        sim = {}
        for field in raw_row.split('\0'):
            field_title_end = field.find(':')
            if field_title_end == -1:
                raise RuntimeError('Bad data from get_all_sim_info.php:' + field)
            field_title = field[0:field_title_end]
            field_data = coerce_data(field[field_title_end + 1:])
            sim[field_title] = field_data
        sim_data.append(sim)
    return sim_data

def runCommand(command, output=False):
    try:
        import subprocess
        if output:
            p = subprocess.Popen(command, stdout=subprocess.PIPE)
            return p.stdout.readlines()
        else:
            subprocess.check_call(command)
        return
    except ImportError:
        pass

    if output:
        import popen2
        c = re.sub('\s+', ' ', ' '.join(command))
        pipe = os.popen(c, 'r')
        return pipe.readlines()
    else:
        os.system(' '.join(command))

# Poor man's way to set up global config options quickly, this will
# be set from command line option parser
global_config = None

SIM_TYPE_JAVA = 0
SIM_TYPE_FLASH = 1

DEFAULT_LOCALE = 'en'

REGEX_JNLP_LOCALE = '^%s(_(([a-z][a-z])(_([A-Z][A-Z]))?))?.jnlp$'

def getJavaLocales(project, simulation, sim_root):
    # Get the absolute path to the project
    project_dir = path.abspath(path.join(sim_root, project))

    # Compile regex to extract the info
    jnlp_locale = re.compile(REGEX_JNLP_LOCALE % (simulation,), re.IGNORECASE)

    locales = []
    for file in os.listdir(project_dir):
        match = jnlp_locale.search(file)
        if match:
            if match.group(1) is None:
                locales.append((DEFAULT_LOCALE, DEFAULT_LOCALE, None))
            else:
                locales.append(match.group(2, 3, 5))

    return locales

def updateJarTextFile(jar_file, text_file, text):
    """Extracts the text_file from jar_file (if it exists), appends text
    it, then updates jar_file with the updates.  Note: all this
    happens in the current directory"""

    # Extract the file
    command = '%s xf %s %s' % (global_config.jar_cmd, jar_file, text_file)
    runCommand(command.split(' '))
##    subprocess.check_call(command.split(' '))

    # Append the text
    open(text_file, 'a').write(text)
    
    # Update the JAR
    command = '%s uf %s -C . %s' % (global_config.jar_cmd, jar_file, text_file)
    runCommand(command.split(' '))
##    subprocess.check_call(command.split(' '))

    # Get rid of the evidence
    os.remove(text_file)

def updateJarTextFile2(jar_file, updates):
    """Extracts the text_file from jar_file (if it exists), appends text
    it, then updates jar_file with the updates.  Note: all this
    happens in the current directory"""

    # Extract the file
    command = '%s xf %s %s' % (global_config.jar_cmd, jar_file, ' '.join(updates.keys()))
    runCommand(command.split(' '))
##    subprocess.check_call(command.split(' '))

    # Append the text
    for text_file, text in updates.items():
        open(text_file, 'a').write(text)
    
    # Update the JAR
    command = '%s uf %s -C . %s' % (global_config.jar_cmd, jar_file, ' '.join(updates.keys()))
    runCommand(command.split(' '))
##    subprocess.check_call(command.split(' '))

    # Get rid of the evidence
    for text_file in updates.keys():
        os.remove(text_file)

def doJavaSim(project, simulation, sim_root):
##    print 'Doing Java sim...'

    # Get the absolute path to the project and sim root
    project_dir = path.abspath(path.join(sim_root, project))
    sim_abs_dir = path.abspath(sim_root)

    # The 'jar' command extracts to the current directory, so create a
    # temporary directory and work from there
    temp_dir = tempfile.mkdtemp(prefix='phet_')
    cwd = os.getcwd()
    os.chdir(temp_dir)

    # Source JAR file to modify
    source_jar = '%s_all.jar' % (project,)
    source_jar_path = path.join(project_dir, source_jar)
    try:
        os.stat(source_jar_path)
    except OSError:
        source_jar = '%s.jar' % (project,)
        source_jar_path = path.join(project_dir, source_jar)
        
        # If this doesn't work, propogate the error
        os.stat(source_jar_path)

    # Loop through all the locales
    locales = getJavaLocales(project, simulation, sim_abs_dir)
    for locale, language, country in locales:
        print '   locale "%s"...' % (locale)

        if locale == DEFAULT_LOCALE:
            jar_file = '%s.jar' % (simulation,)
        else:
            jar_file = '%s_%s.jar' % (simulation, locale)

        # Copy the source JAR file to this directory
        shutil.copy(source_jar_path, jar_file)

        if True:
            updates = {}
            updates['options.properties'] = 'locale=%s\n' % (language,)
            updates['flavor.properties'] = 'flavor=%s\n' % (simulation,)
            text = 'user.language=%s\n' % (language,)
            if country is not None:
                text = text + 'user.country=%s\n' % (country,)
            updates['locale.properties'] = text
            updateJarTextFile2(jar_file, updates)

        else:
            # Old language-code-style localization scheme
            updateJarTextFile(jar_file, 
                              'options.properties',
                              'locale=%s\n' % (language,))
            
            # Tell sim what flavor it is
            updateJarTextFile(jar_file, 
                              'flavor.properties',
                              'flavor=%s\n' % (simulation,))
            
            # New locale-style localization schemp
            text = 'user.language=%s\n' % (language,)
            if country is not None:
                text = text + 'user.country=%s\n' % (country,)
                updateJarTextFile(jar_file, 
                                  'locale.properties',
                                  text)

        # JAR file is ready, move it to where it belongs
        shutil.move(jar_file, path.join(project_dir, jar_file))

    # Restore the original working directory
    os.chdir(cwd)

    # Cleanup
    os.rmdir(temp_dir)

def doFlashJar(project, simulation, locale, sim_root, template_location):
    project_dir = path.abspath(path.join(sim_root, project))

    # Create temp directory
    tdir = tempfile.mkdtemp(prefix='phet_')

    # Add extra files needed
    tt = path.join(tdir, 'flash-launcher-args.txt')
    outf = open(tt, 'w')
    outf.write('%s %s' % (simulation, locale))
    outf.close()

    # Make JAR
    ntmp = tempfile.NamedTemporaryFile(prefix='phet_jar_')
    jar_name = ntmp.name

    # Grab all the files from the project directory for this sim
    files = os.listdir(project_dir)

    # Get all the JAR files
    string_files = [file for file in files if -1 != file.find('%s-strings' % (simulation,))]

    args = ['-C %s flash-launcher-args.txt' % (tdir,), 
            '-C %s flash-launcher-template.html' % (template_location,),
            '-C %s %s.properties' % (project_dir, simulation),
            '-C %s %s.swf' % (project_dir, simulation),
            '-C %s edu' % (template_location,)
            ]
    args.extend(['-C %s %s' % (project_dir, string_file) for string_file in string_files])


    command = '%s cmf %s %s %s' % (global_config.jar_cmd, path.join(template_location, 'META-INF', 'MANIFEST.MF'), jar_name, ' '.join(args))
##    print command
    runCommand(command.split(' '))
##    result = subprocess.call(command.split(' '))
##    print 'jar result:', result

    # Copy JAR into place
##    print 'copying to Desktop "%s_%s.jar"' % (simulation, locale)
    open(path.join(project_dir, '%s_%s.jar' % (simulation, locale)), 'w').write(open(jar_name, 'r').read())

    # Delete temp files
    ntmp.close()

    # Delete temp directory
    os.remove(tt)
    os.rmdir(tdir)

def getFlashLocales(project, simulation, sim_root):
    # Grab all the files from the project directory for this sim
    files = os.listdir(path.join(sim_root, project))

    # Get locales from HTML files
    flash_locale_extract = re.compile('%s_([a-z][a-z](_([A-Z][A-Z]))?).html' % (simulation,))
    locales = []
    for file in files:
        match = flash_locale_extract.search(file)
        if match:
            locales.append(match.group(1))
            
    return locales

def doFlashSim(project, simulation, sim_root, template_location):
##    print 'Doing Flash sim...'

    locales = getFlashLocales(project, simulation, sim_root)

    # If the triple exist (HTML, XML, SWF) exists, create the jar
    for locale in locales:
        html_file = path.join(sim_root, project, '%s_%s.html' % (simulation, locale))
        xml_file = path.join(sim_root, project, '%s-strings_%s.xml' % (simulation, locale))
        swf_file = path.join(sim_root, project, '%s.swf' % (simulation))
##        if os.stat(html_file) and os.stat(xml_file) and os.stat(swf_file):
##            print 'locale veified:', locale
##        else:
##            print 'BADBADBAD'

        # Create the JAR file
        print '   Creating %s_%s.jar' % (simulation, locale)
        doFlashJar(project, simulation, locale, sim_root, template_location)

def DoMain():
    """Run from the command line"""

    usage = "usage: %prog [options] project_name sim_name"
    description = """Create the jar files, multi-purpose to be honed later. TODO: add text about keywords "ALL" """

    parser = OptionParser(usage, description=description)
    parser.add_option('-d', '--dryrun', dest='dryrun', action='store_true',
                      default=False, help="don't change anything (dry run)")
    parser.add_option('-v', '--verbose', dest='verbose', action='store_true',
                      default=False, help='verbose output')
    parser.add_option('-s', '--sim-root', dest='sim_root',
                      default='/web/htdocs/phet/sims',
                      help='alternative location of simulation directory (default: %default)')
    parser.add_option('-f', '--only-flash', dest='only_flash',
                      action='store_true', default=False,
                      help='only do Flash sims')
    parser.add_option('-j', '--only-java', dest='only_java',
                      action='store_true', default=False,
                      help='only do Flash sims')
    parser.add_option('-t', '--flash-template', dest='download_flash_template',
                      default='/web/htdocs/phet/phet-dist/flash-launcher',
                      help='alternative location of template for creating downloadable Flash JAR files (default: %defalut)')
    parser.add_option('', '--jar-cmd', dest='jar_cmd',
                      default='/web/chroot/phet/usr/local/java/bin/jar',
                      help="where to find the 'jar' executable if not running on tigercat, if it is in your path it can be merely 'jar' (default: %default)")
    (options, args) = parser.parse_args()

    #
    # Look for command line errors

    if (len(args) != 2):
        parser.error('A project and simplation must be specified, prehaps you meant "ALL"')

    if options.only_flash and options.only_java:
        parser.error('only Flash and only Java options are mutually exclusive')

    # Put the options in the global scope
    global global_config
    global_config = options

    # Generate a list of sims to work on
    simlist = []
    project, simulation = args
    if project == 'ALL':
        # Get all projects and all sims
        for d in get_sim_dir_access_info2():
            if not d['sim_is_real']:
                continue

            if options.only_flash and (d['sim_type'] != SIM_TYPE_FLASH) or \
                    options.only_java and (d['sim_type'] != SIM_TYPE_JAVA):
                continue

            simlist.append((d['sim_dirname'],
                            d['sim_flavorname'],
                            d['sim_type']))

        if len(simlist) == 0:
            print 'ERROR: no information found in databsae'
            return
    elif simulation == 'ALL':
        # Get all project on a partiular sim
        for d in get_sim_dir_access_info():
            if (d['sim_dirname'] != project):
                continue

            if not d['sim_is_real']:
                continue

            if (options.only_flash and (d['sim_type'] != 1)) or \
                    (options.only_java and (d['sim_type'] != 0)):
                continue

            simlist.append((d['sim_dirname'],
                            d['sim_flavorname'],
                            d['sim_type']))
        
        if len(simlist) == 0:
            print 'ERROR: cannot find any sims with criteria given'
            return
    else:
        for d in get_sim_dir_access_info2():
            if (d['sim_dirname'] != project) or \
                    (d['sim_flavorname'] != simulation):
                continue

            if not d['sim_is_real']:
                continue

            if (options.only_flash and (d['sim_type'] != 1)) or \
                    (options.only_java and (d['sim_type'] != 0)):
                continue

            simlist.append((d['sim_dirname'],
                            d['sim_flavorname'],
                            d['sim_type']))
        
        if len(simlist) == 0:
            print 'WARNING: cannot find a matching sim in the database ' \
                'matching the criteria, proceeding anyway'
            simlist.append((project, simulation))

    for project, simulation, type in simlist:
        print 'Processing:', project, simulation
        if type == SIM_TYPE_JAVA:
            doJavaSim(project, simulation, options.sim_root)
        elif type == SIM_TYPE_FLASH:
            doFlashSim(project, simulation, options.sim_root, options.download_flash_template)


if __name__ == '__main__':
    DoMain()

