<?php
    /*
    This file requires that you create a login-info.php with the following
    constants defined:

        DB_HOSTNAME     Hostname of the MySQL server.
        DB_NAME         Name of the database.
        DB_USERNAME     Username for the database account.
        DB_PASSWORD     Password for the database account.

    If this file does not exist, or these constants are not defined, this
    file will not successfully load.

    */

    // Only one requirement, sidestep the include_path melarchy
    require_once("login-info.php");

    // connect to the server, select db
    function connect_to_db($hostname = DB_HOSTNAME,
                           $dbname = DB_NAME,
                           $user = DB_USERNAME,
                           $password = DB_PASSWORD,
                           $verbose = true) {
        global $connection;

        @$connection = mysql_connect($hostname, $user, $password);

        if (!$connection) {
            if ($verbose) {
                print("Database error: Could not connect to database.");
            }
            return false;
        }

        if (!mysql_select_db($dbname, $connection)) {
            if ($verbose) {
                print("Database error: Could not select database.");
            }
            return false;
        }

        return true;
    }

    // Usused as of 1/5/09
    function showerror($errortext) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $phet_help_email = PHET_HELP_EMAIL;
        print("<br>==========================");
        print("<p>We're very sorry, but there appears to have been an error. We would appreciate it if you would report this to us, so that we can fix the problem and serve you better. Please direct all correspondence to <a href='mailto:{$phet_help_email}'>{$phet_help_email}</a>.</p>");
        print($errortext);
        print("<p>Error#: ". mysql_errno($connection).", Error Description: ".mysql_error($connection).".</p>");
        print("==========================<br>");
        exit;
    }

    // Usused as of 1/5/09
    function missingfield($showtext) {
        print("<p>".$showtext."</p>");
        print("<p><a href='javascript:history.back(1)'>Click here</a> or use your browser's back button to return to the form.</p>");

        exit;
    }

?>