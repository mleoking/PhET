#!/bin/bash

#-----------------------------------------------------------------------------
# This script rips a single simulation and outputs the amount of time that was
# required.  The basic idea is to run the script on different servers and to
# vary the contants defined below in order to gather data about how long
# various rip operations take in different circumstances.
#-----------------------------------------------------------------------------

# Set the path to HTTrack as needed for the machine on which the script is
# being run.
HTTRACK="/usr/local/httrack/bin/httrack"
#HTTRACK="/web/chroot/phet/usr/local/apache/htdocs/installer-builder/HTTrack/Linux/httrack"

# Specify the directory where the rip with be captured.
RIP_DIR="./rip_dir"

# Specify the page from which the rip will start.
RIP_PAGE_URL="http://phet.colorado.edu/simulations/sims.php?sim=Balloons_and_Static_Electricity"

# Specify the filters that define which files are captured.
HTTRACK_FILTERS=" \"-*\" \"+*/sims/balloons/*\" "

# Specify the options for the rip command.
HTTRACK_OPTIONS="-j0 -D -c1 -v -f"

# Put the various pieces together into the overall command.
HTTRACK_CMD="$HTTRACK $RIP_PAGE_URL -O $RIP_DIR $HTTRACK_FILTERS $HTTRACK_OPTIONS"

# If a previous rip exists, delete it.
if [ -d "$RIP_DIR" ]; then
   echo Removing previous rip in directory $RIP_DIR
   rm -rf $RIP_DIR
fi

# Output the host name so we can tell where this was run.
echo Hostname: `hostname`

# Output the HTTrack version information.  The command to do this is a little
# odd, but # I couldn't get any of the options that were supposed to output the
# version info to actually work.
$HTTRACK --help | grep "HTTrack version"

# Perform the rip operation and output the time information.
echo Command being timed: $HTTRACK_CMD
START_TIME="$(date +%s)"
time $HTTRACK_CMD
FINISH_TIME="$(date +%s)"

# Output the elapsed seconds in addition to the data that is output by the
# 'time' command.
ELAPSED_SECONDS="$(expr $FINISH_TIME - $START_TIME)"
echo Elapsed time for code block in seconds: $ELAPSED_SECONDS
