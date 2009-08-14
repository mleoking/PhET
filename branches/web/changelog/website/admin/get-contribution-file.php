<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/web-utils.php");
    require_once("include/sys-utils.php");
    require_once("include/contrib-utils.php");

    if (isset($_REQUEST['contribution_file_id'])) {
        $contribution_file_id = $_REQUEST['contribution_file_id'];

        $contribution_file = contribution_get_contribution_file_by_id($contribution_file_id);

        send_file_to_browser($contribution_file['contribution_file_name'], base64_decode($contribution_file['contribution_file_contents']));
    }

?>