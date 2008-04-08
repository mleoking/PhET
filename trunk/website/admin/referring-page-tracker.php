<?php

    include_once("../admin/global.php");
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