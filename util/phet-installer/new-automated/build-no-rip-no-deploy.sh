#!/bin/sh

###############################################################################
# This script builds the installers but does not deploy them.  It assumes
# that an existing copy of the web site is available that has been fully
# prepared for bundling into the installers.  If no copy can be found, the
# script aborts.  This script is generally intended to be used for testing.
#
# Also note that the output of this script is NOT added to the installer log.
###############################################################################

WEB_SITE_LOCATION="./temp/website"

#==============================================================================
# Main body of this script.
#==============================================================================

echo "Checking for existing copy of web site..."

if [ -d $WEB_SITE_LOCATION ]; then
   echo "Found web site at $WEB_SITE_LOCATION, continuing."
else
   echo "Error: No copy of web site found, aborting."
   exit 1
fi

echo "Creating the marker file..."

/usr/local/php/bin/php build-install.php --create-marker-file

if [ "$?" -ne "0" ]; then
  echo "Error creating marker file, aborting."
  exit 1
fi

echo "Inserting the creation time stamps..."

/usr/local/php/bin/php build-install.php --insert-installer-creation-time

if [ "$?" -ne "0" ]; then
  echo "Error inserting creation time, aborting."
  exit 1
fi

echo "Building all installers..."

/usr/local/php/bin/php build-install.php --build-all

if [ "$?" -ne "0" ]; then
  echo "Error building installers, aborting."
  exit 1
fi

echo "Done."
