<?php
    if (!defined('SITE_ROOT')) {
        define("SITE_ROOT", "./");
    }
    
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    
    $thumbnails = sim_get_animated_previews();
    
    if (count($thumbnails) > 0) {
        $random_key = array_rand($thumbnails);

    	$resolved_url = resolve_url_upload($thumbnails[$random_key]);

        send_file_to_browser($resolved_url);
    }
?>