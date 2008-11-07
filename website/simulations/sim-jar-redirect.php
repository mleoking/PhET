<?php

    // This file is called from sims.php to run a sim "offline" (download from Internet and run locally)

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    // Get the simulation id
    //$sim_id = $_REQUEST['sim_id'];
    //todo: hard coded to always give english balloons,  needs to be fixed
    $sim_id = 81;

    // Get the language, if present
    $language_code = "en";
    if (isset($_REQUEST["lang"])) {
        $language_code = $_REQUEST["lang"];
    }

    // Get the simulation data
    $simulation = sim_get_sim_by_id($sim_id);
//    echo($simulation);

    // Get the filename and content
    $download_data = sim_get_run_offline($simulation, $language_code);

    if ($download_data) {
        $filename = $download_data[0];
        $contents = $download_data[1];
        send_file_to_browser($filename, $contents, null, "attachment");
    }

?>