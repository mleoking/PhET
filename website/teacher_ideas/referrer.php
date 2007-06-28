<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/web-utils.php");

    if (!isset($GLOBALS['referrer'])) {
        if (isset($_REQUEST['referrer'])) {
            $GLOBALS['referrer'] = $_REQUEST['referrer'];
        }
        else {
            $GLOBALS['referrer'] = $_SERVER['REQUEST_URI'];
        }
    }
    
    $GLOBALS['referrer'] = format_for_html(remove_script_param_from_url('content_only', $GLOBALS['referrer']));
?>