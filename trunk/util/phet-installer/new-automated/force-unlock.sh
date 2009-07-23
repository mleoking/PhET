#!/bin/sh

#############################################################################
# This script forces an unlock of the file that is used to prevent multiple
# simultaneous builds of the installers.  It should only be used if something
# went awry with the build process and the file remained locked when it
# shouldn't be, thus blocking all builds.
#############################################################################

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================" | tee --append installer-builder-log.txt
echo " Unlock forced by `whoami` on: " | tee --append installer-builder-log.txt
echo " `date`"  | tee --append installer-builder-log.txt
echo "================================================================" | tee --append installer-builder-log.txt

/usr/local/php/bin/php ./bin/force-unlock.php
