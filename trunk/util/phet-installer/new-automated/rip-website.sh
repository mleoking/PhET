#!/bin/sh

#############################################################################
# This is a test script that will rip the web site to a temporary directory.
# This script was developed for testing and is not a part of the regular
# installer build process.  It is used to create a ripped image of the web
# site which can then be used to test the installer builder.  This is
# needed because the regular process generally deletes the ripped web site
# when done, so repeating it takes a long time (on the order of 1/2 hour).
# jblanco, 6/10/2008
#############################################################################

LOG_FILE_NAME=./rip-log.txt

#==============================================================================
# Main body of this script.
#==============================================================================

echo "================================================================" | tee --append $LOG_FILE_NAME
echo " Ripping web site on: " | tee --append $LOG_FILE_NAME
date | tee --append $LOG_FILE_NAME
echo "================================================================" | tee --append $LOG_FILE_NAME

#------------------------------------------------------------------------------
# Remove the current copy of the web site if it exists.
#------------------------------------------------------------------------------

echo "Started removing old web site at `date`" | tee --append $LOG_FILE_NAME

/usr/local/php/bin/php build-install.php --remove-website-copy | tee --append $LOG_FILE_NAME

if [ "$?" -ne "0" ]; then
echo "Error removing old web site." | tee --append $LOG_FILE_NAME
exit 1
fi

echo "Finished removing old web site at `date`" | tee --append $LOG_FILE_NAME


#------------------------------------------------------------------------------
# Rip the web site into a local copy.
#------------------------------------------------------------------------------

echo "Started ripping web site at `date`" | tee --append $LOG_FILE_NAME

/usr/local/php/bin/php build-install.php --rip-website | tee --append $LOG_FILE_NAME

if [ "$?" -ne "0" ]; then
  echo "Error ripping web site." | tee --append $LOG_FILE_NAME
  exit 1
fi

echo "Finished ripping web site at `date`" | tee --append $LOG_FILE_NAME

#------------------------------------------------------------------------------
# Download the additional sim resources that can't be obtained directly by
# ripping the web site.
#------------------------------------------------------------------------------

echo "Started downloading additional sim resources at `date`" | tee --append $LOG_FILE_NAME

/usr/local/php/bin/php build-install.php --download-sims | tee --append $LOG_FILE_NAME

if [ "$?" -ne "0" ]; then
  echo "Error downloading sims" | tee --append $LOG_FILE_NAME
  exit 1
fi

echo "Finished downloading additional sim resources at `date`" | tee --append $LOG_FILE_NAME

#------------------------------------------------------------------------------
# Create the marker file, which is used by the sims to determine whether they
# were run from the installer or as an individually downloaded executable.
#------------------------------------------------------------------------------

echo "Started creating marker file at `date`" | tee --append $LOG_FILE_NAME

/usr/local/php/bin/php build-install.php --create-maker-file | tee --append $LOG_FILE_NAME

if [ "$?" -ne "0" ]; then
  echo "Error creating marker file" | tee --append $LOG_FILE_NAME
  exit 1
fi

echo "Finished creating marker file at `date`" | tee --append $LOG_FILE_NAME

#------------------------------------------------------------------------------
# Insert the time stamps that are used by the sims to interact with the server
# and decide if a newer version of the sim is available.
#------------------------------------------------------------------------------

echo "Started creating marker file at `date`" | tee --append $LOG_FILE_NAME

/usr/local/php/bin/php build-install.php --insert-installer-creation-time | tee --append $LOG_FILE_NAME

if [ "$?" -ne "0" ]; then
  echo "Error inserting creation timestamps." | tee --append $LOG_FILE_NAME
  exit 1
fi

echo "Finished creating marker file at `date`" | tee --append $LOG_FILE_NAME

#------------------------------------------------------------------------------
# Output some final time information.
#------------------------------------------------------------------------------

echo "================================================================" | tee --append $LOG_FILE_NAME
echo " Completed web site rip at: " | tee --append $LOG_FILE_NAME
date | tee --append $LOG_FILE_NAME
echo "================================================================" | tee --append $LOG_FILE_NAME

