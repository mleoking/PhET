<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    if (!isset($_REQUEST["teachers_guide_id"]) ||
        empty($_REQUEST["teachers_guide_id"])) {
            print "no";
        exit;
    }

    $teachers_guide = SimUtils::inst()->getTeachersGuideFilenameAndContents($_REQUEST["teachers_guide_id"], true);

    send_file_to_browser($teachers_guide["teachers_guide_filename"], $teachers_guide["teachers_guide_contents"]);


?>