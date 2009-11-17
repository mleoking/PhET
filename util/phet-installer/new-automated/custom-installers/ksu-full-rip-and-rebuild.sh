#!/bin/sh

#############################################################################
# This script kicks off a full rip of the Arabic version of the web site and
# a rebuild of the special KSU installer.
#############################################################################

LOG=./ksu-installer-builder-log.txt

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

# Make sure we are starting from the correct directory.
cd /web/htdocs/phet/installer-builder/ksu-special-installer

echo "================================================================" | tee --append $LOG
echo " Rip and rebuild of KSU installer initiated by `whoami` on: " | tee --append $LOG
echo " `date`" | tee --append $LOG
echo "================================================================" | tee --append $LOG

/usr/local/php/bin/php ./bin/full-rip-and-rebuild.php | tee --append $LOG

