<?php

	error_reporting(E_ERROR);

    ini_set("session.gc_maxlifetime", "999999999"); 
    ini_set("session.cache_expire",   "999999999");
    ini_set('upload_max_filesize',    '20M');

    session_start();

    /**
     * This constant is used so that included scripts can reference the files 
     * they require using an absolute path, which seems to be required due to
     * odd behavior of require/include functions.
     */     
     
    if (!defined("SITE_ROOT")) {
        define("SITE_ROOT", "../");
    }

     // Set the timezone for stricter compliance
    ini_set("date.timezone", "America/Denver");

    // Debugging aids
    function microtime_float() {
        list($usec, $sec) = explode(" ", microtime());
        return ((float)$usec + (float)$sec);
    }

?>