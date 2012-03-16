#!/bin/bash
# Reads in platform-specific libraries and creates the JARs that we need.
# by Jonathan Olson

for platform in linux macosx solaris windows
do
	jarName=native_${platform}.jar
	
	# get a list of the libraries that we need to pack in the JAR
	cd ../native/$platform
	libList=`ls -1 * | xargs`
	cd ../../stripped-natives
	
	# create a temporary directory and fill it with copies of those libraries
	mkdir build
	for lib in $libList
	do
		cp "../native/$platform/$lib" build/
	done
	
	# build the JAR from our temporary directory
	jar -cfm "$jarName" MANIFEST.MF -C build .
	
	# delete the temporary directory
	rm -Rf build
	
	# list files in the JAR for verification
	echo "Files in ${jarName}:"
	jar -tf "$jarName"
done
