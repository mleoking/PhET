#!/bin/sh

#############################################################################
# This script kicks off a full rebuild of the standard PhET installers,
# meaning the installers that are available for download from the web site.
#############################################################################

LOG=./installer-builder-log.txt

#----------------------------------------------------------------------------
# Subroutine for sending email notification of results.
#----------------------------------------------------------------------------
function send_email_notification {

   # Email distribution list - this controls who receives notifications of the
   # result of the build process.
   EMAIL_ADDR="john.blanco@colorado.edu cmalley@pixelzoom.com olsonsjc@gmail.com reids@colorado.edu katherine.perkins@colorado.edu oliver.nix@colorado.edu"

   EMAIL_MSG="/tmp/phet_build_email_msg.txt"
   LINES_TO_SEND=30
   echo "Below are the final $LINES_TO_SEND lines from the installer build log file:"> $EMAIL_MSG
   echo "">> $EMAIL_MSG
   tail -n $LINES_TO_SEND $LOG >> $EMAIL_MSG
   EMAIL_SUBJECT="Automated installer build completed on `hostname`"
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

# Output logging information based on the specified parameters.
if [ "$1" = "--deploy" -o "$2" = "--deploy" ]; then
   echo "Installer(s) will be deployed after they are built." | tee --append $LOG
else
   echo "Installer(s) will NOT be deployed after they are built." | tee --append $LOG
fi

if [ "$1" = "--email" -o "$2" = "--email" ]; then
   echo "Email notification will be sent for this build." | tee --append $LOG
else
   echo "Email notification will NOT be sent for this build." | tee --append $LOG
fi

# Execute the main build script, passing in the appropriate options.
if [ "$1" = "--deploy" -o "$2" = "--deploy" ]; then
   /usr/local/php/bin/php ./bin/phet-full-rip-and-rebuild.php --deploy | tee --append $LOG
else
   /usr/local/php/bin/php ./bin/phet-full-rip-and-rebuild.php | tee --append $LOG
fi

# Send out email notification if specified on the command line.
if [ "$1" = "--email" -o "$2" = "--email" ]; then
   echo -e "\nSending email notification for this build." | tee --append $LOG
   send_email_notification
else
   echo -e "\nNot sending email notification for this build." | tee --append $LOG
fi


