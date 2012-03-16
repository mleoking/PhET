#!/bin/sh

# Bash script for launching the unfuddle notifer.

echo "Starting Unfuddle emailer on `date`"

LOG_FILE=$HOME/log/unfuddle-notifier-log.txt

# See if a log file already exists and, if so, copy it to a backup.
if [ -f $LOG_FILE ];
then
   echo "Log file exists, copying to backup before overwriting."
   BACKUP_LOG_FILE="$HOME/log/unfuddle-notifier-log-"`date '+%Y-%m-%d-%H%M'`".txt"
   echo "Backup file name = $BACKUP_LOG_FILE"
   mv $LOG_FILE $BACKUP_LOG_FILE
fi

# Start the installer.
echo "Starting Unfuddle emailer on `date`"
echo "Log file = $LOG_FILE"
while [ 1 -eq 1 ];
do
	echo "started loop"
	java -classpath contrib/javamail/smtp.jar:contrib/javamail/dsn.jar:contrib/javamail/imap.jar:contrib/javamail/mailapi.jar:contrib/javamail/pop3.jar:dist/unfuddle-email-pro.jar: edu.colorado.phet.unfuddle.UnfuddleEmailNotifier notifier 5hfYQB phetmail@colorado.edu smtp.colorado.edu phetmail P-Hmesa3duh /home/phet/svn/trunk phet.unfuddled.xml true > $LOG_FILE 2>&1
done
