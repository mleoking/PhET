<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");
    
    $contributor = contributor_get_contributor_by_email($_REQUEST['contributor_email']);
    
    if ($contributor) {
        print $contributor['contributor_organization'];
    }

?>