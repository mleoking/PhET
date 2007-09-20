<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    
    db_update_table('contributor', gather_script_params_into_array('contributor_'), 'contributor_id', $_REQUEST['contributor_id']);
    
    function print_profile_updated_message() {
        global $contributor_id;
    
        print("<p>The user's profile has been successfully updated!</p><br/>");
    }
    
    print_site_page('print_profile_updated_message', 9, "../admin/manage-contributors.php", 2);
?>