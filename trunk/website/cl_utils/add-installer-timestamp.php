#!/web/chroot/phet/usr/local/php/bin/php
<?php

    chdir(dirname(__FILE__));

    // SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/installer-utils.php");

    // Parse the command line
    if (empty($argv[1]) || (count($argv) != 2)) {
        $prog = 'add-installer-timestamp.php';
        if (!empty($argv[0])) {
            $prog = basename($argv[0]);
        }
        print "ERROR: usage: {$prog} time_in_secs\n";
        exit(1);
    }

    $new_timestamp = $argv[1];

    $match = preg_match('/^[0-9]+$/', $new_timestamp);
    if ($match <= 0) {
        print_usage();
        print "ERROR: value must be an int (all digits)\n";
        exit(2);
    }

    $result = installer_check_timestamp($new_timestamp);
    if (!$result) {
        $db_newest_timestamp = installer_get_latest_timestamp();
        print "ERROR: specified value '{$new_timestamp}' older than newest in database '{$db_newest_timestamp}'\n";
        exit(3);
    }

    $result = installer_add_new_timestamp($new_timestamp);
    if ($result <= 0) {
        print "ERROR: insert into database failed\n";
        exit(4);
    }

    // Hey, we made it OK!
    exit(0);

?>
