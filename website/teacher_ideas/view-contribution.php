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
        
        $contribution_answers_included = $contribution_answers_included == 1 ? "Yes" : "No";
        
        $type_list    = convert_array_to_comma_list($type_names);
        $subject_list = convert_array_to_comma_list($subject_names);
        $level_list   = convert_array_to_comma_list($level_names);
        
        $comment_count = 0;
        
        $files_html = contribution_get_files_listing_html($contribution_id);
        
        $download_script = SITE_ROOT."admin/download-archive.php?contribution_id=$contribution_id";
        
        $comments = '';
        
        print <<<EOT
        
            <h3>Download Files</h3>
        
            $files_html          
            
            Or you may <a href="$download_script">download</a> all files as a compressed archive.  
            
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
            
            <div>
                <span class="label">Title</span>
                <span class="label_content">$contribution_title</span>
            </div>            
            
            <div>
                <span class="label">Keywords</span>
                <span class="label_content">$contribution_keywords</span>
            </div>
            
            <div>
                <span class="label">Description</span>
                <span class="label_content">$contribution_desc</span>
            </div>

            <div>
                <span class="label">Level</span>
                <span class="label_content">$level_list</span>
            </div>
            
            <div>
                <span class="label">Type</span>
                <span class="label_content">$type_list</span>
            </div>
            
            <div>
                <span class="label">Subject</span>
                <span class="label_content">$subject_list</span>
            </div>
            
            <div>
                <span class="label">Duration</span>
                <span class="label_content">$contribution_duration</span>
            </div>
            
            <div>
                <span class="label">Answers Included</span>
                <span class="label_content">$contribution_answers_included</span>
            </div>
            
            <div>
                <span class="label">
                    Comments
                </span>
                
                <span class="label_content">
                    <a href="#" onclick="$(this).parent().parent().next().toggle(300);">$comment_count comments</a>
                </span>
            </div>
            
            <div class="label_content" style="display: none">
                These are the comments; $comments
            </div>
        
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