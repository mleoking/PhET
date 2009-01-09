<?php

    // TODO: make sure that this page works as intended... removal of global variables may have affected this

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/referring-statistics.php");

    if (!isset($GLOBALS['referring_page'])) {
        global $referring_page;

        if (isset($_COOKIE["referring_page"])) {
            $referring_page = $_COOKIE["referring_page"];
        }
    }

    if (isset($_SERVER['PHP_SELF'])) {
        setcookie("referring_page", $_SERVER['PHP_SELF'], time() + 3600, "/");
    }

    if (isset($GLOBALS['referring_page']) && isset($_SERVER['PHP_SELF'])) {
        referring_statistics_add($GLOBALS['referring_page'], $_SERVER['PHP_SELF']);
    }

?>