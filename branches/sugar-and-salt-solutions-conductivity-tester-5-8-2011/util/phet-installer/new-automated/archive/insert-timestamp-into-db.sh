#!/bin/bash

#=============================================================================
# This script inserts the timestamp contained in a temporary file (presumably
# created during the installer-build process) into a database so that it can
# be retrieved by sims out in the real world.
#=============================================================================

PHP=/usr/local/php/bin/php
TIMESTAMP_FILE_NAME=installer-creation-timestamp.txt

if [ ! -e $TIMESTAMP_FILE_NAME ]
then
   echo "Error: Timestamp file $TIMESTAMP_FILE_NAME not found, aborting."
   exit 1
else
   TIMESTAMP=`cat $TIMESTAMP_FILE_NAME`
   /web/htdocs/phet/cl_utils/add-installer-timestamp.php $TIMESTAMP
fi

