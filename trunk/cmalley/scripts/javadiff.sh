#!/bin/bash
#####################################################################
#
# Diff's all of the Java source files under 2 directories and reports 
# which ones are different or missing.  Use this to compare the source
# files in two project directories checked out of CVS.
#
# This script was written when SourceForge reported that their 
# CVS repository was possibly corrupted.
#
# Usage:  javadiff directory1 director2
# where directory1 contains the files that you know are OK,
# and directory2 is contains the files you wish to verify.
#
#####################################################################

dir1=$1
dir2=$2
for file1 in `find ${dir1} -name *.java  -print`; 
do
    if test -f ${file1};
    then
        file2=`echo ${file1} | sed -e 1s/${dir1}/${dir2}/`
        if ! test -e ${file2};
        then
            echo "${file2} is missing";
        else
            diff ${file1} ${file2} > /dev/null
            if [ $? -ne 0 ];
            then
                echo "${file2} is different" 
            fi;
        fi;
    fi;
done;

#####################################################################
# end of file
