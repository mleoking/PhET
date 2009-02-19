#!/usr/bin/python

from optparse import OptionParser
import os
import sys

from phet.presets import PRESETS
from phet import websiteutils
from phet.simutils import *

VALID_PRESETS = ', '.join(PRESETS.keys())
DEFAULT_PRESET = 'tigercat'
DEFAULT_PRESET_DATA = PRESETS[DEFAULT_PRESET]

def get_base_abspath(project, sim_root, dev):
    # Dev is somewhat overloaded
    #
    # If dev is None
    #    return the project directory using
    #    a production simulations directory structure
    #    ex: sims_root/project
    # If dev is a version number, use
    #    return the project directory using
    #    a development simulations directory structure
    #    ex: sims_root/project/1.3.4
    # If dev is a keyword 'NEWEST', use
    #    return the newest project directory (by creation time) using    
    #    a development simulations directory structure
    #    ex: sims_root/project/1.3.4 (where 1.3.4 has the latest creation time)

    def cmp_version(a, b):
        y = a.split('.')
        x = b.split('.')

        if len(x) != 3 or len(y) != 3:
            raise ValueError("Invalid version")

        if x[0] != y[0]:
            return int(x[0]) - int(y[0])
        if x[1] != y[1]:
            return int(x[1]) - int(y[1])
        return int(x[2]) - int(y[2])

    project_path = path.join(sim_root, project)
    if dev is None:
        base_path = project_path
    elif dev == 'NEWEST':
        files = [f for f in os.listdir(project_path) if len(f.split('.')) == 3]
        files.sort(cmp_version)
        base_path = path.join(project_path, files[0])
    else:
        base_path = path.join(project_path, dev)
    return path.abspath(base_path)

def generate_simlist_with_database(project, simulation,
                                   sim_root, dev_version,
                                   verbose=False):
    simlist = []
    sim_info = get_java_sim_info()

    if project == 'ALL':
        if verbose:
            print '   Finding all simulations in all projects'

        # Get all projects and all sims
        for d in sim_info:
            base_directory = get_base_abspath(d['sim_dirname'],
                                              sim_root,
                                              dev_version)
            if path.exists(base_directory):
                simlist.append((d['sim_dirname'], d['sim_flavorname'],
                                base_directory))
            else:
                print 'ERROR: project root "%s" does not exist, skipping' % \
                    (base_directory,)

        if len(simlist) == 0:
            print 'ERROR: no sims matching criteria found in database'

    elif simulation == 'ALL':
        if verbose:
            print '   Finding all simulations in project "%s"' % (project,)

        # Get all project on a partiular sim
        for d in sim_info:
            if (d['sim_dirname'] != project):
                continue

            base_directory = get_base_abspath(d['sim_dirname'],
                                              sim_root,
                                              dev_version)
            if path.exists(base_directory):
                simlist.append((d['sim_dirname'], d['sim_flavorname'],
                                base_directory))
            else:
                print 'ERROR: project root "%s" does not exist, skipping' % \
                    (base_directory,)

        
        if len(simlist) == 0:
            print 'ERROR: cannot find any sims with criteria given'

    else:
        if verbose:
            print '   Finding simulation "%s" in project "%s"' % (simulation,
                                                                  project)

        for d in sim_info:
            if ((d['sim_dirname'] != project) or 
                (d['sim_flavorname'] != simulation)):
                continue

            base_directory = get_base_abspath(d['sim_dirname'],
                                              sim_root,
                                              dev_version)
            if not path.exists(base_directory):
                simlist.append((d['sim_dirname'], d['sim_flavorname'],
                                base_directory))
            else:
                print 'ERROR: project root "%s" does not exist, skipping' % \
                    (base_directory,)

        
        if len(simlist) == 0:
            base_directory = get_base_abspath(project,
                                              sim_root,
                                              dev_version)
            if path.exists(base_directory):
                print 'WARNING: this sim has not been found in the ' \
                    'database, continuing anyway'
                simlist.append((project, simulation, base_directory))
            else:
                print 'ERROR: project root "%s" does not exist, skipping' % \
                    (base_directory,)

    return simlist

def prefetch_command_line_preset():
    # Command line usage hack
    #
    # I'd like to have a 'preset' switch, but then be able to override
    # that switch with other command line switches.  Unfortunately the
    # OptionParser command doesn't support this kind of behavior, so
    # search the command line for the presence of 'preset' first, and
    # these use the presets as the default values that are fed into
    # OptionParser.
    preset = None

    # First switch wins
    for switch in sys.argv:
        if -1 != switch.find('--preset='):
            preset = switch[9:]
            if preset in VALID_PRESETS:
                return preset

    return 'tigercat'

