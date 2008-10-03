#!/bin/sh

#############################################################################
# This is a version of the main build script that was constructed for
# testing the builder.  It assumes that the web site has already been
# ripped into the temporary directory.  This is for testing purposes and
# and it not part of the nightly installer build process.
#
# Other differences:
#   - Email notification is removed.
#   - The ripped web site is not deleted.
#   - Does not append any output to the log file.
#   - Does not copy installers to the distribution directory.
#
# jblanco, 6-10-2008.
#############################################################################

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

# Make sure we're in the proper directory
cd /web/htdocs/phet/installer-builder/

echo "================================================================"
echo "Installers built on: "
date 
echo "================================================================"

echo "Performing SVN update"

svn update

if [ "$?" -ne "0" ]; then
  echo "Error performing SVN update"
  exit 1
fi

echo "Building all installers"

/usr/local/php/bin/php build-install.php --build-all

if [ "$?" -ne "0" ]; then
  echo "Error building installers"
  exit 1
fi


