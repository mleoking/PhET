<?php

    include_once("../admin/global.php");
    include_once("../admin/sys-utils.php");
    include_once("../admin/contrib-utils.php");
    include_once("../admin/site-utils.php");
    
    include_once("../teacher_ideas/user-login.php");

    $sim_id             = $_REQUEST['sim_id'];
    $contribution_title = $_REQUEST['contribution_title'];
    
    $file = $_FILES['contribution_file_url'];
    
    $name     = $file['name'];
    $type     = $file['type'];
    $tmp_name = $file['tmp_name'];
    $size     = $file['size'];
    $error    = $file['error'] !== 0;
    
    if ($contribution_title == '') {
        $contribution_title = remove_file_extension(basename($name));
    }
    
    $contribution_id = contribution_add_new_contribution($contribution_title, $contributor_id, $tmp_name, $name);
    
    if (is_numeric($sim_id)) {
        contribution_associate_contribution_with_simulation($contribution_id, $sim_id);
    }
    
    $sims_page    = "../simulations/sims.php?sim_id=$sim_id";    
    $edit_contrib = "$prefix/teacher_ideas/edit-contribution.php?contribution_id=$contribution_id&amp;referrer=$sims_page";
    
    // Redirect to contribution editing page:
    force_redirect("$edit_contrib", 7);
    
    // Automatically approve submission:
    contribution_set_approved($contribution_id, true);
    
    function print_content() {
        global $contributor_is_team_member;
        
        print <<<EOT
        <h1>New Contribution</h1>
        
        <p>Your contribution has been successfully submitted to PhET.</p>
        
        <p>On the next page, you will have the chance to edit your contribution or specify additional information.</p>
        
        <p><a href="$edit_contrib">Proceed to the next page.</a></p>
EOT;
    }
    
    print_site_page('print_content', 2, "../..");
?>