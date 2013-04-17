Post processing and visualization for events collected from the sim sharing project.
See Unfuddle ticket https://phet.unfuddle.com/projects/9404/tickets/by_number/3147

######################################
#     mongodb
Backup/Restore:

For backing up and restoring the data, we are using mongodump, see http://www.mongodb.org/display/DOCS/Import+Export+Tools#ImportExportTools-mongodumpandmongorestore
Run the command like this:
mongodump.exe --host phet-server.colorado.edu --port 44100

To load a dump directory into a running mongoDB instance, use:
mongorestore C:\Users\Sam\Desktop\mongodbbackup\dump
#
###################

Sam Reid
11-10-2011
updated Jan 24, 2012