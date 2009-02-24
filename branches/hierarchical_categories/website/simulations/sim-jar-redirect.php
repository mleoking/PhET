<?php

    // This file is called from Java (& Flash?) sims as part of the update process

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/sim-utils.php");
    require_once("include/sys-utils.php");
    require_once("include/web-utils.php");

    function query_string_extract($key) {
        if (is_array($key)) {
            foreach ($key as $k) {
                if (isset($_GET[$k])) {
                    return $_GET[$k];
                }
            }
            return '';
        }

        if (isset($_GET[$key])) {
            return $_GET[$key];
        }
        else {
            return '';
        }
    }

    function error($message) {
        if (debug_is_on()) {
            var_dump($message);
        }
        else {
            send_file_to_browser('error.txt', $message, null, "attachment");
        }
    }

    // Grab the required parameters
    $required_params = array('project', 'sim', 'language');
    $missing_params = array();
    foreach ($required_params as $key) {
        $$key = query_string_extract($key);
        if (empty($$key)) {
            $missing_params[] = $key;
        }
    }

    // Send an error if there are missing parametrs
    if (!empty($missing_params)) {
        error("Error: Missing required information in the query string: '".
              join("', '", $missing_params)."'.\n");
        exit;
    }

    // Get the country, this is not required to be specified
    $country = query_string_extract('country');

    // Create a locale from the language and country
    $locale = $language;
    if (!empty($country)) {
        $locale .= '_'.$country;
    }

    // Get the database info for the requested sim
    $simulation = sim_get_sim_by_dirname_flavorname($project, $sim);
    if (!$simulation) {
        error("Error: Simulation not found.\n");
        exit;
    }

    // Get the filename and content
    $download_data = sim_get_download($simulation, $locale);
    if (!$download_data) {
        error("Error: Simulation jar or language not found.\n");
        exit;
    }

    // Send the file as an attachment
    $filename = $download_data[0];
    $contents = $download_data[1];
    send_file_to_browser($filename, $contents, null, "attachment");

?>