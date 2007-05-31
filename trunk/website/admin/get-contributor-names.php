<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");

    if (isset($_REQUEST['q'])) {
        $name_prefix = $_REQUEST['q'];
    
        $contributors = contributor_get_all_contributors();
    
        foreach($contributors as $contributor) {
            $contributor_name = $contributor['contributor_name'];
        
            if (string_starts_with(strtoupper($contributor_name), strtoupper($name_prefix))) {
                print $contributor_name."\n";
            }
        }
    }
?>