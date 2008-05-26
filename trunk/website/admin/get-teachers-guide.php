<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sim-utils.php");

    if (!isset($_REQUEST["teachers_guide_id"]) ||
        empty($_REQUEST["teachers_guide_id"])) {
            print "no";
        exit;
    }

    $teachers_guide = sim_get_teachers_guide($_REQUEST["teachers_guide_id"], true);

    send_file_to_browser($teachers_guide["teachers_guide_filename"], $teachers_guide["teachers_guide_contents"]);


?>