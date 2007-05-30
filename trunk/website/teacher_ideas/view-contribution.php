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
        
        if ($contribution_duration == '') {
            $contribution_duration = 0;
        }
        
        $comments = contribution_get_comments($contribution_id);
        
        $comments_html = '';
        
        foreach($comments as $comment) {
            $comments_html .= '<p class="comment">"';
            $comments_html .= $comment['contribution_comment_text'];
            $comments_html .= '" - '.$contributor_name;
            $comments_html .= '</p>';
        }
        
        $contribution_desc = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Duis tincidunt enim et urna pretium porta. Quisque ligula. Praesent accumsan venenatis justo. Sed aliquam arcu eu eros. Ut justo tortor, nonummy nec, dignissim et, aliquet lacinia, libero. Suspendisse at lectus in tellus porta consectetuer. Ut sagittis. Nullam quam. Integer elit nulla, rhoncus nec, rhoncus varius, sagittis eget, pede. Pellentesque malesuada, nisi sit amet tincidunt molestie, ipsum libero suscipit turpis, eu dapibus sem libero sit amet tortor. Donec adipiscing, tortor ut ultrices placerat, orci urna varius diam, sit amet ultricies leo metus ut nisl. Vestibulum nunc massa, auctor ac, mattis non, ultrices eget, lorem.";
        
        print <<<EOT
        <div id="contributionview">
        
            <h3>Download Files</h3>
        
            $files_html          
            
            Or you may <a href="$download_script">download</a> all files as a compressed archive.  
            
            <h3>Submission Information</h3>
            
            <div class="field">
                <span class="label">Authors</span>
                <span class="label_content">$contribution_authors &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Contact Email</span>
                <span class="label_content">$contribution_contact_email &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">School/Organization</span>
                <span class="label_content">$contribution_authors_organization &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Submitted</span>
                <span class="left_label_content">$contribution_date_created</span>
                
                <span class="right_label">Updated</span>
                <span class="right_label_content">$contribution_date_updated</span>
            </div>            
            
            <h3>Contribution Description</h3>
            
            <div class="field">
                <span class="label">Title</span>
                <span class="label_content">$contribution_title &nbsp;</span>
            </div>            
            
            <div class="field">
                <span class="label">Keywords</span>
                <span class="label_content">$contribution_keywords &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Description</span>
                <span class="label_content">$contribution_desc &nbsp;</span>
            </div>

            <div class="field">
                <span class="label">Level</span>
                <span class="label_content">$level_list &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Type</span>
                <span class="label_content">$type_list &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Subject</span>
                <span class="label_content">$subject_list &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Duration</span>
                <span class="label_content">$contribution_duration minutes</span>
            </div>
            
            <div class="field">
                <span class="label">Answers Included</span>
                <span class="label_content">$contribution_answers_included</span>
            </div>
            
            <div class="field">
                <span class="label">
                    Comments
                </span>
                
                <span class="label_content">
                    <a href="javascript:void;" onclick="$(this).parent().parent().next().toggle(300);">$comment_count comments</a>
                    (<a href="javascript:void;" onclick="$(this).parent().parent().next().next().toggle(300);">add</a>)
                </span>
            </div>
            
            <div class="label_content" style="display: none">
                $comments_html
            </div>
            
            <div style="display: none">
                <div class="field">
                    <span class="label">Name</span>
                    <span class="label_content">
                        <input type="text" size="20" name="contributor_name" />
                    </span>
                </div>
                
                <div id="required_login_info_uid">
                </div>
                
                <div>
                    <textarea name="contribution_comment_text" cols="20" rows="5"></textarea>
                </div>
            </div>
            
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