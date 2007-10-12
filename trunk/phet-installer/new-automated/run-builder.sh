#!/bin/sh
echo "Performing SVN update" | tee installer-builder-log.txt

svn update

if [ "$?" -ne "0" ]; then
  echo "Error performing SVN update" | tee installer-builder-log.txt
  exit 1
fi

echo "Building all installers" | tee installer-builder-log.txt

/usr/local/php/bin/php build-install.php --full | tee installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error building installer" | tee installer-builder-log.txt
  exit 1
fi

echo "Removing temporary files" | tee installer-builder-log.txt

rm -rf ./website/* | tee installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error removing temporary files" | tee installer-builder-log.txt
  exit 1
fi

echo "Copying new installers to distribution directory" | tee installer-builder-log.txt

cp ./installer-output/*.* ../phet-dist/ | tee installer-builder-log.txt

if [ "$?" -ne "0" ]; then
  echo "Error copying new installers to distribution directory" | tee installer-builder-log.txt
  exit 1
fi