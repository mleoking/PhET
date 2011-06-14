#!/bin/bash

# The placeholders will be replaced by the post installation script

if [ $(which htmlview) != -1 ] ; then
    htmlview file://@@installdir@@/docs/readme.html
elif [ $(which mozilla) != -1 ] ; then
    mozilla file://@@installdir@@/docs/readme.html
elif [ $(which konqueror) != -1 ] ; then
    konqueror file://@@installdir@@/docs/readme.html
elif [ $(which xterm) != -1 ] ; then
    xterm -e more "@@installdir@@/docs/readme.txt"
fi




