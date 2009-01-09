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

    /**
     * Return the referrer:
     *   1st if specified by REQUEST
     *   2nd if specified by parameter
     *   3rd will be the REQUEST_URI
     *
     * @param $referrer_if_no_request string[optional] referrer to use if REQUEST isn't specified
     * @return unknown
     */
    function get_referrer($referrer_if_no_request = null) {
        assert(!isset($GLOBALS['referrer']));

        if (isset($_REQUEST['referrer'])) {
            $referrer = $_REQUEST['referrer'];
            //$GLOBALS['referrer'] = $_REQUEST['referrer'];
        }
        else if (!is_null($referrer_if_no_request)) {
            $referrer = $referrer_if_no_request;
        }
        else {
            $referrer = $_SERVER['REQUEST_URI'];
            //$GLOBALS['referrer'] = $_SERVER['REQUEST_URI'];
        }

        // TODO: check if this is only for browse.php and find a better solution
        $referrer = remove_script_param_from_url('content_only', $referrer);
        //$GLOBALS['referrer'] = format_for_html(remove_script_param_from_url('content_only', $GLOBALS['referrer']));

        return $referrer;
    }

?>