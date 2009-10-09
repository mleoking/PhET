#!/bin/bash
#====================================================================================
#
# Creates the default directories and files needs by every Java simulation project.
#
#====================================================================================

# command line args
if [ "${#}" -ne "2" ] ; then
    echo "usage: ${0} trunk_dir project_name";
    exit 1;
fi
PROJECT_NAME=${1}
TRUNK_DIR=${2}
if [ ! -e ${TRUNK_DIR} ] ; then
    echo "trunk directory does not exist: ${TRUNK_DIR}";
    exit 1;
fi

# constants
PROJECT_DIR=${TRUNK_DIR}/simulations-java/simulations/${PROJECT_NAME}
if [ -e ${PROJECT_DIR} ] ; then
   echo "project directory already exists: ${PROJECT_DIR}";
   exit 1;
fi

# directories
mkdir -p ${PROJECT_DIR}
mkdir -p ${PROJECT_DIR}/assets
mkdir -p ${PROJECT_DIR}/data/${PROJECT_NAME}/images
mkdir -p ${PROJECT_DIR}/data/${PROJECT_NAME}/localization
mkdir -p ${PROJECT_DIR}/deploy
mkdir -p ${PROJECT_DIR}/doc
mkdir -p ${PROJECT_DIR}/screenshots
mkdir -p ${PROJECT_DIR}/src

# files
touch ${PROJECT_DIR}/changes.txt
touch ${PROJECT_DIR}/${PROJECT_NAME}-build.properties
touch ${PROJECT_DIR}/data/${PROJECT_NAME}/images/license.txt
touch ${PROJECT_DIR}/data/${PROJECT_NAME}/localization/${PROJECT_NAME}-strings.properties

#====================================================================================
# end of file