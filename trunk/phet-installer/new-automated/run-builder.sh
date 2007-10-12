#!/bin/sh
svn update

/usr/local/php/bin/php build-install.php --full | tee installer-builder-log.txt

rm -rf ./website/* | tee installer-builder-log.txt
cp ./installer-output/*.* ../phet-dist/ | tee installer-builder-log.txt