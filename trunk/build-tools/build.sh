#!/bin/sh
#----------------------------------------------------------------------------------------------
#
# build.sh -
#
# A shell script for building PhET simulations.
# See build.xml for a complete list of build targets and properties.
#
# Usage:
#
#    build : builds the default ant target
#    build target : builds the specified ant target
#    build target sim.name : builds the specified ant target with sim.name properties set
#       
#----------------------------------------------------------------------------------------------

ROOT_DIR=`dirname $0`
export ANT_HOME=${ROOT_DIR}/contrib/apache-ant
export ANT_OPTS=-Xmx640m
PATH=${ANT_HOME}/bin:${PATH}

cd ${ROOT_DIR}

# JAVA_HOME can be set automatically on Mac, is required on other platforms
if [ "${JAVA_HOME}" = "" ]; then
    if [ `uname` = "Darwin" ]; then
        export JAVA_HOME=/Library/Java/Home
        echo "Mac detected, assuming Java home is $JAVA_HOME"
    else
        echo "The environment variable JAVA_HOME must be set to the location of a valid JDK."
        exit 1
    fi      
fi

# Invoke ant
if [ $# = 0 ]; then
    ant
elif [ $# = 1 ]; then
    ant $1
elif [ $# = 2 ]; then
    ant $1 -Dsim.name=$2
else
    echo "Unknown command line arguments."
    exit 1
fi

#----------------------------------------------------------------------------------------------
