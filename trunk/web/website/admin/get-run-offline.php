<?php

    // This file is called from sims.php to run a sim "offline" (download from Internet and run locally)

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/sim-utils.php");
    require_once("include/sys-utils.php");
    require_once("include/web-utils.php");
    require_once("include/log-utils.php");

    // Get the simulation id
    $sim_id = $_REQUEST['sim_id'];

    // Get the language, if present
    $localeUtils = Locale::inst();
    $locale = Locale::DEFAULT_LOCALE;
    if (isset($_REQUEST["locale"]) && ($localeUtils->isValid($_REQUEST["locale"]))) {
        $locale = $_REQUEST["locale"];
    }
    else if (isset($_REQUEST["lang"]) && ($localeUtils->isValid($_REQUEST["lang"]))) {
        // Legacy, support the language argument
        $locale = $_REQUEST["lang"];
    }

    // Get the simulation data
    $simulation = sim_get_sim_by_id($sim_id);

    // Log this info
    $datefmt = "y/m/d h:i:s A";
    $log_string = join("\t", array(date($datefmt), time(), $simulation['sim_name'], $locale, $_SERVER['REMOTE_ADDR']))."\n";
    log_message('download-sim.log', $log_string);

    // Get the filename and content
    $download_data = sim_get_download($simulation, $locale, false);

    if ($download_data) {
        $filename = $download_data[0];
        $contents = $download_data[1];
        send_file_to_browser($filename, $contents, null, "attachment");
    }

?>