def main():
    """Run from the command line"""

    # Set the umask so it will be correctly permissioned
    os.umask(0002)

    # See the prefetch command for details, basically I want to specify
    # preset where individual parameters can be overridden
    PRESET = prefetch_command_line_preset()
    PRESET_DATA = PRESETS[PRESET]

    # Parse command line (formally this time)
    usage = "usage: [options] project_name sim_name"
    description = """Create localized downloadable JAR files files.  This works exclusively on Java sims.  The 'project_name' and 'sim_name' parameters support a special case-sensitive command ALL which will get all projects and/or all sims within a project and process them (--use-database parameter is required for this to work).  A project of ALL will get all simulations too regardless of what it is set to."""

    parser = OptionParser(usage, description=description)
    parser.add_option('-v', '--verbose', dest='verbose', action='store_true',
                      default=PRESET_DATA['verbose'],
                      help='verbose output (default: %default)')
    parser.add_option('-q', '--quiet', dest='verbose', action='store_false',
                      help='quiet, only output errors and warnings')
    parser.add_option('-p', '--preset',
                      action='store', type='string', dest='preset',
                      default=PRESET,
                      help='preset parameters for a particular environment, ' \
                          'further specifying any other parameter will ' \
                          'override the presets. Valid presets are: ' + \
                          VALID_PRESETS + ' (default: %default)')
    parser.add_option('-l', '--legacy',
                      action='store_true', dest='legacy_support',
                      help='generate legacy files (e.g. options.properties)')
    parser.add_option('-m', '--modern',
                      action='store_false', dest='legacy_support',
                      default=PRESET_DATA['legacy_support'],
                      help='DO NOT generate legacy files (default: %default)')
    parser.add_option('', '--use-database',
                      action='store_true', dest='use_database',
                      default=PRESET_DATA['use_database'],
                      help='check the production database to verify the sim ' \
                          'existance or use the ALL shortcuts ' \
                          '(default: %default)')
    parser.add_option('', '--no-use-database',
                      action='store_false', dest='use_database',
                      default=PRESET_DATA['use_database'],
                      help='DO NOT check the production database')
    parser.add_option('-d', '--dev', dest='dev_version',
                      default=PRESET_DATA['dev_version'],
                      help='use development directory structure, ' \
                          'must specify the keyword NEWEST or a version ' \
                          'number of the form 1.00.00')
    parser.add_option('', '--production',
                      action='store_const', const=None, dest='dev_version',
                      default=PRESET_DATA['dev_version'],
                      help='use production directory structure')
    parser.add_option('', '--jar-cmd', dest='jar_cmd',
                      default=PRESET_DATA['jar_cmd'],
                      help="where to find the 'jar' executable if not " \
                          "running on tigercat, if it is in your path it " \
                          "can be merely 'jar' (default: %default)")
    parser.add_option('-s', '--sim-root', dest='sim_root',
                      default=PRESET_DATA['sim_root'],
                      help='location of simulation directory (ex: on ' \
                          'tigercat: "/web/htdocs/phet/sims", on spot: ' \
                          '"/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev" ' \
                          '(default: %default)')

    # Put the options in the global scope
    (options, args) = parser.parse_args()

    #
    # Look for command line errors

    if (len(args) != 2):
        parser.error('A project and simplation must be specified, ' \
                     'prehaps you meant "ALL"')

    if not path.exists(options.sim_root):
        print 'ERROR: sim root "%s" does not exist"' % (options.sim_root,)
        return 1

    # Get the processed options
    options = options
    if options.preset and (options.preset not in VALID_PRESETS):
        parser.error('No preset exists for "%s"' % options.preset)

    # Get the project and simulation
    project, simulation = args
    if (((project == 'ALL') or (simulation == 'ALL')) and 
        (not options.use_database)):
        parser.error('To use the ALL keyword you must specify --use-database')

    if options.verbose:
        print 'Options:'
        print '   verbose:', options.verbose
        print '   preset environment:', options.preset
        print '   legacy support:', options.legacy_support
        print '   use database:', options.use_database
        if options.dev_version is None:
            print '   directory structure: production'
        else:
            print '   directory structure: development, version=', \
                options.dev_version
        print '   sim root:', options.sim_root
        print '   jar cmd:', options.jar_cmd


    # Generate a list of sims to work on
    if options.verbose:
        print 'Determining which sim(s) to work on'

    simlist = []
    project, simulation = args
    if not options.use_database:
        print options.dev_version
        base_directory = get_base_abspath(project,
                                          options.sim_root,
                                          options.dev_version)
        print base_directory
        if path.exists(base_directory):
            simlist.append((project, simulation, base_directory))
        else:
            print 'ERROR: project root "%s" does not exist, skipping' % \
                (base_directory,)
            
    else:
        simlist = generate_simlist_with_database(project,
                                                 simulation,
                                                 options.sim_root,
                                                 options.dev_version,
                                                 options.verbose)

    if len(simlist) == 0:
        print 'ERROR: no sims to process'

    try:
        for project, simulation, abspath in simlist:
            ## HERE: generate the directory (production or dev) and
            ## verify that it exists
            
            if options.verbose:
                print 'Processing project "%s", simulation "%s",' % \
                    (project, simulation)
            try:
                process_java_sim(project,
                                 simulation,
                                 abspath,
                                 legacy_support=options.legacy_support,
                                 jar_cmd=options.jar_cmd,
                                 verbose=options.verbose)

            except RuntimeError, r:
                print '***************************'
                print '** ERROR: in project "%s", ' \
                    'simulation "%s"' % \
                    (project, simulation)
                print '**'
                print '**',
                print '\n** '.join(r.message.split('\n'))
                print '** SKIPPING this simulation ' \
                    '(no further processing on this sim)'
                print '***************************'

    except KeyboardInterrupt:
        if options.verbose:
            print 'Aborting...'


if __name__ == '__main__':
    try:
        retval = main()
        websiteutils.clear_cache()
    except RuntimeError, r:
        print
        print 'ERROR:', r


