#!/bin/bash

#=============================================================================
# This script reads the latest installation date information from the PhET
# database.  It exists to aid in the testing of the feature where the
# installer-builder inserts the time at which the most recent version was
# created into the DB.
#=============================================================================

MYSQL=/usr/local/mysql/bin/mysql

printf "\n"
echo "All timestamps:";

$MYSQL -u maytag --password=peth5431 -h hellcat phet_production -e 'SELECT * FROM `installer_info`';

printf "\n\n"
echo "Most recent timestamp";
$MYSQL -u maytag --password=peth5431 -h hellcat phet_production -e 'SELECT * FROM `installer_info` ORDER BY `installer_info_id` DESC LIMIT 1';

