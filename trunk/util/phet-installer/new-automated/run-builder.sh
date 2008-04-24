#!/bin/sh

#
# This script is intended to be run from the PhET website server (currently tigercat).
# Paths are currently hard coded to this system.
# 

# Make sure we're in the proper directory
cd /web/htdocs/phet/installer-builder/

echo "================================================================" | tee --append installer-builder-log.txt
echo " Installer opreation performed on: " | tee --append installer-builder-log.txt
date  | tee --append installer-builder-log.txt
echo "================================================================" | tee --append installer-builder-log.txt

echo "Performing SVN update" | tee --append installer-builder-log.txt

svn update

if [ "$?" -ne "0" ]; then
  echo "Error performing SVN update" | tee --append installer-builder-log.txt
  exit 1
fi

echo "Building all installers" | tee --append installer-builder-log.txt

/usr/local/php/bin/php build-install.php --full | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error building installer" | tee --append installer-builder-log.txt
  exit 1
fi

echo "Removing temporary files" | tee --append installer-builder-log.txt

rm -rf ./temp/website/* | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error removing temporary files" | tee --append installer-builder-log.txt
  exit 1
fi

echo "Copying new installers to distribution directory" | tee --append installer-builder-log.txt

cp ./temp/installer-output/*.* ../phet-dist/ | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error copying new installers to distribution directory" | tee --append installer-builder-log.txt
  exit 1
fi
