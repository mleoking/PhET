<?php

    include_once("../../admin/sys-utils.php");
    include_once("../../admin/contrib-utils.php");
    include_once("../../admin/site-utils.php");
    
    $prefix = "../..";
    
    include_once("../../teacher_ideas/user-login.php");

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
    
    // Redirect to contribution editing page:
    force_redirect("edit-contribution.php?contribution_id=$contribution_id&amp;sim_id=$sim_id", 7);
    
    // Automatically approve submissions by PhET team members:
    if ($contributor_is_team_member) {
        contribution_set_approved($contribution_id, true);
    }
    
    function print_content() {
        global $contributor_is_team_member;
        ?>
        
        <h1>New Contribution</h1>
        
        <?php
        if (!$contributor_is_team_member) {
            print <<<EOT
            <p>
                Your contribution has been successfully submitted to PhET. Because it must be reviewed and 
                approved by PhET personnel, it may take as long as a week before your contribution becomes
                visible on the website.
            </p>
EOT;
        }
        else {
            print <<<EOT
            <p>
                Your contribution has been successfully submitted to PhET. Because you are a PhET team member,
                your contribution is immediately visible on the website.
            </p>
EOT;
        }
        ?>
        
        <p>On the next page, you will have the chance to edit your contribution or specify additional information.</p>
        
        <p><a href="edit-contribution.php?contribution_id=$contribution_id">Proceed to the next page.</a></p>
        
        <?php
    }
    
    print_site_page('print_content', 2, "../..");
?>