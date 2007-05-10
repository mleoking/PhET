<?php

    include_once("../admin/global.php");    

    include_once(SITE_ROOT."teacher_ideas/user-login.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    
    $contribution_id = $_REQUEST['contribution_id'];
    
    function print_content() {
        ?>
        
        <h1>Contribution Approved</h1>
        
        <p>The contribution has been marked as approved.</p>
        
        <?php
    }
    
    function print_content_error() {
        ?>
        
        <h1>Contribution Approval Error</h1>
        
        <p>The contribution cannot be marked as approved because you do not have permission to do so.</p>
        
        <?php
    }
    
    if ($contributor_is_team_member) {
        contribution_set_approved($contribution_id, true);
        
        print_site_page('print_content', 3);
    }
    else {
        print_site_page('print_content_error', 3);
    }
    
    if (isset($_REQUEST['referrer'])) {
        $referrer = $_REQUEST['referrer'];
    }
    else {
        $referrer = SITE_ROOT.'teacher_ideas/manage-contributions.php';
    }
    
    force_redirect($referrer, 4);

?>