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
# remove '-' from project name
PACKAGE_NAME=edu.colorado.phet.`echo ${PROJECT_NAME} | sed 's/-//g'`

# directories
mkdir -p ${PROJECT_DIR}
mkdir -p ${PROJECT_DIR}/assets
mkdir -p ${PROJECT_DIR}/data/${PROJECT_NAME}/images
mkdir -p ${PROJECT_DIR}/data/${PROJECT_NAME}/localization
mkdir -p ${PROJECT_DIR}/deploy
mkdir -p ${PROJECT_DIR}/doc
mkdir -p ${PROJECT_DIR}/screenshots

# source dir, replace '.' with '/' in package name
SRC_DIR=${PROJECT_DIR}/src/`echo ${PACKAGE_NAME} | sed 's/\./\//g'`
mkdir -p ${SRC_DIR}

# files
touch ${PROJECT_DIR}/changes.txt
touch ${PROJECT_DIR}/data/${PROJECT_NAME}/images/license.txt

# build properties skeleton
BUILD_PROPERTIES=${PROJECT_DIR}/${PROJECT_NAME}-build.properties
touch ${BUILD_PROPERTIES}
echo "# build properties for ${PROJECT_NAME}" >> ${BUILD_PROPERTIES}
echo "project.depends.data=data" >> ${BUILD_PROPERTIES}
echo "project.depends.source=src" >> ${BUILD_PROPERTIES}
echo "project.depends.lib=phetcommon piccolo-phet" >> ${BUILD_PROPERTIES}
echo "project.flavor.FLAVOR.mainclass=${PACKAGE_NAME}.X" >> ${BUILD_PROPERTIES}
echo "project.flavor.FLAVOR.args=" >> ${BUILD_PROPERTIES}

# project properties
PROJECT_PROPERTIES=${PROJECT_DIR}/data/${PROECT_NAME}/${PROJECT_NAME}.properties
touch ${PROJECT_PROPERTIES}
echo "version.major=0" >> ${PROJECT_PROPERTIES}
echo "version.minor=00" >> ${PROJECT_PROPERTIES}
echo "version.dev=00" >> ${PROJECT_PROPERTIES}
echo "version.revision=0" >> ${PROJECT_PROPERTIES}
echo "version.timestamp=0" >> ${PROJECT_PROPERTIES}

# English strings skeleton
STRINGS_FILE=${PROJECT_DIR}/data/${PROJECT_NAME}/localization/${PROJECT_NAME}-strings.properties
touch ${STRINGS_FILE}
echo "# English strings for ${PROJECT_NAME}" >> ${STRINGS_FILE}
echo "FLAVOR.name=" >> ${STRINGS_FILE}

# credits skeleton
CREDITS=${PROJECT_DIR}/data/${PROECT_NAME}/credits.txt
touch ${CREDITS}
echo "phet-credits lead-design=X software-development=X design-team=X, X, X interviews=X, X, X" >> ${CREDITS}

# done
echo "Done. Project lives in ${PROJECT_DIR}"

#====================================================================================
# end of file