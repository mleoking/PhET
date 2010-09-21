<?php

    // Called from Javascript for AJAX stuff

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/contrib-utils.php");

    if (isset($_REQUEST['q'])) {
        $name_prefix = $_REQUEST['q'];

        $contributors = contributor_get_all_contributors();

        foreach($contributors as $contributor) {
            $contributor_name = $contributor['contributor_name'];

            if (string_starts_with(strtoupper($contributor_name), strtoupper($name_prefix))) {
                // TODO: Does this need format_for_html?  I'm not sure of the context it is used in.
                print $contributor_name."\n";
            }
        }
    }

?>