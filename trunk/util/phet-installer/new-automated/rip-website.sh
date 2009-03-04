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

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================"
echo " Ripping web site on: "
date
echo "================================================================"

echo "Removing old web site..."

/usr/local/php/bin/php build-install.php --remove-website-copy

if [ "$?" -ne "0" ]; then
  echo "Error removing old web site."
  exit 1
fi

echo "Ripping the web site..."

/usr/local/php/bin/php build-install.php --rip-website

if [ "$?" -ne "0" ]; then
  echo "Error ripping web site."
  exit 1
fi

echo "Downloading the sims..."

/usr/local/php/bin/php build-install.php --download-sims

if [ "$?" -ne "0" ]; then
  echo "Error downloading sims"
  exit 1
fi

echo "Creating marker file..."

/usr/local/php/bin/php build-install.php --create-maker-file

if [ "$?" -ne "0" ]; then
  echo "Error creating marker file"
  exit 1
fi

echo "Inserting creation time stamps..."

/usr/local/php/bin/php build-install.php --insert-installer-creation-time

if [ "$?" -ne "0" ]; then
  echo "Error inserting creation timestamps."
  exit 1
fi

echo "Done."
