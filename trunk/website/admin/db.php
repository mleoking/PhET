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
    include_once("login-info.php");

    // connect to the server, select db
    function connect_to_db() {
        global $connection;

        assert('defined("DB_HOSTNAME")');
        assert('defined("DB_NAME")');
        assert('defined("DB_USERNAME")');
        assert('defined("DB_PASSWORD")');

        $connection = mysql_connect(DB_HOSTNAME, DB_USERNAME, DB_PASSWORD);

        if (!$connection) {
            print("Database error: Could not connect to database.");
        }
        if (!mysql_select_db(DB_NAME, $connection)) {
            print("Database error: Could not select database.");
        }
    }

    /*
     * Don't connect unless we need to
    if (!isset($GLOBALS['connection'])) {
        connect_to_db();
    }
    */

    function showerror($errortext) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $phet_help_email = PHET_HELP_EMAIL;
        echo "<br>==========================";
        echo "<p>We're very sorry, but there appears to have been an error. We would appreciate it if you would report this to us, so that we can fix the problem and serve you better. Please direct all correspondence to <a href='mailto:{$phet_help_email}'>{$phet_help_email}</a>.</p>";
        echo $errortext;
        echo "<p>Error#: ". mysql_errno($connection).", Error Description: ".mysql_error($connection).".</p>";
        echo "==========================<br>";
        exit;
    }

    function missingfield($showtext) {
        echo "<p>".$showtext."</p>";
        echo "<p><a href='javascript:history.back(1)'>Click here</a> or use your browser's back button to return to the form.</p>";

        exit;
    }

?>