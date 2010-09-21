<?php

    // This file is called from sims.php to run a sim "offline" (download from Internet and run locally)

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

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

    // Allow downloading test sims if requested
    if (isset($_REQUEST['enable_test_sims']) && 
        $_REQUEST['enable_test_sims']) {
        SimFactory::inst()->enableTestSims();
    }

    // Get the simulation data
    $simulation = SimFactory::inst()->getById($sim_id);

    // Get the filename and content
    $filename = $simulation->getDownloadFilename($locale);
    if (!file_exists($filename)) {
        $filename = $simulation->getDownloadFilename(Locale::DEFAULT_LOCALE);
    }

    if (file_exists($filename)) {
        $contents = file_get_contents($filename);
        send_file_to_browser($filename, $contents, null, 'attachment');
    }

?>
