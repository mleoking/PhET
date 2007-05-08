<?php

    include_once("../admin/contrib-utils.php");
    include_once("../admin/site-utils.php");
    include_once("user-login.php");
    
    function print_edit_profile_form() {
        global $contributor_id;
        
        contributor_print_full_edit_form($contributor_id, "user-update-profile.php");
    }
    
    print_site_page('print_edit_profile_form', 6);

?>