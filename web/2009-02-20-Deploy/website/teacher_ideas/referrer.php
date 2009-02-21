<?php

    // Many pages in the teacher ideas use a variable passed called "referrer" to tell which page to
    // go back to after an operation is complete.  For example, when a contribution is deleted, the
    // delete-contribution page is loaded, and after an timeout (via a meta redirect) it is directed
    // back to whatever page had the "delete" command posted to it.  I've tried to maitain this
    // behavior as I've been refactoring many of these pages, but it has been awkward and may have
    // affected the referring page tracker.  It does work, though.

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/web-utils.php");

    function get_referrer($default = '') {
        $headers = apache_request_headers();
        if ((empty($_SERVER['HTTP_REFERER'])) || empty($_SERVER['HTTP_HOST'])) {
            // Cannot find the 2 variables we need to make this work
            return $default;
        }

        $host = $_SERVER['HTTP_HOST'];
        $referer = $_SERVER['HTTP_REFERER'];

        $url_regex = "@(?:http://)([^/]+)(.*)@i";

        $matches = array();
        $result = preg_match($url_regex, $referer, $matches);
        if (!$result) {
            // The search either had an error or didn't match
            return $default;
        }

        if ($host != $matches[1]) {
            // The referer host does is not this machine
            return $default;
        }

        if (empty($matches[2])) {
            // There is no path specified
            return $default;
        }

        // The referer host is this machine, return the referer path
        return $matches[2];
    }

?>