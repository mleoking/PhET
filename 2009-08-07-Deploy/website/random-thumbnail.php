<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "./");

    // This file is in the root, don't need to change include_path,
    // see global.php for an explaination.
    if (!defined("INCLUDE_PATH_SET")) define("INCLUDE_PATH_SET", "true");

    require_once("include/global.php");
    require_once("include/sys-utils.php");
    require_once("include/web-utils.php");

    $thumbnails = SimUtils::inst()->getAllAnimatedPreviewUrls();

    if (count($thumbnails) > 0) {
        for ($i = 0; $i < count($thumbnails); $i++) {
            $random_key = array_rand($thumbnails);

            $animated_screenshot = $thumbnails[$random_key];

            $file_contents = file_get_contents($animated_screenshot);
            if ($file_contents) {
                send_file_to_browser($animated_screenshot, $file_contents);
                break;
            }
        }
    }
    else {
        print "There are no animated previews.";
    }

?>