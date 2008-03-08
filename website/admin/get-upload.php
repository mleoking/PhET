<?php
	error_reporting(0);

    include_once("web-utils.php");
    include_once("sys-utils.php");
    include_once("contrib-utils.php");

    if (isset($_REQUEST['contribution_file_id'])) {
        $contribution_file_id = $_REQUEST['contribution_file_id'];

        $contribution_file = contribution_get_contribution_file_by_id($contribution_file_id);

        send_file_to_browser($contribution_file['contribution_file_name'], base64_decode($contribution_file['contribution_file_contents']));
    }
    else {
        $url = resolve_url_upload($_REQUEST["url"]);

        if (isset($_REQUEST['mime_type'])) {
            send_file_to_browser($url, null, $_REQUEST['mime_type']);
        }
        else {
            send_file_to_browser($url);
        }
    }
?>