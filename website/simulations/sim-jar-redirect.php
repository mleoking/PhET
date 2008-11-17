<?php

    // This file is called from sims.php to run a sim "offline" (download from Internet and run locally)

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    // Get the pertenant information
    if ((isset($_GET['project']) && (!empty($_GET['project']))) &&
        (isset($_GET['sim']) && (!empty($_GET['sim']))) &&
        (isset($_GET['locale']) && (!empty($_GET['locale'])))) {
        $dirname = $_GET['project'];
        $flavorname = $_GET['sim'];
        $language_code = $_GET['locale'];
    }
    else {
        print 'Cannot retrieve sim, specified information is incorrect.  Need query term with "project", "sim", and "locale"'."\n";
        exit;
    }

    // Get the database info for the requested sim
    $simulation = sim_get_sim_by_dirname_flavorname($dirname, $flavorname);
    if (!$simulation) {
        print "Cannot retrieve sim, no simulation exists with that project name and sim name\n";
        exit;
    }

    // Get the filename and content
    $download_data = sim_get_run_offline($simulation, $language_code);
    if (!$download_data) {
        print "Cannot retrieve sim, there has been an error retrieving the file\n";
        exit;
    }

    // Send the file as an attachment
    $filename = $download_data[0];
    $contents = $download_data[1];
    send_file_to_browser($filename, $contents, null, "attachment");

?>