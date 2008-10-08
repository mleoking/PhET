#!/bin/bash          

#------------------------------------------------------------------------------
# Simple script for creating a backup directory and copying the current set of
# installers into it.  This should be done periodically so that we can be sure
# that we have copies of the installers that work in case something breaks
# with the automated installer-builder process.
#
# jblanco, October 2008
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

if [ "$?" -ne "0" ]; then
  echo "Error copying files into backup directory."
  exit 1
fi

# See if there are too many backups already hanging around and, if so, give
# the user the option to delete the oldest ones.

MAX_BACKUP_DIRS=5
NUMBER_OF_BACKUP_DIRS=`ls -d backup* | wc -l`
EXISTING_BACKUP_DIRS=`ls -d backup*`

if [ "$NUMBER_OF_BACKUP_DIRS" -gt "$MAX_BACKUP_DIRS" ]; then
   echo "There are more than $MAX_BACKUP_DIRS backup directories."
   for DIR in $EXISTING_BACKUP_DIRS
   do
      if [ "$NUMBER_OF_BACKUP_DIRS" -gt "$MAX_BACKUP_DIRS" ]; then
         read -p "Okay to delete backup directory $DIR (y/n)? " INPUT
         if [ "$INPUT" == "y" ]; then
            rm -rfv $DIR
         else
            echo "Not deleting $DIR."
         fi
      else
         echo "Not deleting $DIR."
      fi
      let "NUMBER_OF_BACKUP_DIRS -= 1"
   done
fi
