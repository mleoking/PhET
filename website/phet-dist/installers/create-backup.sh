#!/bin/bash          

#------------------------------------------------------------------------------
# Simple script for creating a backup directory and copying the current set of
# installers into it.  This should be done periodically so that we can be sure
# that we have copies of the installers that work in case something breaks
# with the automated installer-builder process.
#------------------------------------------------------------------------------

echo "Creating backup of installers..."
BACKUP_DIR=backup-$(date +%Y-%m-%d)

# Make the backup directory.

mkdir $BACKUP_DIR
if [ "$?" -ne "0" ]; then
  echo "Error creating backup directory."
  exit 1
fi

# Copy the files into the directory.

cp -v *installer* ./$BACKUP_DIR
cp -v *CD* ./$BACKUP_DIR

if [ "$?" -ne "0" ]; then
  echo "Error copying files into backup directory."
  exit 1
fi

EXISTING_BACKUP_DIRS=`ls -d`
for DIR in $EXISTING_BACKUP_DIRS
do
   echo $DIR
done
