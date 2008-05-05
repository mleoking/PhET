<?php

    @include_once("local-debug-settings.php");

    function debug_is_on() {
        if (isset($GLOBALS["DEBUG"]) && $GLOBALS["DEBUG"]) {
            return true;
        }
        else {
            return false;
        }
    }

    if (!debug_is_on()) {
        error_reporting(E_ERROR);
        assert_options(ASSERT_ACTIVE, 0);
    }

    ini_set("session.gc_maxlifetime", "999999999"); 
    ini_set("session.cache_expire",   "999999999");
    ini_set('upload_max_filesize',    '20M');

    if (isset($GLOBALS['IE6_DOWNLOAD_WORKAROUND']) &&
        ($GLOBALS['IE6_DOWNLOAD_WORKAROUND'])) {
        // Workaround for IE6 which has a bug with meta refresh
        // and downloading files.  Double check this against the
        // local test machine before uncommenting.
        $browser = $_SERVER['HTTP_USER_AGENT'];
        if (strstr($browser, 'MSIE 5.5') ||
            strstr($browser, 'MSIE 6.0')) {
            session_cache_limiter('must-revalidate');
        }
    }
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