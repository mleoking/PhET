#!/bin/bash

# Very special purpose script that is helpful in trimming web site rip.
# Created in early Feb 2009 by jblanco.  Not intended to go into SVN.

NEEDED_FILES=`grep -l -i balloons *`

for FILE in $NEEDED_FILES
do
   echo Moving file $FILE
   cp $FILE ./keepers/
done
