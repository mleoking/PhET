#!/usr/bin/python

from optparse import OptionParser
import os

from pyphetutils.siminfo import get_sim_info
from pyphetutils import siteutils
from pyphetutils.simutils import *

# These defaults are for running on tigercat
DEFAULT_SIM_ROOT = '/web/chroot/phet/usr/local/apache/htdocs/sims'
DEFAULT_FLASH_TEMPLATE = '/web/chroot/phet/usr/local/apache/htdocs/phet-dist/flash-launcher'
DEFAULT_JAR_COMMAND = '/web/chroot/phet/usr/local/java/bin/jar'
DEFAULT_VERBOSE = False
DEFAULT_FLASH_ONLY = False
DEFAULT_JAVA_ONLY = False

def main():
    """Run from the command line"""

    # Set the umask so it will be correctly permissioned
    os.umask(0002)

    usage = "usage: %prog [options] project_name sim_name"
    description = """Create the jar files, multi-purpose to be honed later. TODO: add text about keywords "ALL" """

    parser = OptionParser(usage, description=description)
    parser.add_option('-v', '--verbose', dest='verbose', action='store_true',
                      default=DEFAULT_VERBOSE, help='verbose output')
    parser.add_option('-s', '--sim-root', dest='sim_root',
                      default=DEFAULT_SIM_ROOT,
                      help='alternative location of simulation directory ' \
                          '(default: %default)')
    parser.add_option('-f', '--flash-only', dest='flash_only',
                      action='store_true', default=DEFAULT_FLASH_ONLY,
                      help='only do Flash sims')
    parser.add_option('-j', '--java-only', dest='java_only',
                      action='store_true', default=DEFAULT_JAVA_ONLY,
                      help='only do Flash sims')
    parser.add_option('-t', '--flash-template', dest='download_flash_template',
                      default=DEFAULT_FLASH_TEMPLATE,
                      help='alternative location of template for creating ' \
                          'downloadable Flash JAR files (default: %defalut)')
    parser.add_option('', '--jar-cmd', dest='jar_cmd',
                      default=DEFAULT_JAR_COMMAND,
                      help="where to find the 'jar' executable if not " \
                          "running on tigercat, if it is in your path it " \
                          "can be merely 'jar' (default: %default)")

    # Put the options in the global scope
    (options, args) = parser.parse_args()

    #
    # Look for command line errors

    if (len(args) != 2):
        parser.error('A project and simplation must be specified, prehaps you meant "ALL"')

    if options.flash_only and options.java_only:
        parser.error('only Flash and only Java options are mutually exclusive')


    if options.verbose:
        print 'Options:'
        print '   verbose:', options.verbose
        print '   sim root:', options.sim_root
        print '   jar cmd:', options.jar_cmd
        print '   flash template dir:', options.download_flash_template
        if options.java_only:
            print '   limit to type: Java'
        elif options.flash_only:
            print '   limit to type: Flash'
        else:
            print '   limit to type: No restrictions'

    # Generate a list of sims to work on
    if options.verbose:
        print 'Determining which sim(s) to work on'
    simlist = []
    flash_project_found = False
    project, simulation = args
    if project == 'ALL':
        if options.verbose:
            print '   Finding all simulations in all projects'
        # Get all projects and all sims
        for d in get_sim_info():
            if not d['sim_is_real']:
                continue

            if options.flash_only and (d['sim_type'] != SIM_TYPE_FLASH) or \
                    options.java_only and (d['sim_type'] != SIM_TYPE_JAVA):
                continue

            simlist.append((d['sim_dirname'],
                            d['sim_flavorname'],
                            d['sim_type']))

        if len(simlist) == 0:
            print 'ERROR: no sims matching criteria found in database'
            return
    elif simulation == 'ALL':
        if options.verbose:
            print '   Finding all simulations in project "%s"' % (project,)

        # Get all project on a partiular sim
        for d in get_sim_info():
            if (d['sim_dirname'] != project):
                continue

            if not d['sim_is_real']:
                continue

            if (options.flash_only and (d['sim_type'] != 1)) or \
                    (options.java_only and (d['sim_type'] != 0)):
                continue

            simlist.append((d['sim_dirname'],
                            d['sim_flavorname'],
                            d['sim_type']))
        
        if len(simlist) == 0:
            print 'ERROR: cannot find any sims with criteria given'
            return
    else:
        if options.verbose:
            print '   Finding simulation "%s" in project "%s"' % (simulation, project)

        for d in get_sim_info():
            if (d['sim_dirname'] != project) or \
                    (d['sim_flavorname'] != simulation):
                continue

            if not d['sim_is_real']:
                continue

            if (options.flash_only and (d['sim_type'] != 1)) or \
                    (options.java_only and (d['sim_type'] != 0)):
                continue

            simlist.append((d['sim_dirname'],
                            d['sim_flavorname'],
                            d['sim_type']))
        
        if len(simlist) == 0:
            print 'WARNING: cannot find sim in the database matching the ' \
                'criteria (is it new?)'
            print '         proceeding anyway'
            try:
                sim_type = detect_sim_type(project,simulation,options.sim_root)
            except RuntimeError, r:
                print 'ERROR: %s' % (r,)
                return
            if sim_type == SIM_TYPE_JAVA:
                if options.verbose:
                    print '   Auto detected a Java simulation type'
            elif sim_type == SIM_TYPE_FLASH:
                if options.verbose:
                    print '   Auto detected a Flash simulation type'
            simlist.append((project, simulation, sim_type))

    # Scan the simulations to see if there are any Flash sims,
    # If so, make sure the flash template directory exists
    if 0 < len([type for p, s, type in simlist if type == SIM_TYPE_FLASH]):
        try:
            os.stat(options.download_flash_template)
        except OSError:
            print 'ERROR: Flash template directory "%s" does not exist' % \
                (options.download_flash_template,)
            return 1

        try:
            os.stat(path.join(options.download_flash_template,
                              'META-INF',
                              'MANIFEST.MF'))
        except OSError:
            print 'ERROR: Flash template directory "%s" looks invalid' % \
                (options.download_flash_template,)
            print '       proceeding anyway'

    try:
        os.stat(options.sim_root)
    except OSError:
        print 'ERROR: sim root "%s" does not exist"' % (options.sim_root,)
        return 1

    try:
        for project, simulation, type in simlist:
            if project in []: #SKIP_PROJECTS:
                print 'SKIPPING project "%s", simulation "%s",' \
                    ' type "%s"' % \
                    (project, simulation, SIM_TYPE[type])
                print '   it probably does not have a project_all.jar file'
                print '   if this is not true, edit this file and remove'
                print '   the explicit skipping'
                continue

            if options.verbose:
                print 'Processing project "%s", simulation "%s",' \
                    ' type "%s":' % \
                    (project, simulation, SIM_TYPE[type])
            try:
                if type == SIM_TYPE_JAVA:
                    process_java_sim(project, simulation,
                                     options.sim_root,
                                     jar_cmd=options.jar_cmd,
                                     verbose=options.verbose)
                elif type == SIM_TYPE_FLASH:
                    process_flash_sim(project, simulation,
                                      options.sim_root,
                                      options.download_flash_template,
                                      jar_cmd=options.jar_cmd,
                                      verbose=options.verbose)
            except RuntimeError, r:
                print '***************************'
                print '** ERROR: in project "%s", ' \
                    'simulation "%s", type "%s"' % \
                    (project, simulation, SIM_TYPE[type])
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
        siteutils.clear_cache()
    except RuntimeError, r:
        print
        print 'ERROR:', r


