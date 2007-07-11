<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/authentication.php");      
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."teacher_ideas/referrer.php");  
    
    function handle_action($action) {
        $contribution = gather_script_params_into_array('contribution_');
        
        if (isset($_REQUEST['contributor_id'])) {
            $contribution['contributor_id'] = $_REQUEST['contributor_id'];
        }
        
        if ($action == 'update') {
            update_contribution($contribution);
        }
    }
    
    function update_contribution($contribution) {
        do_authentication(true);
        
        if (!isset($contribution['contributor_id']) || $contribution['contributor_id'] == -1) {
            // The contribution is unowned; transfer ownership to the present user:
            $contribution['contributor_id'] = $GLOBALS['contributor_id'];
        }
        
        if (!isset($contribution['contribution_id']) || $contribution['contribution_id'] == -1) {
            // Updating a contribution that does not exist. First, create it:
            $contribution['contribution_id'] = contribution_add_new_contribution(
                $contribution['contribution_title'],
                $contribution['contributor_id']                
            );
            
            $GLOBALS['contribution_id'] = $contribution['contribution_id'];
        }
        
        contribution_update_contribution($contribution);
        
        $contribution_id = $contribution['contribution_id'];
        
        contribution_delete_all_multiselect_associations('contribution_level',   $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_type',    $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_subject', $contribution_id);

        contribution_unassociate_contribution_with_all_simulations($contribution_id);
        
        // Establish multiselect associations (level, subject, type):
        $files_to_keep = contribution_establish_multiselect_associations_from_script_params($contribution_id);
        
        $standards_compliance = generate_encoded_checkbox_string('standards');
        
        contribution_update_contribution(
            array(
                'contribution_id'                   => $contribution_id,
                'contribution_standards_compliance' => $standards_compliance
            )
        );
        
        // Automatically approve 'edited' submission:
        contribution_set_approved($contribution_id, true);
        
        // Handle files:
        
        // First, delete all files we aren't supposed to keep:
        contribution_delete_all_files_not_in_list($contribution_id, $files_to_keep);
        
        // Second, add all new files:
        contribution_add_all_form_files_to_contribution($contribution_id);
    }
    
    function print_content_no_contribution_specified() {
        print <<<EOT
            <h1>No Contribution Specified</h1>
            
            No contribution has been specified.
EOT;
    }
    
    function print_content_no_permission() {
        global $referrer;
        
        print <<<EOT
            <h1>Permission Error</h2>
            
            <p>You do not have permission to edit the specified contribution.</p>
EOT;

        print "<p><a href=\"$referrer\">cancel</a></p>";
    }

    function print_content() {
        global $referrer, $contribution_id, $contributor_id;
    
        ?>
    
        <h1>Edit Contribution</h1>
    
        <?php
    
        contribution_print_full_edit_form($contribution_id, "edit-contribution.php", $referrer);  
            
        print "<p><a href=\"$referrer\">cancel</a></p>";
    }
    
    // Authenticate, if information is available:
    do_authentication(false);
    
    if (isset($_REQUEST['sim_id'])) {
        $sim_id = $_REQUEST['sim_id'];
    }
    
    if (!isset($_REQUEST['contribution_id'])) {
        print_site_page('print_content_no_contribution_specified', 3);
    }
    else {
        $contribution_id = $_REQUEST['contribution_id'];
        
        if (!isset($contributor_id)) {            
            $contributor_id = -1;
        }
        
        if (contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {   
            if (isset($_REQUEST['action'])) {
                handle_action($_REQUEST['action']);
            }
            
            print_site_page('print_content', 3);
        }
        else {
            print_site_page('print_content_no_permission', 3);
        }
    }
?>