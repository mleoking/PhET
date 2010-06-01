#!/bin/sh

#############################################################################
# This script kicks off a full rebuild of the standard PhET installers.
#############################################################################

LOG=./custom-installer-builder-log.txt

#----------------------------------------------------------------------------
# Subroutine for sending email notification of results.
#----------------------------------------------------------------------------
function send_email_notification {

   # Email distribution list - this controls who receives notifications of the
   # result of the build process.
   EMAIL_ADDR="john.blanco@colorado.edu cmalley@pixelzoom.com daniel.mckagan@gmail.com reids@colorado.edu katherine.perkins@colorado.edu marjorie.frankel@colorado.edu"

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

echo "================================================================" | tee --append $LOG
echo " Rip and rebuild of standard PhET installers initiated by `whoami` on: " | tee --append $LOG
echo " `date`" | tee --append $LOG
echo "================================================================" | tee --append $LOG

if [ "$1" = "--deploy" ]; then
   echo "Sims will be deployed after they are built." | tee --append $LOG
   /usr/local/php/bin/php ./bin/phet-full-rip-and-rebuild.php --deploy | tee --append $LOG
else
   echo "Sims will NOT be deployed after they are built." | tee --append $LOG
   /usr/local/php/bin/php ./bin/phet-full-rip-and-rebuild.php | tee --append $LOG
fi

if [ "$2" = "--email" ]; then
   send_email_notification
fi

