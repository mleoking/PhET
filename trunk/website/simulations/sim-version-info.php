<?php

    // Given some info about a sim, return an XML file containg the version info.
    // Examlpe usage:
    //    http://phet.colorado.edu/simulations/sim-get-version.php?project=eating-and-exercise&sim=eating-and-exercise
    // Returns an attachment named 'version.xml' of the form (to avoid parsing problems, '***' => '?'):
    // <***xml version="1.0"***> 
    // <sim-version-info project="eating-and-exercise" sim="eating-and-exercise" version="1.00.00" revision="25678"/>
    //
    // This script is intended to be called from a simulation.

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    // Get the pertenant information
    if ((isset($_GET['project']) && (!empty($_GET['project']))) &&
        (isset($_GET['sim']) && (!empty($_GET['sim'])))) {
        $dirname = $_GET['project'];
        $flavorname = $_GET['sim'];
    }
    else {
        $error = "Error: Missing required information in the query string.\n";
        send_file_to_browser('error.txt', $error, null, "attachment");
        exit;
    }

    // Get the database info for the requested sim
    $simulation = sim_get_sim_by_dirname_flavorname($dirname, $flavorname);
    if (!$simulation) {
        $error = "Error: Simulation not found.\n";
        send_file_to_browser('error.txt', $error, null, "attachment");
        exit;
    }


    // Get the version info
    $version = sim_get_version($simulation);
    $version_string = '';
    if ((!empty($version['major'])) && (!empty($version['minor'])) && (!empty($version['dev']))) {
        $version_string = "{$version['major']}.{$version['minor']}.{$version['dev']}";
    }

    // Create the XML
    $xml = <<<EOT
<?xml version="1.0"?>
<sim-version-info project="{$dirname}" sim="{$flavorname}" version="{$version_string}" revision="{$version['revision']}"/>

EOT;

    // Send the file
    send_file_to_browser('version.xml', $xml, 'text/xml', 'attachment');

?>