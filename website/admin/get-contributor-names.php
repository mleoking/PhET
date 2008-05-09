<?php

    // Called from Javascript for AJAX stuff

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");

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