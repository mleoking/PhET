<?php
    if (!defined('SITE_ROOT')) {
        include_once('../admin/global.php');
    }

    include_once(SITE_ROOT."admin/web-utils.php");

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
        else if (is_null($referrer_if_no_request)) {
            $referrer = $referrer_if_no_request;
        }
        else {
            $referrer = $_SERVER['REQUEST_URI'];
            //$GLOBALS['referrer'] = $_SERVER['REQUEST_URI'];
        }

        $referrer = remove_script_param_from_url('content_only', $referrer);
        //$GLOBALS['referrer'] = format_for_html(remove_script_param_from_url('content_only', $GLOBALS['referrer']));

        return $referrer;
    }

?>