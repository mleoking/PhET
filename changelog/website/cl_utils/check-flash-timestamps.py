#!/usr/bin/python

import os
import os.path

from pyphetutils.siminfo import *
from pyphetutils.simutils import *

def main():
    """Run from the command line"""

    base = path.join('..', '..', 'sims')
    sim_info = get_sim_info()
    for sim in sim_info:
        if sim['sim_dirname'] is None:
            continue
        if sim['sim_type'] is not SIM_TYPE_FLASH:
            continue

        locales = get_flash_locales(sim['sim_dirname'],
                                  sim['sim_flavorname'],
                                  base)
        print sim['sim_name'], locales

        for locale in locales:
            dir = path.join(base, sim['sim_dirname'])
            html = path.join(dir, '%s_%s.html' % (sim['sim_flavorname'], locale))
            xml = path.join(dir, '%s-strings_%s.xml' % (sim['sim_flavorname'], locale))
            swf = path.join(dir, '%s.swf' % (sim['sim_flavorname']))
            jar = path.join(dir, '%s_%s.jar' % (sim['sim_flavorname'], locale))
            try:
                html_stat = os.stat(html)
                xml_stat = os.stat(xml)
                swf_stat = os.stat(swf)
                jar_stat = os.stat(jar)
            except OSError:
                print 'ERROR: directory structre is messed up in %s, locale %s' % (sim['sim_dirname'],locale)
                print 'Skipping this check'
                continue

            # Check timestamps of jar
            if (html_stat.st_mtime > jar_stat.st_mtime) or \
                    (html_stat.st_ctime > jar_stat.st_ctime) or \
                    (xml_stat.st_mtime > jar_stat.st_mtime) or \
                    (xml_stat.st_ctime > jar_stat.st_ctime) or \
                    (swf_stat.st_mtime > jar_stat.st_mtime) or \
                    (swf_stat.st_ctime > jar_stat.st_ctime):
                print 'needs updating:', locale
            continue
            print locale
            print 'html:'
##            #print html_stat.st_atime, jar_stat.st_atime, (html_stat.st_atime > jar_stat.st_atime)
##            print html_stat.st_ctime, jar_stat.st_ctime, (html_stat.st_ctime > jar_stat.st_ctime)
##            print html_stat.st_mtime, jar_stat.st_mtime, (html_stat.st_mtime > jar_stat.st_mtime)
##            print 'xml:'
##            #print xml_stat.st_atime, jar_stat.st_atime, (xml_stat.st_atime < jar_stat.st_atime)
##            print xml_stat.st_ctime, jar_stat.st_ctime, (xml_stat.st_ctime > jar_stat.st_ctime)
##            print xml_stat.st_mtime, jar_stat.st_mtime, (xml_stat.st_mtime > jar_stat.st_mtime)
##            print 'swf:'
            #print swf_stat.st_atime, jar_stat.st_atime, (swf_stat.st_atime < jar_stat.st_atime)
##            print swf_stat.st_ctime, jar_stat.st_ctime, (swf_stat.st_ctime > jar_stat.st_ctime)
##            print swf_stat.st_mtime, jar_stat.st_mtime, (swf_stat.st_mtime > jar_stat.st_mtime)

        return

if __name__ == '__main__':
    main()
