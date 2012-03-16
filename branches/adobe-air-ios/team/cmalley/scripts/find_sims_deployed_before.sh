#!/bin/bash
#------------------------------------------------------------------------------------------
#
# Finds all sim projects that were last deployed before a specified SVN revison number.
# Each such project name is printed to stdout, along with its revision number
#
# Author: Chris Malley (cmalley@pixelzoom.com)
#
#------------------------------------------------------------------------------------------

# check command line args
if [ ${#} -ne "2" ]
then
	echo "usage: ${0} revision trunk_path"
	exit 1
fi

# parse command line args
BASELINE_REVISION=${1}
TRUNK=${2}

# visit each type of simulation (Java, Flash, Flex)
DIRECTORIES="simulations-java simulations-flash simulations-flex"
for DIR in ${DIRECTORIES}
do
    echo "$DIR ================================"
    
    PROPERTIES_FILES=`ls ${TRUNK}/${DIR}/simulations/*/data/*/*.properties`
    for FILE in ${PROPERTIES_FILES}
    do
	    PROJECT_NAME=`echo ${FILE} | cut -d / -f 10`
	    PROPERTIES_FILE_BASENAME=`echo ${FILE} | cut -d / -f 11 | cut -d . -f 1`
	    
	    # only examine project properties files
	    if [ ${PROJECT_NAME} = ${PROPERTIES_FILE_BASENAME} ] 
	    then
	        # parse out the revision number, taking care to handle DOS-to-UNIX file conversion
	        REVISION=`cat ${FILE} | tr '\r' '\n' | grep version.revision | cut -d = -f 2`
	        
	        # verify that we got a number
	        echo ${REVISION} | grep "[^0-9]" > /dev/null 2>&1
            if [ "$?" -eq "0" ]; then
                # If the grep found something other than 0-9 then it's not an integer.
                echo "${PROJECT_NAME}: Sorry, wanted a number, got version.revision=<${REVISION}>"
            else
                # compare the revision
	            if [ ${REVISION} -lt ${BASELINE_REVISION} ]
	            then
	                echo "${PROJECT_NAME} ${REVISION}"
	            fi
	        fi
	    fi
    done
done

#------------------------------------------------------------------------------------------
# end of file