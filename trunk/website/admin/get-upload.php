<?php
    include_once("web-utils.php");
    include_once("sys-utils.php");

    $url = resolve_url_upload($_REQUEST["url"]);
    
    if (isset($_REQUEST['mime_type'])) {
        send_file_to_browser($url, $_REQUEST['mime_type']);
    }
    else {        
        send_file_to_browser($url);
    }
?>