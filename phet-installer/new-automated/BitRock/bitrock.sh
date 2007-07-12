#!/bin/sh
if [ `uname` = "Darwin" ]; then
    # Mac
    ./bin/Builder.app/Contents/MacOS/installbuilder build $1 $2 $3
    
else
    # Linux
	echo "Linux not yet supported"
fi
