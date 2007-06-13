<?php
    if (!defined('SITE_ROOT')) {
        define("SITE_ROOT", "./");
    }
    
    include_once("admin/sim-utils.php");
    include_once("admin/sys-utils.php");
    include_once("admin/web-utils.php");
    
    $thumbnails = sim_get_animated_previews();
    
    if (count($thumbnails) > 0) {
        $random_key = array_rand($thumbnails);
    
        send_file_to_browser(resolve_url_upload($thumbnails[$random_key]));
    }
?>