#!/bin/sh

#############################################################################
# Script for analyzing a rip of the PhET web site and listing any missing
# JNLP files.  The list of files that SHOULD be present is determined by
# analyzing the sim pages.  That list is then tested agains the JNLP files in
# the sims directory.
#
# This is highly dependent on the structure of the web site, so it may not
# work if that structure has changed.
#
# This scrip was created in early June 2015 to help solve this issue:
# https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3696.
#############################################################################

# Change the following path var to use this script in different places
WEB_RIP_ROOT=./temp/website/phet.colorado.edu

# Use the legacy directory if it exists.  This depends on when the rip 
# occurred.  Before May 28 2015 it didn't exist, after that it did.
if [ -e $WEB_RIP_ROOT/en/simulation/legacy ]; then
  pathToSimPages=$WEB_RIP_ROOT/en/simulation/legacy;
else
  pathToSimPages=$WEB_RIP_ROOT/en/simulation;
fi

# Create a list of the JNLP files linked from the HTML files that define the "sim pages".
jnlpLinks=( $( grep "_.*\.jnlp" $pathToSimPages/*.html | grep -v textarea | sed -e 's/^.*\/sims\///' -e 's/jnlp.*$/jnlp/' | uniq | sort ) );

# Temp for testing
echo ${jnlpLinks[@]} > temp.txt;

# Test for missing JNLP files
count=0
for fileName in "${jnlpLinks[@]}"
do
  fileName=$WEB_RIP_ROOT/sims/$fileName
  if [ ! -e $fileName ]; then 
    echo $fileName ": file does not exist"; 
    let "count += 1";
  fi
done

echo "Number of missing JNLP files = "$count;

