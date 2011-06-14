#!/bin/bash

# The target installation directory ${installdir} is passed as the first
# argument to the script. This script in turns creates a demo.sh script
# and substitutes the @@installdir@@ placefolder with the actual
# installation directory value

sed "s:@@installdir@@:$1:g" $1/bin/demo.sh.inc > $1/bin/demo.sh
chmod +rx $1/bin/demo.sh
rm -f $1/bin/demo.sh.inc

