#!/bin/sh

#############################################################################
# This script kicks off a full rebuild of the standard PhET installers.
#############################################################################

LOG=./custom-installer-builder-log.txt

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================" | tee --append $LOG
echo " Rip and rebuild of standard PhET installers initiated by `whoami` on: " | tee --append $LOG
echo " `date`" | tee --append $LOG
echo "================================================================" | tee --append $LOG

if [ "$1" = "--deploy" ]; then
   echo "Sims will be deployed after they are built." | tee --append $LOG
   /usr/local/php/bin/php ./bin/phet-full-rip-and-rebuild.php --deploy | tee --append $LOG
else
   echo "Sims will NOT be deployed after they are built." | tee --append $LOG
   /usr/local/php/bin/php ./bin/phet-full-rip-and-rebuild.php | tee --append $LOG
fi

