#!/bin/sh

#############################################################################
# This is a test script that will rip ONLY A SMALL SUBSET of the PhET web
# site to a temporary directory. This script was developed for testing and is
# not a part of the regular installer build process.  It is used to quickly
# create a small version of the web site which can then be packaged up by
# the installer builder.
# jblanco, 10/22/2008
#############################################################################

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

# Make sure we're in the proper directory
cd /web/htdocs/phet/installer-builder/

echo "================================================================"
echo " Ripping subset of web site on: "
date
echo "================================================================"

echo "Ripping the home page of the web site..."

/usr/local/php/bin/php build-install.php --rip-website-subset

if [ "$?" -ne "0" ]; then
  echo "Error ripping web site."
  exit 1
fi

echo "Done."
