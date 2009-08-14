#!/bin/bash

function check {
	for file in *;
	do
		if [ -e "${file}" ];
		then
			p=0

			# check whether the reporter can write to this file
			if [ ! -w "$file" ];
			then
				let p=p+1
			fi;

			# check whether the reporter can read this file
			if [ ! -r "$file" ];
			then
				let p=p+2
			fi;

            # check group read
			perms=`stat -c '%A' "$file"`
			if [ ! ${perms:4:1} = "r" ];
			then
			    let p=p+4
			fi;

			# check group write
			perms=`stat -c '%A' "$file"`
			if [ ! ${perms:5:1} = "w" ];
			then
			    let p=p+8
			fi;

			if [ ! $p -eq 0 ];
			then
                echo `stat -c '%U %A %G' "$file"` "${1}${file}" "("`stat -c '%F' "$file"`")"
			fi;
		fi;
		if [ -d "${file}" ];
		then
		    if [ -r "${file}" ];
		    then
                cd "$file"
                check "${1}${file}/"
                cd ..
			fi;
		fi;
	done;
}

check

