#!/bin/sh

#############################################################################
# This script kicks off a full rip of the web site and rebuild of the
# installers.
#############################################################################

LOG=./installer-builder-log.txt

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
   tail -n $LINES_TO_SEND $LOG >> $EMAIL_MSG
   EMAIL_SUBJECT="Automated installer build completed"
   /bin/mail -s "$EMAIL_SUBJECT" "$EMAIL_ADDR" < $EMAIL_MSG
   rm $EMAIL_MSG
}

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

# Make sure we are starting from the correct directory.
cd /web/htdocs/phet/installer-builder

echo "================================================================" | tee --append $LOG
echo " Full rip and rebuild operation performed by `whoami` on: " | tee --append $LOG
echo " `date`" | tee --append $LOG
echo "================================================================" | tee --append $LOG

if [ "$1" = "--deploy" ]; then
   echo "Sims will be deployed after they are built." | tee --append $LOG
   /usr/local/php/bin/php full-rip-and-rebuild.php --deploy | tee --append $LOG
else
   echo "Sims will NOT be deployed after they are built." | tee --append $LOG
   /usr/local/php/bin/php full-rip-and-rebuild.php | tee --append $LOG
fi

send_email_notification
