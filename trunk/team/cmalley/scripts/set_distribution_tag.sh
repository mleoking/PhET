#!/bin/bash
#--------------------------------------------------------------------------
#
# Sets the distribution tag for an existing simulation JAR.
# This works for both Java and Flash sims.
# You will be prompted for the keystore password.
#
# General usage:
#
# set_distribution_tag.sh jar project tag keystore alias
#
# Example usage:
#
# set_distribution_tag.sh glaciers_en.jar glaciers KSU phet-certificate.p12 ***
#
#--------------------------------------------------------------------------

# validate command line syntax
if [ "${#}" -ne "5" ] ; then
    echo "usage: ${0} jar project tag_value keystore alias";
    exit 1;
fi

# use symbolic names for command line args
JAR=${1}
PROJECT=${2}
TAG_VALUE=${3}
KEYSTORE=${4}
ALIAS=${5}

# constants
TAG_NAME=distribution.tag
TMPFILE=/tmp/phet$$

echo -n "**** determining sim type: "
PROPERTIES=${PROJECT}/${PROJECT}.properties
FOUND=`jar tvf ${JAR} | grep ${PROPERTIES}`
if [ -z "${FOUND}" ] ; then
    echo "Flash"
    PROPERTIES=${PROJECT}.properties
else
    echo "Java"
fi

echo "**** extracting properties file from JAR"
jar xvf ${JAR} ${PROPERTIES}

echo "**** removing any existing tags"
grep -v ${TAG_NAME} ${PROPERTIES} > ${TMPFILE}
mv ${TMPFILE} ${PROPERTIES}

echo "**** adding new tag to end of properties file"
echo "${TAG_NAME}=${TAG_VALUE}" >> ${PROPERTIES}

echo "**** updating JAR with new properties file"
jar uvf ${JAR} ${PROPERTIES}

echo "**** signing JAR (jarsigner prompts for password!)"
jarsigner -keystore ${KEYSTORE} -storetype pkcs12 ${JAR} ${ALIAS}

echo "**** running JAR, look for tag in About dialog"
java -jar ${JAR}

#--------------------------------------------------------------------------
# end of file
