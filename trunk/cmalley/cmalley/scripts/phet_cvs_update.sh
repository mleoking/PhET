#!/bin/bash
#####################################################################################
# 
# Replaces cvs.sourceforge.net with phet.cvs.sourceforge.net in all CVS/Root files.
# This was necessary when SourceForge changed their CVS infrastructure in May 2006.
#
#####################################################################################

for file in `find . -print | grep CVS/Root`; do
        echo $file;
        sed 's/cvs\.sourceforge\.net/phet\.cvs\.sourceforge\.net/g' $file > $file.update;
        mv $file.update $file;
done

#####################################################################################
# end of file