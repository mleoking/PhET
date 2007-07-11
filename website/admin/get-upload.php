<?php
    include_once("web-utils.php");
    include_once("sys-utils.php");
    include_once("contrib-utils.php");

    if (isset($_REQUEST['contribution_file_id'])) {
        $contribution_file_id = $_REQUEST['contribution_file_id'];
        
        $contribution_file = contribution_get_contribution_file_by_id($contribution_file_id);
        
        eval(get_code_to_create_variables_from_array($contribution_file));
        
        send_file_to_browser($contribution_file_name, base64_decode($contribution_file_contents));
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