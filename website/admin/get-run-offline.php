<?php

    // This file is called from sims.php to run a sim "offline" (download from Internet and run locally)

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    $sim_id = $_REQUEST['sim_id'];

    $simulation = sim_get_sim_by_id($sim_id);

    $sim_file_location = sim_get_run_offline_content_location($simulation);

    $sim_file = file_get_contents($sim_file_location);

    send_file_to_browser($sim_file_location, $sim_file, null, "attachment");
?>