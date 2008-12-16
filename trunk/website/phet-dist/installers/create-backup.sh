#!/bin/bash          

#------------------------------------------------------------------------------
# Simple script for creating a backup directory and copying the current set of
# installers into it.  This should be done periodically so that we can be sure
# that we have copies of the installers that work in case something breaks
# with the automated installer-builder process.
#------------------------------------------------------------------------------

# Initialize the variables that will be needed later.
if [ $# -gt 0 ]; then
    ROOT_DIR=$1
else
    ROOT_DIR=`pwd`/
fi
BACKUP_DIR_STEM_NAME=backup
BACKUP_DIR=$ROOT_DIR$BACKUP_DIR_STEM_NAME-$(date +%Y-%m-%d)

echo ""
echo "Creating backup of installers in directory $ROOT_DIR"

# See if the backup directory already exists and delete it if so.

if [ -d $BACKUP_DIR ]; then
    echo ""
    echo "Warning: A backup directory already exists for today, indicating that a"
    echo "backup has already been performed.  This directory will be deleted."
    echo ""
    rm -rf $BACKUP_DIR
fi

# Create the backup directory.

mkdir $BACKUP_DIR
if [ "$?" -ne "0" ]; then
  echo "Error creating backup directory."
  exit 1
fi

# Copy the files into the backup directory.

echo ""
cp -v $ROOT_DIR*installer* $BACKUP_DIR
cp -v $ROOT_DIR*CD* $BACKUP_DIR
echo ""

if [ "$?" -ne "0" ]; then
  echo "Error copying files into backup directory."
  exit 1
fi

# Check if we need to delete any old backups.  The number of backups that
# are kept is defined by the number in the awk portion of the command
# that creates the list of excess backup directories.  Adjust this number
# if you need to increase or reduce the number of backups maintained.

BACKUP_DIR_PATTERN=backup*
EXCESS_BACKUP_DIRS=`ls -C1 -t -d $ROOT_DIR$BACKUP_DIR_PATTERN | awk 'NR>4'`
for DIR in $EXCESS_BACKUP_DIRS
do
   echo Removing old backup directory $DIR
   rm -rf $DIR
   echo ""
done

