#!/bin/sh

#############################################################################
# This script deploys the installers to the production server by copying them
# from a local temporary directory.
#############################################################################

SOURCE_DIR=./temp/installer-output/
LOG=installer-builder-log.txt

#----------------------------------------------------------------------------
# Verify that the files exist and that the user really wants to deploy
# them.
#----------------------------------------------------------------------------
echo ""
echo "The following is a list of the installer files in the source directory"
ls -l $SOURCE_DIR
echo ""
echo "Please verify that that list appears complete and that the dates are correct."
read -p "Do you wish to deploy these installers to the production web site (y/n)? "
if [ $REPLY != "y" ]
then
    echo "Aborting deployment at user's request."
    echo ""
    exit
fi

echo "================================================================" | tee --append $LOG
echo " Deployment of installers performed by `whoami` on: " | tee --append $LOG
echo " `date`"  | tee --append $LOG
echo "================================================================" | tee --append $LOG

/usr/local/php/bin/php ./bin/deploy-installers.php $1 | tee --append $LOG


