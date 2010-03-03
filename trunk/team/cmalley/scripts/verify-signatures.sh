#!/bin/bash
##############################################################################
#
#  Verifies the digital signatures of all jars below a given directory.
#  Use this to, for example, verify all jars on the PhET website.
#
##############################################################################

# command line args
if [ "${#}" -ne "1" ] ; then
    echo "usage: ${0} directory";
    exit 1;
fi
DIR=${1}

for jarfile in `find ${DIR} -name *.jar -print`
do
  echo -n "${jarfile}: "
  jarsigner -verify ${jarfile}
done | grep SecurityException

##############################################################################
# end of file