#!/bin/sh

#############################################################################
# This is the main control script for the installer builder.  It is intended
# to be run from the PhET website server (currently tigercat), and paths are
# currently hard coded to this system.
#############################################################################

#----------------------------------------------------------------------------
# Subroutine for sending email notification of results.
#----------------------------------------------------------------------------
function send_email_notification {

   # Email distribution list - this controls who receives notifications of the
   # result of the build process.
   EMAIL_ADDR="john.blanco@colorado.edu cmalley@pixelzoom.com wendy.adams@colorado.edu daniel.mckagan@gmail.com reids@colorado.edu marjorie.frankel@colorado.edu"

   EMAIL_MSG="/tmp/phet_build_email_msg.txt"
   LINES_TO_SEND=30
   echo "Below are the final $LINES_TO_SEND lines from the installer build log file:"> $EMAIL_MSG
   echo "">> $EMAIL_MSG
   tail -n $LINES_TO_SEND ./installer-builder-log.txt >> $EMAIL_MSG
   EMAIL_SUBJECT="Result of nightly installer build: $1"
   /bin/mail -s "$EMAIL_SUBJECT" "$EMAIL_ADDR" < $EMAIL_MSG
   rm $EMAIL_MSG
}

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

echo "================================================================" | tee --append installer-builder-log.txt
echo " Installer operation performed on: " | tee --append installer-builder-log.txt
date  | tee --append installer-builder-log.txt
echo " Initiated in directory: " | tee --append installer-builder-log.txt
pwd  | tee --append installer-builder-log.txt
echo "================================================================" | tee --append installer-builder-log.txt

echo "Removing previous web site copy" | tee --append installer-builder-log.txt
/usr/local/php/bin/php build-install.php --remove-web-site-copy | tee --append installer-builder-log.txt

echo "Building all installers" | tee --append installer-builder-log.txt

/usr/local/php/bin/php build-install.php --full | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error building installer" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

echo "Creating a backup of the previous set of installers." | tee --append installer-builder-log.txt

../phet-dist/installers/create-backup.sh ../phet-dist/installers/ | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error creating backups" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

echo "Copying new installers to distribution directory" | tee --append installer-builder-log.txt

cp ./temp/installer-output/*.* ../phet-dist/installers/ | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error copying new installers to distribution directory" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

echo "Putting creation timestamp into database..."

./insert-timestamp-into-db.sh

if [ "$?" -ne "0" ]; then
  echo "Error putting creation timestamp into database, aborting."
  exit 1
fi

send_email_notification SUCCESS

echo "Removing temporary files" | tee --append installer-builder-log.txt

rm -rf ./temp/installer-output/* | tee --append installer-builder-log.txt
rm -rf ./BitRock/output/* | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error removing temporary files" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

