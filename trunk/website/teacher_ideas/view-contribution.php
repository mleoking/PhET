<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");       
    include_once(SITE_ROOT."admin/db-utils.php");        

    function print_content() {
        global $contribution_id, $referrer;
        
        $level_names   = contribution_get_level_names_for_contribution($contribution_id);
        $subject_names = contribution_get_subject_names_for_contribution($contribution_id);
        $type_names    = contribution_get_type_names_for_contribution($contribution_id);

        $contribution = contribution_get_contribution_by_id($contribution_id);
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        $contribution_date_created = simplify_sql_timestamp($contribution_date_created);
        $contribution_date_updated = simplify_sql_timestamp($contribution_date_updated);        
        
        print <<<EOT
        
            <h3>Download Files</h3>
            
            
            <h3>Submission Information</h3>
            
            <div>
                <span class="label">Authors</span>
                <span class="label_content">$contribution_authors</span>
            </div>
            
            <div>
                <span class="label">Contact Email</span>
                <span class="label_content">$contribution_contact_email</span>
            </div>
            
            <div>
                <span class="label">School/Organization</span>
                <span class="label_content">$contribution_authors_organization</span>
            </div>
            
            <div>
                <span class="label">Submitted</span>
                <span class="label_content">$contribution_date_created</span>
                
                <span class="right_label">Updated</span>
                <span class="right_label_content">$contribution_date_updated</span>
            </div>            
            
            <h3>Contribution Description</h3>
            
            
        
        <p><a href="$referrer">back</a></p>
EOT;
    }
    
    $contribution_id = $_REQUEST['contribution_id'];
    
    if (isset($_REQUEST['referrer'])) {
        $referrer = $_REQUEST['referrer'];
    }
    else {
        $referrer = SITE_ROOT.'teacher_ideas/manage-contributions.php';
    }
    
    print_site_page('print_content', 3);

?>