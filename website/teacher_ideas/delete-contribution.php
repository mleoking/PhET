<?php

    include_once("../admin/global.php");
    include_once("user-login.php");
    include_once("../admin/contrib-utils.php");

    function print_content() {
        ?>
        
        <h1>Contribution Deleted</h1>
        
        <p>The contribution has been successfully deleted.</p>
        
        <?php
    }
    
    function print_content_error() {
        ?>
        
        <h1>Contribution Deletion Failed</h1>
        
        <p>The contribution could not be deleted because it does not exist or you do not have permission to delete it.</p>
        
        <?php
    }

    if (isset($_REQUEST['referrer'])) {
        $referrer = $_REQUEST['referrer'];
    }
    else {
        $referrer = 'teacher_ideas/manage-contributions.php';
    }

    force_redirect("../$referrer", 3);
    
    if (isset($_REQUEST['contribution_id'])) {
        $contribution_id = $_REQUEST['contribution_id'];
        
        if (contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {
            contribution_delete_contribution($contribution_id);
        
            print_site_page('print_content', 3);
            
            exit;
        }
    }
    
    print_site_page('print_content_error', 3);
?>