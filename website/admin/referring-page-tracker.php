<?php

    // TODO: make sure that this page works as intended... removal of global variables may have affected this

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/referring-statistics.php");

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