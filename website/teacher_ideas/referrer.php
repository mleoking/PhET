<?php

    include_once("../admin/global.php");

    if (!isset($GLOBALS['referrer'])) {
        if (isset($_REQUEST['referrer'])) {
            $GLOBALS['referrer'] = $_REQUEST['referrer'];
        }
        else {
            $GLOBALS['referrer'] = $_SERVER['REQUEST_URI'];
        }
    }

?>