<?php

    // This file is called from sims.php to run a sim "offline" (download from Internet and run locally)

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/log-utils.php");

    // Get the simulation id
    $sim_id = $_REQUEST['sim_id'];

    // Get the language, if present
    $language_code = "en";
    if (isset($_REQUEST["lang"])) {
        $language_code = $_REQUEST["lang"];
    }

    // Get the simulation data
    $simulation = sim_get_sim_by_id($sim_id);

    // Log this info
    $datefmt = "y/m/d h:i:s A";
    $log_string = join("\t", array(date($datefmt), time(), $simulation['sim_name'], $language_code, $_SERVER['REMOTE_ADDR']))."\n";
    log_message('download-java-sim.log', $log_string);

    // Get the filename and content
    $download_data = sim_get_run_offline($simulation, $language_code);

    if ($download_data) {
        $filename = $download_data[0];
        $contents = $download_data[1];
        send_file_to_browser($filename, $contents, null, "attachment");
    }

?>