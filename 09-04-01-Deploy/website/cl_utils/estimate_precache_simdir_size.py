#!/usr/bin/python

import sys
import os
import os.path as path
import re

if len(sys.argv) != 2:
    print 'usage: %s sims_dir' % (path.basename(sys.argv[0]),)
    print 'Estimate size of sims dir with localization'
    sys.exit(-1)

# Get the sims dir
simdir = sys.argv[1]

# Get regexes ready
re_loc_jnlp = re.compile('.*_..\.jnlp')
re_jar = re.compile('.*\.jar')
re_xml = re.compile('.*\.xml')

# Get lists ready
total_raw_size = []
total_bigger_size = []

# Walk the sims dir
for root, dirs, files in os.walk(simdir):
    # Skip any projects
    try:
        dirs.remove('test-project')
    except ValueError:
        # Ignore if test-project isn't present
        pass

    # Determine if this is a Java or Flash sim by file detection
    TYPE_UNKNOWN = 0
    TYPE_JAVA = 1
    TYPE_FLASH = 2
    sim_type = TYPE_UNKNOWN
    for file in files:
        if re_xml.search(file):
            print root, 'is a Flash sim'
            sim_type = TYPE_FLASH
            break
        elif re_jar.search(file):
            print root, 'is a Java sim'
            sim_type = TYPE_JAVA
            break

    if sim_type == TYPE_UNKNOWN:
        print 'WARNING:', root, 'is UNKNOWN type, skipping'
        continue

    if sim_type == TYPE_JAVA:
        # Count all JNLP files
        jnlp_files = len([d for d in files if re_loc_jnlp.match(d)])

        # Biggest JAR file found per sim, None if not found
        biggest_jar_file = None
        biggest_jar_size = 0
        jars = [(d, path.getsize(path.join(root, d)))  for d in files if re_jar.match(d)]
        if jars:
            jars.sort(lambda a, b: cmp(a[1], b[1]))
            biggest_jar_file, biggest_jar_size = jars[-1]

        # Compute the sizes
        raw_size = sum(path.getsize(path.join(root, name)) for name in files)
        bigger_size = raw_size + (jnlp_files * (biggest_jar_size))

        # Add the sizes to the list that we're keeping track of
        total_raw_size.append(raw_size)
        total_bigger_size.append(bigger_size)

        # Enquiring minds want to know...
        print root, "is", raw_size, "bytes big, has", jnlp_files, "JNLP files",
        if biggest_jar_file is not None:
            print "bigest jar: %s %d" % (biggest_jar_file, biggest_jar_size)
        else:
            print "no jar found"

    elif sim_type == TYPE_FLASH:
        # Count number of XML files
        xml_files = len([d for d in files if re_xml.match(d)])

        # Multiply it by the the "average" download size
        biggest_jar_size = 192000

        # Compute the sizes
        raw_size = sum(path.getsize(path.join(root, name)) for name in files)
        bigger_size = raw_size + (xml_files * (biggest_jar_size))

        # Add the sizes to the list that we're keeping track of
        total_raw_size.append(raw_size)
        total_bigger_size.append(bigger_size)

        # Enquiring minds want to know...
        print root, "is", raw_size, "bytes big, has", xml_files, "XML files"


# Report results
size = float(sum(total_raw_size))
print 'Total raw size:    %10.0fb % 10.1fk % 6.1fM % 4.2fG' % (size, (size / (pow(1024,1))), (size / (pow(1024,2))), (size / (pow(1024,3))))
size = float(sum(total_bigger_size))
print 'Total bigger size: %10.0fb % 10.1fk % 6.1fM % 4.2fG' % (size, (size / (pow(1024,1))), (size / (pow(1024,2))), (size / (pow(1024,3))))

