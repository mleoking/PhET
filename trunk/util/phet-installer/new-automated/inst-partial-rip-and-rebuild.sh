#!/bin/sh

#############################################################################
# This script kicks off a partial rip of the web site, downloading new
# versions of the files needed for the execution of the specified simulation,
# and then rebuilds the installers based on these files and on the locally
# cached mirror of the rest of the web site.
#############################################################################

LOG=./installer-builder-log.txt

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================" | tee --append $LOG
echo " Partial rip and rebuild operation performed by `whoami` on: " | tee --append $LOG
echo " `date`"  | tee --append $LOG
echo "================================================================" | tee --append $LOG

/usr/local/php/bin/php ./bin/partial-rip-and-rebuild.php $1 $2 | tee --append $LOG

