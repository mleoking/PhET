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
            
        // Perform cleanup for some fields:
        $contribution_keywords = convert_comma_list_into_linked_keyword_list($contribution_keywords);
        
        $contribution_date_created = db_simplify_sql_timestamp($contribution_date_created);
        $contribution_date_updated = db_simplify_sql_timestamp($contribution_date_updated);        
        
        $contribution_answers_included = $contribution_answers_included == 1 ? "Yes" : "No";
        
        $type_list    = convert_array_to_comma_list($type_names);
        $subject_list = convert_array_to_comma_list($subject_names);
        $level_list   = convert_array_to_comma_list($level_names);        

        $files_html = contribution_get_files_listing_html($contribution_id);
        
        $download_script = SITE_ROOT."admin/download-archive.php?contribution_id=$contribution_id";
        
        if ($contribution_duration == '') {
            $contribution_duration = 0;
        }
        
        $comments = contribution_get_comments($contribution_id);
        
        $comment_count = count($comments);
        
        $comments_html = '';
        
        foreach($comments as $comment) {
            $comments_html .= '<p class="comment">&quot;<em>';
            $comments_html .= $comment['contribution_comment_text'];
            $comments_html .= '</em>&quot; - '.$comment['contributor_name'];
            $comments_html .= '</p>';
        }

		$contribution_simulations = contribution_get_simulation_listings_as_list($contribution_id);
        
        print <<<EOT
        <div id="contributionview">
        
            <h3>Download Files</h3>
        
            $files_html          
            
            Or you may <a href="$download_script">download</a> all files as a compressed archive (<a href="http://en.wikipedia.org/wiki/ZIP_(file_format)">ZIP</a>).  
            
            <h3>Submission Information</h3>
            
            <div class="field">
                <span class="label_content">$contribution_authors &nbsp;</span>
                
                <span class="label">authors</span>
            </div>
            
            <div class="field">
                <span class="label_content">$contribution_contact_email &nbsp;</span>
                
                <span class="label">contact email</span>
            </div>
            
            <div class="field">
                <span class="label_content">$contribution_authors_organization &nbsp;</span>
                
                <span class="label">school/organization</span>
            </div>
            
            <div class="field">
                <span class="label_content">$contribution_date_created</span>
                
                <span class="label">submitted</span>
            </div>   
            
            <div class="field">
                <span class="label_content">$contribution_date_updated</span>
                
                <span class="label">updated</span>
            </div>        
            
            <h3>Contribution Description</h3>
            
            <div class="field">
                <span class="label_content">$contribution_title &nbsp;</span>
                
                <span class="label">title</span>
            </div>            

            <div class="field">
                <span class="label_content">$contribution_simulations &nbsp;</span>
                
                <span class="label">simulations</span>
            </div>
            
            <div class="field">
                <span class="label_content" id="keywords">$contribution_keywords</span>
                
                <span class="label">keywords</span>
            </div>
            
            <div class="field">
                <span class="label_content">$contribution_desc &nbsp;</span>
                
                <span class="label">description</span>
            </div>

            <div class="field">
                <span class="label_content">$level_list &nbsp;</span>
                
                <span class="label">level</span>
            </div>
            
            <div class="field">
                <span class="label_content">$type_list &nbsp;</span>
                
                <span class="label">type</span>
            </div>
            
            <div class="field">
                <span class="label_content">$subject_list &nbsp;</span>
                
                <span class="label">subject</span>                
            </div>
            
            <div class="field">
                <span class="label_content">$contribution_duration minutes</span>
                
                <span class="label">duration</span>                
            </div>
            
            <div class="field">
                <span class="label_content">$contribution_answers_included</span>
                
                <span class="label">answers included</span>
            </div>

            <div class="field">            
                <span class="label">standards compliance</span>
            </div>
            
            <div style="field">
EOT;

        contribution_print_standards_compliance($contribution_standards_compliance, true);
        
        $php_self = $_SERVER['PHP_SELF'];
        
        if (!isset($GLOBALS['contributor_name'])) {
            $contributor_name = '';
        }
        else {
            $contributor_name = $GLOBALS['contributor_name'];
        }

        print <<<EOT
            </div>
            
            <div class="field">
                <span class="label_content">
                    <a href="javascript:void;" onclick="$(this).parent().parent().next().toggle(300);">$comment_count comments</a>
                    (<a href="javascript:void;" onclick="$(this).parent().parent().next().next().next().toggle(300);">add</a>)
                </span>
                
                <span class="label">
                    comments
                </span>
            </div>
            
            <div class="comments" style="display: none">
                $comments_html
            </div>
            
            <hr/>
            
            <div style="display: none">
                <form method="post" action="add-comment.php" onsubmit="javascript:return false;">
                    <input type="hidden" name="contribution_id" value="$contribution_id" />
                    <input type="hidden" name="referrer"        value="$php_self?contribution_id=$contribution_id&referrer=$referrer" />
                    
                    <div class="field">
                        <span class="label_content">                
                            <input type="text" size="25" name="contributor_name" id="contributor_name_uid" onchange="javascript:on_email_entered();" value="$contributor_name"/>
                        </span>
                        
                        <span class="label">your name</span>                        
                    </div>
                
                    <div id="required_login_info_uid">
                        
                    </div>
                
                    <div class="field">
                        <span class="label_content">
                            <textarea name="contribution_comment_text" cols="40" rows="5" ></textarea>
                        </span>
                        
                        <span class="label">your comment</span>
                    </div>
                    
                    <div class="field">
                        <span class="label_content">
                            <input type="button" onclick="javascript:this.form.submit();" value="Add Comment" name="add" />
                        </span>
                        
                        <span class="label">&nbsp;</span>                        
                    </div>
                    
                </form>
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