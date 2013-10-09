#!/bin/sh

#############################################################################
# This script kicks off a full rebuild of a PhET installer that contains
# multiple languages.
#############################################################################

LOG=./installer-builder-log.txt

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================" | tee --append $LOG
echo " Rip and rebuild of multi-language PhET installers initiated by `whoami` on: " | tee --append $LOG
echo " `date`" | tee --append $LOG
echo "================================================================" | tee --append $LOG

# Execute the main build script, passing in the appropriate options.
/usr/local/php/bin/php ./bin/multi-language-installer-rip-and-rebuild.php | tee --append $LOG



