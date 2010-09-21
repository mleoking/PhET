#!/bin/sh

#############################################################################
# This script kicks off a full rip of the Arabic version of the web site and
# a rebuild of the special KSU installer.
#############################################################################

LOG=./custom-installer-builder-log.txt

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================" | tee --append $LOG
echo " Rip and rebuild of KSU installer initiated by `whoami` on: " | tee --append $LOG
echo " `date`" | tee --append $LOG
echo "================================================================" | tee --append $LOG

/usr/local/php/bin/php ./bin/ksu-full-rip-and-rebuild.php | tee --append $LOG

