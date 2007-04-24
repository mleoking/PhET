<?php
    include_once("admin/sim-utils.php");
    include_once("admin/sys-utils.php");
    include_once("admin/web-utils.php");
    
    $thumbnails = sim_get_animated_previews();
    
    $random_key = array_rand($thumbnails);
    
    send_file_to_browser(resolve_url_upload($thumbnails[$random_key]));
?>