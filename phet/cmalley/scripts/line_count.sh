#!/bin/bash
#####################################################################
#
# Counts the lines of Java source code in a specified directory.
# Sorts the files from largest to smallest.
# Usage: line_count directory
#
#####################################################################

if [ ${#} != 1 ];
then
    echo "usage: `basename ${0}` directory";
else
    ( wc -l `find ${1} -name *.java -print` | sort -r );
fi

#####################################################################
# end of file