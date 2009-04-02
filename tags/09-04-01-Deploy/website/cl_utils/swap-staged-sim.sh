#!/bin/bash

# Convenience variables
WEB_ROOT=/web/htdocs/phet
BACKUPS_ROOT=$WEB_ROOT/backups
BACKUP_SIMS_ROOT=$BACKUPS_ROOT/sims
SIMS_ROOT=$WEB_ROOT/sims
STAGING_SIMS_ROOT=$WEB_ROOT/staging/sims

#
# Make sure the staging directory exists
if [ ! -d $STAGING_SIMS_ROOT/$1 ] ; then
    echo Project in staging directory does not exist: $STAGING_SIMS_ROOT/$1
    exit 1
fi

#
# Case where the sim is new: no project in SIMS_ROOT
if [ ! -d $SIMS_ROOT/$1 ] ; then
    mv $STAGING_SIMS_ROOT/$1 $SIMS_ROOT
    exit $?
fi

#
# At this point, the staging directory exists and there is a
# simulation to replace
#

#
# Make sure the backup and original sim dir exist
if [ ! -d $SIMS_ROOT/$1 ] ; then
    echo Original sim project does not exist: $SIMS_ROOT/$1
    exit 1
fi

if [ ! -d $BACKUP_SIMS_ROOT ] ; then
    # Try to make it first
    echo Making backup directories to: $BACKUP_SIMS_ROOT
    mkdir $BACKUPS_ROOT 2> /dev/null
    chmod 0770 $BACKUPS_ROOT 2> /dev/null
    mkdir $BACKUP_SIMS_ROOT 2> /dev/null
    chmod 0770 $BACKUP_SIMS_ROOT 2> /dev/null
    if [ ! -d $BACKUP_SIMS_ROOT ] ; then
	echo Backup sims directory does not exist: $BACKUP_SIMS_ROOT
	exit 1
    fi
fi

#
# Move the files
SECS=`date -u +%s`
mv $SIMS_ROOT/$1 $BACKUP_SIMS_ROOT/$1-$SECS
mv $STAGING_SIMS_ROOT/$1 $SIMS_ROOT
exit $?