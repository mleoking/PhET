#!/bin/sh

#############################################################################
# This script kicks off a full rip of the custom installer for Young and
# Freedman.
#############################################################################

LOG=./custom-installer-builder-log.txt

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================" | tee --append $LOG
echo " Rip and rebuild of YF installer initiated by `whoami` on: " | tee --append $LOG
echo " `date`" | tee --append $LOG
echo "================================================================" | tee --append $LOG

/usr/local/php/bin/php ./bin/yf-full-rip-and-rebuild.php | tee --append $LOG

