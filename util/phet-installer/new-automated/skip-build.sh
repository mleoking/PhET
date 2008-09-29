#!/bin/sh

#############################################################################
# This script simply adds an entry to the installer log that says that the
# installers were not build by the cron job.  This is used as a target for
# the crontab file when we do NOT want to build the installers, perhaps
# because there is a bug in the installer scripts that needs to be resolved.
#############################################################################

#----------------------------------------------------------------------------
# Subroutine for sending email notification of results.
#----------------------------------------------------------------------------
function send_email_notification {

   # Email distribution list - this controls who receives notifications of the
   # result of the build process.
   EMAIL_ADDR="john.blanco@colorado.edu cmalley@pixelzoom.com wendy.adams@colorado.edu daniel.mckagan@gmail.com reids@colorado.edu"

   EMAIL_MSG="/tmp/phet_build_email_msg.txt"
   echo "The installers were intentionally NOT BUILT by the cron job."> $EMAIL_MSG
   echo "">> $EMAIL_MSG
   EMAIL_SUBJECT="Result of nightly installer build: $1"
   /bin/mail -s "$EMAIL_SUBJECT" "$EMAIL_ADDR" < $EMAIL_MSG
   rm $EMAIL_MSG
}

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

# Make sure we're in the proper directory
cd /web/htdocs/phet/installer-builder/

echo "================================================================" | tee --append installer-builder-log.txt
echo " Installer operation SKIPPED on: " | tee --append installer-builder-log.txt
date  | tee --append installer-builder-log.txt
echo "================================================================" | tee --append installer-builder-log.txt

send_email_notification SKIPPED
