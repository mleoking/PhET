#!/web/chroot/phet/usr/local/php/bin/php
<?php

// Set the current directory to the script directory
chdir(dirname(__FILE__));

// SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/log-utils.php");

// Check to that we have enough arguments
if (count($argv) != 3) {
    // Change the default log dir to an absolute path for
    // better documentation
    $default_log_dir = dirname(__FILE__).'/'.DEFAULT_LOG_FILE_LOCATION;

    print <<<EOT
    usage:
        {$argv[0]} original_log new_log [log_file_location]

    Rename a log file.  Grab all contents of the old log
    file and prepend it to the new log file (if it has any
    contents).

    Note: be sure to change the function(s) that are
    writing to the log first!

    Optionally you can specify the directory where the log
    file should be found.

    By default, log files go here:
        {$default_log_dir}

EOT;
    exit(-1);
}

$orig_filename = $argv[1];
$new_filename = $argv[2];
$log_file_location = (isset($argv[3])) ? $argv[3] : DEFAULT_LOG_FILE_LOCATION;

$log_path = rtrim($log_file_location, '/\\').DIRECTORY_SEPARATOR;
$orig_path = $log_path.$orig_filename;
$new_path = $log_path.$new_filename;

// Get contents of old file
$orig_data = file_get_contents($orig_path);

// Open the new file
$fp = fopen($new_path, 'r+');
if (!$fp) {
    return false;
}

// Get a file lock on the new file
$startTime = microtime();
do {
    $canWrite = flock($fp, LOCK_EX);
    // If lock not obtained sleep for 0 - 100 milliseconds, to avoid collision and CPU load
    if (!$canWrite) {
        usleep(round(rand(0, 100)*1000));
    }
} while ((!$canWrite) && ((microtime() - $startTime) < 1000));

// File was locked so now we can store information
if ($canWrite) {
    // Get all data in new file
    $new_data = fread($fp, filesize($new_path));

    // Reset the write pointer to the beginning of the file
    fseek($fp, 0);

    // Combine the data
    $msg = $orig_data.$new_data;

    // Write all the data
    $bytes_written = fwrite($fp, $msg);
    if ($bytes_written != strlen($msg)) {
        // Error: just close the file and ignore it
        print "error: bytes written not correct (expected ".strlen($msg).", acutal {$bytes_written})\n";
        flock($fp, LOCK_UN);
        fclose($fp);
        return false;
    }
}

// Done with the new file
flock($fp, LOCK_UN);
fclose($fp);

// Delete the old log file
unlink($orig_path);

?>