<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/authentication.php");
    
    // Don't require authentication, but do it if the cookies are available:
    do_authentication(false);    

    $sim_id             = $_REQUEST['sim_id'];
    $contribution_title = $_REQUEST['contribution_title'];
    
    if (isset($_FILES['contribution_file_url'])) {
        $file = $_FILES['contribution_file_url'];
    }
    else {
        $file = $_FILES['MF__F_0_0'];
    }
    
    $name     = $file['name'];
    $type     = $file['type'];
    $tmp_name = $file['tmp_name'];
    $size     = $file['size'];
    $error    = $file['error'] != 0;
    
    if ($contribution_title == '') {
        $contribution_title = remove_file_extension(basename($name));
    }
    
    if (!isset($contributor_id)) {
        // The user isn't logged in yet. We'll add the contribution and change 
        // the owner later:
        $contributor_id = -1;
    }
    
    $contribution_id = contribution_add_new_contribution($contribution_title, $contributor_id, $tmp_name, $name);
    
    // Handle files:
    for ($i = 1; true; $i++) {
        $file_key = "MF__F_0_$i";
        
        if (!isset($_FILES[$file_key])) {            
            break;
        }
        else {
            $file = $_FILES[$file_key];

            $name     = $file['name'];
            $type     = $file['type'];
            $tmp_name = $file['tmp_name'];
            $size     = $file['size'];
            $error    = $file['error'] != 0;
            
            if (!$error){                
                contribution_add_new_file_to_contribution($contribution_id, $tmp_name, $name);
            }
            else {
                // Some error occurred during file upload
            }
        }
    }
    
    // Associate contribution with simulation:
    if (is_numeric($sim_id)) {
        contribution_associate_contribution_with_simulation($contribution_id, $sim_id);
    }
    
    // Establish multiselect associations (level, subject, type):
    contribution_establish_multiselect_associations_from_script_params($contribution_id);
    
    $sim_url = sim_get_url_to_sim_page($sim_id);
    
    $sims_page    = "\"$sim_url\"";
    $edit_contrib = "../teacher_ideas/edit-contribution.php?contribution_id=$contribution_id&amp;sim_id=$sim_id&amp;referrer=$sims_page";
    
    // Immediately redirect to contribution editing page:
    force_redirect("$edit_contrib", 0);
?>