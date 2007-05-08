<?php

    include_once("../admin/contrib-utils.php");
    include_once("../admin/site-utils.php");
    include_once("user-login.php");
    
    function print_edit_profile_form() {
        global $contributor_id;
        
        print "<h1>Manage Contributions</h1>";
    }
    
    print_site_page('print_edit_profile_form', 3);

?>