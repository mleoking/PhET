<?php

    include_once("../admin/global.php");
    include_once("../admin/contrib-utils.php");
    include_once("../admin/site-utils.php");
    include_once("user-login.php");
    include_once("../admin/web-utils.php");
    include_once("../admin/db-utils.php");
    
    db_update_table('contributor', gather_script_params_into_array('contributor_'), 'contributor_id', $contributor_id);
    
    force_redirect("../teacher_ideas/user-edit-profile.php", 2);    
    
    function print_profile_updated_message() {
        global $contributor_id;
    
        print("<p>Your profile has been successfully updated!</p><br/>");
    }
    
    print_site_page('print_profile_updated_message', 3);

?>