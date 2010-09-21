#!/bin/bash
##############################################################################
#
#  Verifies the digital signatures of all jars below a given directory.
#  Use this to, for example, verify all jars on the PhET website.
#
#  According to the man page for 'jarsigner -verify', a success verification
#  will print "jar verified".  So we look for any output that is not this.
#  If the jar is not signed, "jar is unsigned" will be printed.
#  If the jar is signed and verification fails, a SecurityException is 
#  thrown, caught by jarsigner, and printed.
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
done | grep -v "jar verified"

##############################################################################
# end of file