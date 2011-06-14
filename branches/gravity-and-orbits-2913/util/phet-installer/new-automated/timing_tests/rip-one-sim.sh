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

#Specify the directory where the rip with be captured.
RIP_DIR="./rip_dir"

# Specify the base URL from whence the simulation will be captured.
RIP_BASE_URL="http://phet.colorado.edu"

# Specify the simulation that will be captured.  This name must match the name
# within the sims directory structure.
SIM_NAME=balloons

# Specify the filters for preventing undesired files from being ripped.
HTTRACK_FILTERS=" \"-*\" \"+*/sims/balloons/*\" "

# Specify the options for the rip command.
HTTRACK_OPTIONS="-j0 -D -c1 -v -f"

# Put the various pieces together into the overall command.
HTTRACK_CMD="$HTTRACK $RIP_BASE_URL/sims/$SIM_NAME -O $RIP_DIR $HTTRACK_FILTERS $HTTRACK_OPTIONS"

# If a previous rip exists, delete it.
if [ -d "$RIP_DIR" ]; then
   echo Removing previous rip in directory $RIP_DIR
   rm -rf $RIP_DIR
fi

# Output the host name.
echo Hostname: `hostname`

# Output the version information.  The command to do this is a little odd, but
# I couldn't get any of the options that were supposed to output the version
# info to actually work.
$HTTRACK --help | grep "HTTrack version"

# Perform a rip operation and output the time information.
echo Command being timed: $HTTRACK_CMD
START_TIME="$(date +%s)"
time $HTTRACK_CMD
FINISH_TIME="$(date +%s)"

ELAPSED_SECONDS="$(expr $FINISH_TIME - $START_TIME)"
echo Elapsed time for code block in seconds: $ELAPSED_SECONDS
