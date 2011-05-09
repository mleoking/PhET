#!/web/chroot/phet/usr/local/php/bin/php
<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/contrib-utils.php");
require_once("include/db-utils.php");

DEFINE("LOG_FILENAME", "delete_old_partial_contributions_log.txt");
DEFINE("TOO_OLD_IN_SECS", 60 * 60 * 24);

class Log {
    private $fp;

    function __construct($log_filename = LOG_FILENAME) {
        $this->fp = null;

        $fp = fopen(LOG_FILENAME, "a");
        if ($fp === false) {
            return false;
        }

        $this->fp = $fp;

        $time = strftime("%B-%d-%Y  %H:%M:%S");
        $result = fwrite($this->fp, "----------------------------------------------------------------------\n");
        $result = fwrite($this->fp, "Log started at: $time\n");
    }

    function write($string, $add_newline = true) {
        if (is_null($this->fp)) {
            return;
        }

        $result = fwrite($this->fp, "   ".$string);
        if ($add_newline) {
            $result = fwrite($this->fp, "\n");
        }
    }

    function __destruct() {
        if (is_null($this->fp)) {
            return;
        }

        fflush($this->fp);
        fclose($this->fp);
    }
}

function delete_old_partial_contributions($log, $too_old_in_secs = TOO_OLD_IN_SECS) {
    $rows = db_get_all_rows("temporary_partial_contribution_track");
    $num_rows = count($rows);
    if ($num_rows == 0) {
        $log->write("No records need inspection");
        return 0;
    }

    // Give feedback
    $log->write("Found {$num_rows} row(s)");
    $log->write("Deleting contributions older than {$too_old_in_secs} seconds");

    // We'll give a message if nothing has been deleted
    $something_deleted = false;

    // Get the current time to compare to the entries
    $current_time = time();

    // Check out each temp contribution
    foreach ($rows as $temp_contribution_data) {
        // Get the data
        $contribution_id = $temp_contribution_data["contribution_id"];
        $sess_id = $temp_contribution_data["sessionid"];

        // Keep track of what to delete
        $should_delete_contribution = false;
        $should_delete_temp_record = false;

        // Get the contribtuion itself, we want the creation date
        $contribution = contribution_get_contribution_by_id($contribution_id);
        if ($contribution) {
            // Get the creation time
            $contribution_creation_time = strtotime($contribution["contribution_date_created"]);

            // Is it too old?
            $age = $current_time - $contribution_creation_time;
            if ($age > $too_old_in_secs) {
                // The contribution is too old, delete it and the temp record
                $should_delete_contribution = true;
                $should_delete_temp_record = true;
            }
            else {
                // Don't delete it
            }
        }
        else {
            // No contribution matches the temp record, something weird happend.  Delete the temp record.
            $should_delete_temp_record = true;
        }

        if ($should_delete_contribution) {
            $something_deleted = true;
            $log->write("Deleting contribution (id, age, title): {$contribution_id}, {$age}, \"{$contribution["contribution_title"]}\"");
            contribution_delete_contribution($contribution_id);
        }
        else {
            $log->write("NOT deleting contribution (id, age, title): {$contribution_id}, {$age} secs, \"{$contribution["contribution_title"]}\"");
        }

        if ($should_delete_temp_record) {
            $something_deleted = true;
            $log->write("Delete temp record (contribution_id, sessionid): {$temp_contribution_data["contribution_id"]}, {$temp_contribution_data["sessionid"]}");
            db_delete_row("temporary_partial_contribution_track", array("contribution_id" => $contribution_id));
        }
        else {
            $log->write("NOT deleting temp record (contribution_id, sessionid): {$temp_contribution_data["contribution_id"]}, {$temp_contribution_data["sessionid"]}");
        }
    }

    if (!$something_deleted) {
        $log->write("Nothing old enough to delete");
    }
}

function print_usage($extra_message = null) {
    if (!is_null($extra_message)) {
        print $extra_message."\n";
    }

    print "usage: delete_old_partial_contributions.php [age in secs]\n";
    print "   optional parameter is age threshold:\n";
    print "   delete all contributions older than [age in secs] old\n";
}

if ($argc == 1) {
    $age = TOO_OLD_IN_SECS;
}
else if ($argc == 2) {
    // Try to extract an age
    $age = intval($argv[1]);
    if ($age <= 0) {
        print_usage("ERROR: number of secs must be an integer greater than 0");
        exit(-2);
    }
}
else {
    print_usage();
    exit(-1);
}

$log = new Log();
$result = delete_old_partial_contributions($log, $age);
exit($result);

?>