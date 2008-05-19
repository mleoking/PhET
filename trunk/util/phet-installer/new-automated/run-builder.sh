#!/bin/sh

#
# This script is intended to be run from the PhET website server (currently tigercat).
# Paths are currently hard coded to this system.
# 

# Make sure we're in the proper directory
cd /web/htdocs/phet/installer-builder/

# Subroutine for sending email notification of results.
function send_email_notification {
   EMAIL_ADDR="john.blanco@colorado.edu"
   EMAIL_MSG="/tmp/phet_build_email_msg.txt"
   LINES_TO_SEND=30
   echo "Below are the final $LINES_TO_SEND lines excerpted from the log file:"> $EMAIL_MSG
   echo "">> $EMAIL_MSG
   tail -n $LINES_TO_SEND ./installer-builder-log.txt >> $EMAIL_MSG
   EMAIL_SUBJECT="Result of nightly installer build: $1"
   /bin/mail -s "$EMAIL_SUBJECT" "$EMAIL_ADDR" < $EMAIL_MSG
}

echo "================================================================" | tee --append installer-builder-log.txt
echo " Installer opreation performed on: " | tee --append installer-builder-log.txt
date  | tee --append installer-builder-log.txt
echo "================================================================" | tee --append installer-builder-log.txt

echo "Performing SVN update" | tee --append installer-builder-log.txt

svn update

if [ "$?" -ne "0" ]; then
  echo "Error performing SVN update" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

echo "Building all installers" | tee --append installer-builder-log.txt

/usr/local/php/bin/php build-install.php --full | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error building installer" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

echo "Removing temporary files" | tee --append installer-builder-log.txt

rm -rf ./temp/website/* | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error removing temporary files" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

echo "Copying new installers to distribution directory" | tee --append installer-builder-log.txt

cp ./temp/installer-output/*.* ../phet-dist/ | tee --append installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error copying new installers to distribution directory" | tee --append installer-builder-log.txt
  send_email_notification FAILURE
  exit 1
fi

send_email_notification SUCCESS
