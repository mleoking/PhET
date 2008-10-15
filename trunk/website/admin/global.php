<?php

    assert(defined("SITE_ROOT"));

    @include_once("local-debug-settings.php");
    include_once("referring-page-tracker.php");

    function debug_is_on() {
        if (isset($GLOBALS["DEBUG"]) && $GLOBALS["DEBUG"]) {
            return true;
        }
        else {
            return false;
        }
    }

    if (!debug_is_on()) {
        // Not sure why E_ERROR is on, it was like this when I got here, not touching it for now
        error_reporting(E_ERROR);
        assert_options(ASSERT_ACTIVE, 0);
    }

    if (!defined("PORTAL_ROOT")) {
        define("PORTAL_ROOT", SITE_ROOT);
    }

    if (!defined("CACHE_ROOT")) {
        define("CACHE_ROOT", SITE_ROOT);
    }

    if (!defined("CACHE_DIRNAME")) {
        define("CACHE_DIRNAME", "webcache");
    }

    define("PHET_DOMAIN_NAME", "phet.colorado.edu");
    define("PHET_HELP_EMAIL", "phethelp@colorado.edu");

    // Latest versions of flash and java
    define("OS_MIN_VERSION_OSX", "10.4");

    define("JAVA_MIN_VERSION_GENERIC", "1.5");
    define("JAVA_MIN_VERSION_WIN", "1.5.0_15");
    define("JAVA_MIN_VERSION_OSX", "1.5.0_16");
    define("JAVA_MIN_VERSION_LIN", "1.5.0_15");
    define("JAVA_MIN_VERSION_GENERIC_FULL", JAVA_MIN_VERSION_GENERIC."1.5");
    define("JAVA_MIN_VERSION_WIN_FULL", "Sun Java ".JAVA_MIN_VERSION_WIN);
    define("JAVA_MIN_VERSION_OSX_FULL", "Apple Java ".JAVA_MIN_VERSION_OSX);
    define("JAVA_MIN_VERSION_LIN_FULL", "Sun Java ".JAVA_MIN_VERSION_LIN);
    define("FLASH_MIN_VERSION", "8");
    define("FLASH_MIN_VERSION_FULL", "Macrmedia Flash 8");

    /**
     * This constant is used so that included scripts can reference the files 
     * they require using an absolute path, which seems to be required due to
     * odd behavior of require/include functions.
     */

    if (!defined("SITE_ROOT")) {
        define("SITE_ROOT", "../");
    }


    ini_set('upload_max_filesize',    '20M');
    ini_set("session.gc_maxlifetime", "10800"); 
    ini_set("session.cache_expire",   "180");
    /*
     * Not sure why these are so high... makes for a lot of garbage piling up
     * changing these to lower numbers, about 3 hours
    ini_set("session.gc_maxlifetime", "999999999"); 
    ini_set("session.cache_expire",   "999999999");
    */

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

     // Set the timezone for stricter compliance
    ini_set("date.timezone", "America/Denver");

    // Debugging aids
    function microtime_float() {
        list($usec, $sec) = explode(" ", microtime());
        return ((float)$usec + (float)$sec);
    }

?>
