<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/authentication.php");    
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php"); 
    include_once(SITE_ROOT."admin/sim-utils.php");  
    
    function contribution_search_for_contributions($search_for) {
        $contributions = array();
        
        $st  = "SELECT * FROM `contribution` WHERE ";          
        
        $is_first = true;
        
        foreach(preg_split('/( +)|( *, *)/i', $search_for) as $word) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $st .= " AND ";
            }
            
            $st .= "(`contribution_title` LIKE '%$word%' OR `contribution_desc` LIKE '%$word%' OR `contribution_keywords` LIKE '%$word%' )";
        }
        
        $result = db_exec_query($st);
        
        while ($contribution = mysql_fetch_assoc($result)) {
            $contributions[] = $contribution;
        }
        
        return $contributions;
    }
    
    function contribution_get_comments($contribution_id) {
        $comments = array();
        
        $result = db_exec_query("SELECT * FROM `contribution_comment`, `contributor` WHERE `contribution_comment`.`contributor_id` = `contributor`.`contributor_id` AND `contribution_comment`.`contribution_id`='$contribution_id'  ");
        
        while ($comment = mysql_fetch_assoc($result)) {
            $comments[] = $comment;
        }
        
        return $comments;
    }
    
    function contribution_add_comment($contribution_id, $contributor_id, $contribution_comment_text) {
        $id = db_insert_row(
            'contribution_comment',
            array(
                'contribution_comment_text' => $contribution_comment_text,
                'contribution_id'           => $contribution_id,
                'contributor_id'            => $contributor_id
            )
        );
        
        return $id;
    }
    
    function contribution_delete_comment($contribution_comment_id) {
        return db_delete_row('contribution_comment_id', array( 'contribution_comment_id' => $contribution_comment_id ) );
    }
    
    function contribution_extract_original_file_name($contribution_file_url) {
        $basename = basename($contribution_file_url);
        
        $matches = array();
        
        preg_match('/php.*_(.+)/i', $basename, $matches);
        
        return $matches[1];
    }
    
    function contribution_get_files_listing_html($contribution_id) {
        $files_html = '<p>No files</p>';
        
        $files = contribution_get_contribution_files($contribution_id);
        
        if (count($files) > 0) {
            $files_html = '<ul>';
        
            foreach($files as $file) {
                eval(get_code_to_create_variables_from_array($file));
            
                $name = contribution_extract_original_file_name($contribution_file_url);
            
                $kb = ceil($contribution_file_size / 1024);
            
                $files_html .= "<li><a href=\"".SITE_ROOT."$contribution_file_url\">$name</a> - $kb KB</li>";
            }
        
            $files_html .= "</ul>";
        }
        
        return $files_html;
    }
    
    function contribution_get_simulations_listing_html($contribution_id) {
        $simulations_html = "<ul>";
        
        $simulation_listings = contribution_get_associated_simulation_listings($contribution_id);
                
        foreach($simulation_listings as $simulation_listing) {
            eval(get_code_to_create_variables_from_array($simulation_listing));
            
            $simulation = sim_get_sim_by_id($sim_id);
            
            eval(get_code_to_create_variables_from_array($simulation));
            
            $delete = "<input name=\"delete_simulation_contribution_id_${simulation_contribution_id}\" type=\"submit\" value=\"Delete\" />";
            
            $simulations_html .= "<li>$delete <a href=\"".SITE_ROOT."simulations/sims.php?sim_id=$sim_id\">$sim_name</a></li>";
        }
        
        $simulations_html .= "</ul>";
        
        return $simulations_html;
    }
    
    function contribution_print_standards_checkbox($encoded_string, $count = 1, $read_only = false) {
        for ($i = 0; $i < $count; $i++) {
            print '<td align="center">';
            print_string_encoded_checkbox('standards', $encoded_string, $read_only);
            print '</td>';        
        }
    }
    
    function contribution_print_standards_compliance($contribution_standards_compliance, $read_only = false) {
        print <<<EOT
        <div  id="nationalstandards">
        <table>
            <thead>
                <tr>
                    <td>&nbsp;</td>

                    <td colspan="3">Content Level</td>
                </tr>
                
                
                <tr>
                    <td>Content Standard</td>

                    <td>K-4</td>

                    <td>5-8</td>

                    <td>9-12</td>
                </tr>
            </thead>

            <tbody>

                <tr>
                    <td>Science as Inquiry - A</td>
EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Physical Science - B</td>
EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Life Science - C</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Earth &amp; Space Science - D</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Science &amp; Technology - E</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Science in Personal and Social Perspective - F</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>History and Nature of Science - G</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>
            </tbody>
        </table>
        </div>
EOT;
    }
    
    function contribution_add_all_form_files_to_contribution($contribution_id) {
        if (isset($_FILES['contribution_file_url'])) {
            $file = $_FILES['contribution_file_url'];
        }
        else if (isset($_FILES['MF__F_0_0'])) {
            $file = $_FILES['MF__F_0_0'];
        }
        else {
            return false;
        }

        $name     = $file['name'];
        $type     = $file['type'];
        $tmp_name = $file['tmp_name'];
        $error    = $file['error'] !== 0;
        
        if (!$error) {
            contribution_add_new_file_to_contribution($contribution_id, $tmp_name, $name);
        }
        
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
                $error    = $file['error'] !== 0;

                if (!$error){                
                    contribution_add_new_file_to_contribution($contribution_id, $tmp_name, $name);
                }
                else {
                    // Some error occurred; maybe user cancelled file
                }
            }
        }
    }
    
    function contribution_print_full_edit_form($contribution_id, $script, $referrer, $button_name = 'Update') {
        global $contributor_authenticated;
        
        $contribution = contribution_get_contribution_by_id($contribution_id);
        
        if (!$contribution) {            
            // Allow 'editing' of non-existent contributions:
            $contribution = db_get_blank_row('contribution');
        }
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        $contribution_authors_organization = '';
        $contribution_contact_email        = '';
        $contribution_authors              = '';
        
        do_authentication(false);
        
        if ($contributor_id == -1) {
            // The contribution didn't have any owner; assume the owner is the current editor:
            $contributor_id = $GLOBALS['contributor_id'];
        }
                
        // Set reasonable defaults:
        if ($contributor_authenticated) {
            if (!isset($contribution_authors_organization) || $contribution_authors_organization == '') {
                $contribution_authors_organization = $GLOBALS['contributor_organization'];
            }
            if (!isset($contribution_contact_email) || $contribution_contact_email == '') {
                $contribution_contact_email = $GLOBALS['contributor_email'];
            }
            if (!isset($contribution_authors) || $contribution_authors == '') {
                $contribution_authors = $GLOBALS['contributor_name'];
            }
        }
        if (!isset($contribution_keywords) || $contribution_keywords == '' && isset($GLOBALS['sim_id'])) {
            $simulation = sim_get_sim_by_id($GLOBALS['sim_id']);
            
            $contribution_keywords = $simulation['sim_keywords'];
        }            
        
        if (!isset($contribution_title) || $contribution_title == '') {
            $contribution_title = "A contribution from me";
        }
        
        $all_contribution_types = contribution_get_all_template_type_names();
        $contribution_types     = contribution_get_type_names_for_contribution($contribution_id);

        print <<<EOT
            <form id="contributioneditform" method="post" action="$script" enctype="multipart/form-data">
                <fieldset>
                    <legend>Required</legend>
EOT;

        if (!$contributor_authenticated) {
            if (isset($_REQUEST['loginmethod']) && strtolower($_REQUEST['loginmethod']) == 'static') {
                print_contribute_login_form();
            }
            else {
                print <<<EOT
                    
                            <div class="field">
                                <span class="label_content">                
                                    <input type="text" size="20" name="contributor_name" id="contributor_name_uid" onchange="javascript:on_name_change();"/>
                                </span>
                            
                                <span class="label">your name</span>                            
                            </div>
                
                            <div id="required_login_info_uid">
                    
                            </div>
                        
                            <hr/>   
EOT;
            }
        }

        print <<<EOT

                    <div class="field">
                        <span class="label_content">
                            <input type="text" name="contribution_authors" 
                                value="$contribution_authors" id="contribution_authors_uid" size="40" />
                        </span>
                        
                        <span class="label">
                            authors
                        </span>
                    </div>

                    <div class="field">
                        <span class="label_content">
                            <input type="text" name="contribution_authors_organization" 
                                value="$contribution_authors_organization" id="contribution_authors_organization_uid" size="40"/>
                        </span>
                        
                        <span class="label">
                            authors organization
                        </span>
                    </div>
                    
                    <div class="field">
                        <span class="label_content">
                            <input type="text" name="contribution_contact_email" 
                                value="$contribution_contact_email" id="contribution_contact_email_uid" size="40"
                                onfocus="javascript:select_question_marks_in_input('contribution_contact_email_uid');"
                                onclick="javascript:select_question_marks_in_input('contribution_contact_email_uid');" />
                        </span>
                        
                        <span class="label">
                            contact email
                        </span>
                    </div>

                    <hr/>              
                    
                    <div class="field">
                        <span class="label_content">
                            <input type="text" name="contribution_title" value="$contribution_title" id="contribution_title_uid" size="40"/>
                        </span>
                        
                        <span class="label">
                            title
                        </span>
                    </div>
                    
                    <div class="field">
                        <span class="label_content">
                            <input type="text" name="contribution_keywords"
                                value="$contribution_keywords" id="contribution_keywords_uid" size="40" />
                        </span>
                        
                        <span class="label">
                            keywords
                        </span>
                    </div>
                    
                    <hr/>

                    <p>Choose the simulations that the contribution was designed for:</p>
EOT;

        print_multiple_selection(
            sim_get_all_sim_names(),
            contribution_get_associated_simulation_listing_names($contribution_id)
        );
                    
        print <<<EOT
                    <hr/>
                    
                    <p>Choose the type of contribution:</p>
                    
EOT;
        print_multiple_selection($all_contribution_types, $contribution_types);
        
        print <<<EOT
                    <p>Choose the level of the contribution:</p>
                    
EOT;

        print_multiple_selection(
            contribution_get_all_template_level_names(),
            contribution_get_level_names_for_contribution($contribution_id)
        );

        print <<<EOT
                    <hr/>
                    
                    <p>Remove existing files from the contribution:</p>
EOT;
    
        print_deletable_list(
            contribution_get_contribution_file_names($contribution_id)
        );
    
        print <<<EOT
                    <p>Add any number of files to the contribution:</p>
                    
                    <span class="label_content">
                        <input type="file" name="contribution_file_url" class="multi" />
                    </span>

                    <br/>

                    <div class="button">
                        <input name="submit" type="submit" id="submit" value="$button_name" />
                    </div>


                </fieldset>
                <fieldset>
                    <legend>Optional</legend>
                                        
                    <div class="field">
                        <span class="label_content">
                            <textarea name="contribution_desc" id="contribution_desc_uid" rows="5" cols="40">$contribution_desc</textarea>
                        </span>
                        
                        <span class="label">
                            description
                        </span>
                    </div>
                    
                    
                    <div class="field">
                        <p>Please choose the subject areas covered by the contribution:</p>

EOT;
        
        print_multiple_selection(
            contribution_get_all_template_subject_names(),
            contribution_get_subject_names_for_contribution($contribution_id)
        );
        
        print <<<EOT
                    </div>
                    
                    <div class="field">
EOT;

        print_single_selection(
            "contribution_duration",
            array(
                "0"     => "NA",
                "30"    => "30 minutes",
                "60"    => "60 minutes",
                "120"   => "120 minutes"
            ),
            $contribution_duration
        );

        print <<<EOT
                        
                        <span class="label">
                            duration
                        </span>
                    </div>
                    
                    <div class="field">
EOT;

        print_checkbox(
            "contribution_answers_included",
            "",
            $contribution_answers_included
        );

        print <<<EOT
                        <span class="label">
                            answers included
                        </span>
                    </div>
                    
                    <div class="field">
                        <p>Please describe how the contribution complies with the 
                        <a href="http://www.nap.edu/readingroom/books/nses/html/6a.html">K-12 National Science Standards</a>:
                        </p>
                    
EOT;

        contribution_print_standards_compliance($contribution_standards_compliance);

        print <<<EOT
                    </div>
                    
                    <input type="hidden" name="referrer"        value="$referrer" />
                    <input type="hidden" name="contribution_id" value="$contribution_id" />
                    <input type="hidden" name="action"          value="update" />
                    
                    <div class="button">
                        <input name="submit" type="submit" id="submit" value="$button_name" />
                    </div>
                 </fieldset>
            </form>
EOT;
    }
    
    function contribution_print_summary($contribution, $contributor_id, $contributor_is_team_member, $referrer = '') {
        eval(get_code_to_create_variables_from_array($contribution));
        
        $path_prefix = SITE_ROOT."teacher_ideas/";
        
        $query_string = "?contribution_id=$contribution_id&amp;referrer=$referrer";
        
        $edit    = '';
        $delete  = '';
        $approve = '';
        
        if ($contributor_id !== null && contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {
            $edit   .= "<a href=\"${path_prefix}edit-contribution.php$query_string\">edit</a>";
            $delete .= ", <a href=\"${path_prefix}delete-contribution.php$query_string\">delete</a>";
        
            if ($contributor_is_team_member) {
                if ($contribution_approved) {
                    $approve .= ", <a href=\"${path_prefix}unapprove-contribution.php$query_string\">unapprove</a>";
                }
                else {
                    $approve .= ", <a href=\"${path_prefix}approve-contribution.php$query_string\">approve</a>";
                }
            }
        }
        
        $contribution_link = "${path_prefix}view-contribution.php$query_string";
        
        print "<li><a href=\"$contribution_link\">$contribution_title</a> - ($edit$delete$approve)</li>";        
    }

    function contribution_generate_association_abbr($contribution, $table_name) {
        $desc = "Not Applicable";
        $abbr = "N/A";
        
        if (isset($contribution["${table_name}_desc"])) {
            $desc = $contribution["${table_name}_desc"];
        }
        if (isset($contribution["${table_name}_desc_abbrev"])) {
            $abbr = $contribution["${table_name}_desc_abbrev"];
        }
        
        $abbr = preg_replace('/ *, */', '<br/>', $abbr);
        
        return "<abbr title=\"$desc\">$abbr</abbr>";
    }
    
    function contribution_generate_association_list($table_name, $associations) {
        $is_first = true;
        
        $list = '';
        
        foreach($associations as $association) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $list .= '<br/>';
            }
            
            $desc = $association["${table_name}_desc"];
            $abbr = $association["${table_name}_desc_abbrev"];
            
            $list .= "<abbr title=\"$desc\">$abbr</abbr>";
        }
        
        return $list;
    }
    
    function contribution_print_summary2($contribution_id, $print_sims = true) {
        $contribution = contribution_get_contribution_by_id($contribution_id);
        
        eval(get_code_to_create_variables_from_array($contribution));
            
        $contribution_files = contribution_get_contribution_files($contribution_id);

        $contribution_levels = contribution_get_levels_for_contribution($contribution_id);
        $contribution_types  = contribution_get_types_for_contribution($contribution_id);
        $contribution_sims   = contribution_get_associated_simulation_listings($contribution_id);
        
        $sim_list = '';
        
        $is_first = true;
        
        foreach($contribution_sims as $listing) {
            eval(get_code_to_create_variables_from_array($listing));
            
            $sim = sim_get_sim_by_id($sim_id);
            
            eval(get_code_to_create_variables_from_array($sim));
            
            if ($is_first) {
                $is_first = false;
            }
            else {
                $sim_list .= '<br/>';
            }
            
            $sim_list .= '<a href="'.SITE_ROOT.'simulations/sims.php?sim_id='.$sim_id.'">'.$sim_name.'</a>';
        }
        
        $contribution_authors = explode(',', $contribution_authors);
        
        $contribution_author = $contribution_authors[0];
        
        $parsed_name = parse_name($name);
        
        $author_first_initial = $parsed_name['first_initial'];
        $author_last_name     = $parsed_name['last_name'];

        $level_list = contribution_generate_association_list('contribution_level', $contribution_levels);
        $type_list  = contribution_generate_association_list('contribution_type',  $contribution_types);
        
        $time = strtotime($contribution_date_updated);
        
        $contribution_date_updated = date('n/y', $time);
        
        print <<<EOT
            <tr>
                <td>
                    $contribution_title
                </td>
            
                <td>
                    $author_first_initial. $author_last_name
                </td>
            
                <td>
                    $level_list            
                </td>
            
                <td>
                    $type_list
                </td>
EOT;

        if ($print_sims) {
            print <<<EOT
                <td>
                    $sim_list
                </td>
EOT;
        }
    
        print <<<EOT
                <td>
                    $contribution_date_updated
                </td>
            </tr>
EOT;
    }

    function contribution_print_summary3($contribution, $print_sims = true) {    
        global $referrer;
        
        eval(get_code_to_create_variables_from_array($contribution));
            
        $contribution_files = contribution_get_contribution_files($contribution_id);
        
        $sim_list = "None";
        
        if (isset($sim_name)) {
            $sim_list = '';
            
            $is_first = true;
            
            foreach(preg_split('/ *, */', $sim_name) as $sim) {
                $simulation = sim_get_sim_by_name($sim);
                
                if ($is_first) {
                    $is_first = false;
                }
                else {
                    $sim_list .= '<br/>';
                }
                
                $sim_list .= '<a href="'.SITE_ROOT.'simulations/sims.php?sim_id='.$simulation['sim_id'].'">'.$simulation['sim_name'].'</a>';
            }
        }
        
        $level_list = contribution_generate_association_abbr(
            $contribution, 'contribution_level'
        );
        
        $type_list = contribution_generate_association_abbr(
            $contribution, 'contribution_type'
        );

        $contribution_authors = explode(',', $contribution_authors);
    
        $contribution_author = $contribution_authors[0];
        
        $matches = array();
        
        if (preg_match('/([a-zA-Z])[a-zA-Z]+ ([a-zA-Z ]+\. +)?+([^.]+)$/i', $contribution_author, $matches) == 1) {    
            $author_first_initial = $matches[1];
            $author_last_name     = $matches[3];
        }
        else {
            $contribution_author  = 'John Doe';
            $author_first_initial = 'J';
            $author_last_name     = 'Doe';
        }
        
        $time = strtotime($contribution_date_updated);
        
        $contribution_date_updated = date('n/y', $time);
        
        $author_html = "<abbr title=\"$contribution_author\">$author_first_initial. $author_last_name</abbr>";
        
        $title_html = <<<EOT
                <a href="../teacher_ideas/view-contribution.php?contribution_id=$contribution_id&amp;referrer=$referrer">$contribution_title</a>
EOT;
        
        print <<<EOT
            <tr>
                <td>
                    $title_html
                </td>
            
                <td>
                    $author_html
                </td>
            
                <td>
                    $level_list            
                </td>
            
                <td>
                    $type_list
                </td>
EOT;

        if ($print_sims) {
            print <<<EOT
                <td>
                    $sim_list
                </td>
EOT;
        }
    
        print <<<EOT
                <td>
                    $contribution_date_updated
                </td>
            </tr>
EOT;
    }

    function contribution_get_contribution_file_names($contribution_id) {
        $contribution_file_names = array();
        
        $contribution_files = contribution_get_contribution_files($contribution_id);
        
        foreach($contribution_files as $contribution_file) {
            $name = create_deletable_item_control_name('contribution_file_url', $contribution_file['contribution_file_id']);
            
            $original_file_name = contribution_extract_original_file_name($contribution_file['contribution_file_url']);
            
            $contribution_file_names[$name] = $original_file_name;
        }
        
        return $contribution_file_names;
    }
    
    function contribution_delete_contribution_file($contribution_file_id) {
        $condition = array( 'contribution_file_id' => $contribution_file_id );
        
        db_delete_row('contribution_file', $condition);
        
        return true;
    }
    
    function contribution_delete_all_files_not_in_list($contribution_id, $files_to_keep) {
        $all_files = array();

        $contribution_files = contribution_get_contribution_files($contribution_id);
        
        foreach($contribution_files as $contribution_file) {
            $all_files[] = $contribution_file['contribution_file_id'];
        }
        
        $files_to_delete = array_diff($all_files, $files_to_keep);
        
        foreach($files_to_delete as $file_to_delete) {
            contribution_delete_contribution_file($file_to_delete);
        }
        
        return true;
    }
    
    function contribution_get_contribution_files($contribution_id) {
        $contribution_files = array();
        
        $contribution_file_rows = db_exec_query("SELECT * FROM `contribution_file` WHERE `contribution_id`='$contribution_id' ");
        
        while ($contribution = mysql_fetch_assoc($contribution_file_rows)) {
            $contribution_files[] = format_for_html($contribution);
        }
        
        return $contribution_files;
    }
    
    function contribution_can_contributor_manage_contribution($contributor_id, $contribution_id) {
        if ($contribution_id == -1) return true;

        $contribution = contribution_get_contribution_by_id($contribution_id);
        $contributor  = contributor_get_contributor_by_id($contributor_id);        
        
        return $contribution['contributor_id'] == $contributor_id || 
               $contributor['contributor_is_team_member'] == '1' ||
               $contribution['contributor_id'] == '-1';
    }
    
    function contribution_get_manageable_contributions_for_contributor_id($contributor_id) {
        $contributions = contribution_get_all_contributions();
        
        foreach($contributions as $index => $contribution) {
            if (!contribution_can_contributor_manage_contribution($contributor_id, $contribution['contribution_id'])) {
                unset($contributions[$index]);
            }
        }
        
        return $contributions;
    }
    
    function contribution_delete_contribution($contribution_id) {
        $condition = array( 'contribution_id' => $contribution_id );
        
        db_delete_row('contribution',            $condition);
        db_delete_row('contribution_file',       $condition);
        db_delete_row('simulation_contribution', $condition);
        db_delete_row('contribution_type',       $condition);
        db_delete_row('contribution_level',      $condition);
        db_delete_row('contribution_subject',    $condition);        
        db_delete_row('contribution_comment',    $condition);            
        db_delete_row('contribution_flagging',   $condition);            
        db_delete_row('contribution_nomination', $condition);                            
        
        return true;
    }
    
    function contribution_update_contribution($contribution) {        
        if (!db_update_table('contribution', $contribution, 'contribution_id', $contribution['contribution_id'])) {
            return false;
        }
        
        return true;
    }
    
    function contribution_delete_all_multiselect_associations($assoc_name, $contribution_id) {
        db_exec_query("DELETE FROM `$assoc_name` WHERE `contribution_id`='$contribution_id' AND `${assoc_name}_is_template`='0' ");
        
        return true;
    }
    
    function contribution_get_association_abbreviation_desc($table_name, $text) {
        $result = db_exec_query("SELECT * FROM `$table_name` WHERE `${table_name}_desc`='$text' AND `${table_name}_is_template`='1' ");
        
        if (!$result) {
            return abbreviate($text);
        }
        
        $first_row = mysql_fetch_assoc($result);
        
        if (!$first_row) {
            return abbreviate($text);
        }
        
        $abbrev = $first_row["${table_name}_desc_abbrev"];
        
        if ($abbrev == '') {
            $abbrev = abbreviate($text);
        }
        
        return $abbrev;
    }
    
    function contribution_create_multiselect_association($contribution_id, $multiselect_control_name, $text) {
        $matches = array();
        
        if (preg_match('/multiselect_([a-zA-Z0-9_]+)_id_([0-9]+)$/i', $multiselect_control_name, $matches) !== 1) {
            return false;
        }
        
        $table_name = $matches[1];
        
        $result = db_exec_query("SELECT * FROM `$table_name` WHERE `${table_name}_desc`='$text' AND `contribution_id`='$contribution_id' ");
        
        if ($first_row = mysql_fetch_assoc($result)) {   
            $id = $first_row["${table_name}_id"];
            
            return $id;
        }
        else {                        
            $id = db_insert_row(
                $table_name,
                array(
                    "${table_name}_desc"        => $text,
                    "${table_name}_desc_abbrev" => contribution_get_association_abbreviation_desc($table_name, $text),
                    "${table_name}_is_template" => '0',
                    'contribution_id'           => $contribution_id
                )
            );
            
            return $id;
        }
    }
    
    function contribution_add_new_contribution($contribution_title, $contributor_id, $file_tmp_name = null, $file_user_name = null) {    
        if ($file_tmp_name != null) {
            if (preg_match('/.+\\.(doc|txt|rtf|pdf|odt)/i', $file_user_name) == 1) {
                $contribution_type = "Activity";
            }
            else if (preg_match('/.+\\.(ppt|odp)/i', $file_user_name) == 1) {
                $contribution_type = "Lecture";
            }
            else {
                $contribution_type = "Support";
            }
        }
        else {
            $contribution_type = "Activity";
        }
        
        $contribution_id = db_insert_row(
            'contribution',
            array(
                'contribution_title'        => $contribution_title,
                'contributor_id'            => $contributor_id,
                'contribution_date_created' => date('YmdHis')
            )
        );
        
        if ($file_tmp_name != null) {
            if (contribution_add_new_file_to_contribution($contribution_id, $file_tmp_name, $file_user_name) == FALSE) {
                return FALSE;
            }
        }

        contribution_create_multiselect_association($contribution_id, 
            'multiselect_contribution_type_id_0', $contribution_type
        );
        
        return $contribution_id;
    }
    
    function contribution_add_new_file_to_contribution($contribution_id, $file_tmp_name, $file_user_name) {
        $this_dir = dirname(__FILE__);

        $new_file_dir_rel = "admin/uploads/contributions";        
        $new_file_dir_abs = "$this_dir/uploads/contributions";
        
        mkdirs($new_file_dir_abs);
        
        $new_name = basename($file_tmp_name)."_$file_user_name";
        
        $new_file_path_rel = "$new_file_dir_rel/$new_name";
        $new_file_path_abs = "$new_file_dir_abs/$new_name";
        
        $file_size = filesize($file_tmp_name);
        
        if (move_uploaded_file($file_tmp_name, $new_file_path_abs)) {
            $contribution_file_id = db_insert_row(
                'contribution_file',
                array(
                    'contribution_id'        => $contribution_id,
                    'contribution_file_url'  => $new_file_path_rel,
                    'contribution_file_size' => $file_size
                )
            );
            
            return $contribution_file_id;
        }
        
        return FALSE;
    }
    
    function contribution_unassociate_contribution_with_all_simulations($contribution_id) {
        db_exec_query("DELETE FROM `simulation_contribution` WHERE `contribution_id`='$contribution_id' ");
        
        return true;
    }
    
    function contribution_associate_contribution_with_simulation($contribution_id, $sim_id) {
        $simulation_contribution_id = db_insert_row(
            'simulation_contribution',
            array(
                'sim_id'          => $sim_id,
                'contribution_id' => $contribution_id
            )
        );
        
        return $simulation_contribution_id;
    }

    function contribution_get_all_level_names() {
        $levels = array();
        
        $contribution_level_rows = db_exec_query("SELECT * FROM `contribution_level` ORDER BY `contribution_level_desc` ASC ");
        
        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];
            
            $name = create_multiselect_control_name('contribution_level', $id);
            
            $levels[$name] = format_for_html($contribution_level['contribution_level_desc']);
        }
        
        return array_unique($levels);
    }

    function contribution_get_all_template_levels() {
        $levels = array();
        
        $contribution_level_rows = db_exec_query("SELECT * FROM `contribution_level` WHERE `contribution_level_is_template`='1' ORDER BY `contribution_level_desc` ASC ");
        
        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];
            
            $name = create_multiselect_control_name('contribution_level', $id);
            
            $levels[$name] = $contribution_level;
        }
        
        return $levels;
    }
    
    function contribution_get_all_template_level_names() {
        $levels = contribution_get_all_template_levels();
        
        $level_names = array();
        
        foreach($levels as $key => $level) {
            $level_names[$key] = $level['contribution_level_desc'];
        }
        
        return array_unique($level_names);
    }
    
    function contribution_get_levels_for_contribution($contribution_id) {
        $levels = array();
        
        $contribution_level_rows = db_exec_query("SELECT * FROM `contribution_level` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_level_desc` ASC ");
        
        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];
            
            $name = create_multiselect_control_name('contribution_level', $id);
            
            $levels[$name] = $contribution_level;
        }
        
        return $levels;
    }
    
    function contribution_get_level_names_for_contribution($contribution_id) {
        $levels = contribution_get_levels_for_contribution($contribution_id);
        
        $level_names = array();
        
        foreach($levels as $key => $level) {
            $level_names[$key] = $level['contribution_level_desc'];
        }
        
        return $level_names;
    }

    function contribution_get_all_subject_names() {
        $subjects = array();
        
        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $name = create_multiselect_control_name('contribution_subject', $id);
            
            $subjects[$name] = format_for_html($contribution_subject['contribution_subject_desc']);
        }
        
        return array_unique($subjects);
    }
    
    function contribution_get_all_template_subjects() {
        $subjects = array();
        
        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $name = create_multiselect_control_name('contribution_subject', $id);
            
            $subjects[$name] = format_for_html($contribution_subject);
        }
        
        return $subjects;
    }
    
    function contribution_get_all_template_subject_names() {
        $subjects = array();
        
        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $name = create_multiselect_control_name('contribution_subject', $id);
            
            $subjects[$name] = $contribution_subject['contribution_subject_desc'];
        }
        
        return array_unique($subjects);
    }
    
    function contribution_get_subject_names_for_contribution($contribution_id) {
        $subjects = array();
        
        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $name = create_multiselect_control_name('contribution_subject', $id);
            
            $subjects[$name] = $contribution_subject['contribution_subject_desc'];
        }
        
        return $subjects;
    }
    
    function contribution_get_subjects_for_contribution($contribution_id) {
        $subjects = array();
        
        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_subject_desc` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $name = create_multiselect_control_name('contribution_subject', $id);
            
            $subjects[$name] = format_for_html($contribution_subject);
        }
        
        return $subjects;
    }    

    function contribution_get_all_type_names() {
        $types = array();
        
        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` ORDER BY `contribution_type_desc` ASC ");
        
        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];
        
            $name = create_multiselect_control_name('contribution_type', $id);
        
            $types[$name] = "$type";
        }
        
        return array_unique($types);
    }
    
    function contribution_get_all_template_type_names() {
        $types = array();
        
        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_type_is_template` = '1' ORDER BY `contribution_type_desc` ASC ");
        
        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];
        
            $name = create_multiselect_control_name('contribution_type', $id);
        
            $types[$name] = "$type";
        }
        
        return array_unique($types);
    }

    function contribution_get_types_for_contribution($contribution_id) {
        $types = array();
        
        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_id`='$contribution_id' ");
        
        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
        
            $name = create_multiselect_control_name('contribution_type', $id);
        
            $types[$name] = $contribution_type;
        }
        
        return $types;
    }
    
    function contribution_get_type_names_for_contribution($contribution_id) {
        $types = array();
        
        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_id`='$contribution_id' ");
        
        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];
        
            $name = create_multiselect_control_name('contribution_type', $id);
        
            $types[$name] = "$type";
        }
        
        return $types;
    }    

    function contribution_get_associated_simulation_listing_names($contribution_id) {
        $simulation_rows = db_exec_query("SELECT * FROM `simulation`,`simulation_contribution` WHERE `simulation`.`sim_id`=`simulation_contribution`.`sim_id` AND `simulation_contribution`.`contribution_id`='$contribution_id' ");
        
        $simulations = array();
        
        while ($simulation = mysql_fetch_assoc($simulation_rows)) {
            $id         = $simulation['sim_id'];
            $sim_name   = $simulation['sim_name'];
            
            $simulations["sim_id_$id"] = "$sim_name";
        }
        
        return $simulations;
    }
    
    function contribution_get_associated_simulation_listings($contribution_id) {
        $simulation_contribution_rows = 
            db_exec_query("SELECT * FROM `simulation_contribution` WHERE `contribution_id`='$contribution_id' ");
        
        $simulation_contributions = array();
        
        while ($simulation_contribution = mysql_fetch_assoc($simulation_contribution_rows)) {
            $id = $simulation_contribution['sim_id'];
            
            $simulation_contributions["sim_id_$id"] = format_for_html($simulation_contribution);
        }
        
        return $simulation_contributions;
    }
    
    function contribution_set_approved($contribution_id, $status) {
        if ($status) {
            $status = '1';
        }
        else {
            $status = '0';
        }
        
        return db_update_table('contribution', array( 'contribution_approved' => $status ), 'contribution_id', $contribution_id);
    }
    
    function contribution_explode_contribution_by_array($contribution, $array) {
        $new_contribs = array();
        
        if (count($array) == 0) {
            return array( $contribution );
        }
        
        // Execute join:
        foreach($array as $element) {
            $new_contrib = $contribution;
            
            foreach($element as $key => $value) {
                $new_contrib["$key"] = format_for_html("$value");
            }
            
            $new_contribs[] = $new_contrib;
        }
        
        return $new_contribs;
    }
    
    function contribution_explode_contributions($contributions) {
        // Index by number:
        $contributions = array_values($contributions);
        
        $exploded = array();
        
        $explosion_functions = array(
            'contribution_get_levels_for_contribution',
            'contribution_get_types_for_contribution',
            'contribution_get_associated_simulation_listings',
        );
        
        foreach($explosion_functions as $explosion_function) {
            $exploded = array();
            
            foreach($contributions as $contribution) {
                $contribution_id = $contribution['contribution_id'];
                
                $array = call_user_func($explosion_function, $contribution_id);
            
                $new_contribs = contribution_explode_contribution_by_array($contribution, $array);
                
                $exploded = array_merge($exploded, $new_contribs);
            }
            
            $contributions = $exploded;
        }
        
        // Join simulation data:
        $final = array();
        
        foreach($exploded as $contribution) {
            if (isset($contribution['sim_id'])) {
                $simulation = sim_get_sim_by_id($contribution['sim_id']);
        
                if (is_array($simulation)) {
                    foreach($simulation as $key => $value) {
                        $contribution["$key"] = format_for_html("$value");
                    }
                }
            }
        
            $final[] = $contribution;
        }
        
        return $final;
    }

    function contribution_get_all_contributions() {
        $contributions = array();
        
        $contribution_rows = db_exec_query("SELECT * FROM `contribution` ORDER BY `contribution_title` ASC");
        
        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contribution_id = $contribution['contribution_id'];
            
            $contributions["$contribution_id"] = format_for_html($contribution);
        }
        
        return $contributions;
    }
    
    function contribution_get_all_contributions_for_sim($sim_id) {
        $contributions = array();
        
        $contribution_rows = db_exec_query("SELECT * FROM `contribution` , `simulation_contribution` WHERE `contribution` . `contribution_id` = `simulation_contribution` . `contribution_id` AND `simulation_contribution` . `sim_id` = '$sim_id' ORDER BY `contribution_title` ASC");
        
        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contributions[] = format_for_html($contribution);
        }
        
        return $contributions;
    }
    
    function contribution_get_approved_contributions_for_sim($sim_id) {
        $contributions = contribution_get_all_contributions_for_sim($sim_id);
        
        foreach($contributions as $index => $contribution) {
            if ($contribution['contribution_approved'] == '0') {
                unset($contributions[$index]);
            }
        }
        
        return $contributions;
    }
    
    function contribution_get_contribution_by_id($contribution_id) {
        $contribution_rows = db_exec_query("SELECT * FROM `contribution` WHERE `contribution_id`='$contribution_id' ");
        
        return format_for_html(mysql_fetch_assoc($contribution_rows));
    }
    
    function contributor_get_all_contributors() {
        $contributors = array();
        
        $contributor_rows = 
        db_exec_query("SELECT * from `contributor` ORDER BY `contributor_name` ASC ");
        
        while ($contributor = mysql_fetch_assoc($contributor_rows)) {
            $contributors[] = format_for_html($contributor);
        }
        
        return $contributors;
    }
    
    function contributor_is_contributor($username) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return true;
            }
        }
        
        return false;
    }

    function contributor_send_password_reminder($username) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                $contributor_name     = $contributor['contributor_name'];
                $contributor_password = $contributor['contributor_password'];
                
                mail($username, 
                     "PhET Password Reminder", 
                     "\n".
                     "Dear $contributor_name, \n".
                     "\n".
                     "Your password is \"$contributor_password.\"\n".
                     "\n".
                     "Regards,\n".
                     "\n".
                     "The PhET Team \n",
                
                     "From: The PhET Team <phethelp@colorado.edu>");
            }
        }
    }
    
    function contributor_get_team_members() {
        $admins = array();
        
        $contributor_rows = db_exec_query("SELECT * from `contributor` WHERE `contributor_is_team_member`='1' ORDER BY `contributor_name` ASC ");
        
        while ($contributor = mysql_fetch_assoc($contributor_rows)) {
            $admins[] = format_for_html($contributor);
        }
        
        return $admins;
    }
    
    function contributor_is_admin_username($username) {
        $admins = contributor_get_team_members();
        
        foreach($admins as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return true;
            }
        }
        
        return false;
    }    

    function contributor_get_id_from_username($username) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return $contributor['contributor_id'];
            }
        }
        
        return false;        
    }
    
    function contributor_get_id_from_username_and_password($username, $password) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                if ($contributor['contributor_password'] == $password) {
                    return $contributor['contributor_id'];
                }
            }
        }
        
        return false;        
    }
    
    function contributor_get_id_from_username_and_password_hash($username, $password_hash) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {            
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                if (md5($contributor['contributor_password']) == $password_hash) {
                    return $contributor['contributor_id'];
                }
            }
        }
        
        return false;        
    }

    function contributor_is_valid_login($username, $password_hash) {
        return contributor_get_id_from_username_and_password_hash($username, $password_hash) !== false;
    }
    
    function contributor_is_valid_admin_login($username, $password_hash) {
        if (!contributor_is_admin_username($username)) return false;
        
        return contributor_is_valid_login($username, $password_hash);
    }
    
    function contributor_print_quick_login() {
        print <<<EOT
            <div class="field">
                <span class="label">Name</span>
                <span class="label_content">                
                    <input type="text" size="25" name="contributor_name" id="contributor_name_uid" onchange="javascript:on_email_entered();"/>
                </span>
            </div>
        
            <div id="required_login_info_uid">
            
            </div>
EOT;
    }
    
    function contributor_add_new_blank_contributor($is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';
        
        db_exec_query("INSERT INTO `contributor` (`contributor_is_team_member`) VALUES ('$team_member_field') ");
        
        return mysql_insert_id();
    }
    
    function contributor_add_new_contributor($username, $password, $is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';
        
        db_exec_query("INSERT INTO `contributor` (`contributor_email`, `contributor_password`, `contributor_is_team_member`) VALUES ('$username', '$password', '$team_member_field') ");
        
        return mysql_insert_id();
    }
    
    function contributor_get_contributor_by_id($contributor_id) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_id`='$contributor_id' ");

        if (!$result) {
            return false;
        }
        
        return format_for_html(mysql_fetch_assoc($result));
    }
    
    function contributor_get_contributor_by_name($contributor_name) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_name`='$contributor_name' ");
        
        if (!$result) {
            return false;
        }
        
        return format_for_html(mysql_fetch_assoc($result));
    }
    
    function contributor_get_contributor_by_email($contributor_email) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_email`='$contributor_email' ");
        
        if (!$result) {
            return false;
        }
        
        return format_for_html(mysql_fetch_assoc($result));
    }
    
    function contributor_print_full_edit_form($contributor_id, $script, $optional_message = null, 
                                              $standard_message = "<p>You may edit your profile information below.</p>") {

                                                  
        $contributor = contributor_get_contributor_by_id($contributor_id);
        
        eval(get_code_to_create_variables_from_array($contributor));
        
        $contributor_receive_email_checked = $contributor_receive_email == '1' ? 'checked="checked"' : '';     
        
        print <<<EOT
            <form id="userprofileform" method="post" action="$script">
                <fieldset>
                    <legend>Profile for $contributor_name</legend>
EOT;

        if ($optional_message !== null) {
            print "$optional_message";
        }

        print <<<EOT
                    $standard_message
                    
                    <div class="form_content">
                        <div class="field">
                            <span class="label_content">
                                <input type="password" name="contributor_password" 
                                    value="$contributor_password" id="password" size="25"/>                        
                            </span>
                            
                            <span class="label">password</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_name" 
                                    value="$contributor_name" id="name" size="25"/>
                            </span>
                            
                            <span class="label">name</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_organization" 
                                    value="$contributor_organization" id="contributor_organization" size="25"/>
                            </span>
                            
                            <span class="label">organization</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_title"
                                    value="$contributor_title" id="title" size="25" />
                            </span>
                            
                            <span class="label">title</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_address"
                                    value="$contributor_address" id="address" size="25" />
                            </span>
                            
                            <span class="label">address</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_office"
                                    value="$contributor_office" id="office" size="15" />
                            </span>
                            
                            <span class="label">office</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_city"
                                    value="$contributor_city" id="city" size="15" />
                            </span>
                            
                            <span class="label">city</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_state"
                                    value="$contributor_state" id="state" size="15" />
                            </span>
                            
                            <span class="label">state/province</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_postal_code"
                                    value="$contributor_postal_code" id="postal_code" size="10" />
                            </span>
                            
                            <span class="label">postal code</span>
                        </div>
                    
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_primary_phone"
                                    value="$contributor_primary_phone" id="primary_phone" size="12" />
                            </span>
                            
                            <span class="label">primary phone</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_secondary_phone"
                                    value="$contributor_secondary_phone" id="secondary_phone" size="12" />
                            </span>
                            
                            <span class="label">secondary phone</span>
                        </div>
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="text" name="contributor_fax"
                                    value="$contributor_fax" id="fax" size="12" />
                            </span>                        
                            
                            <span class="label">fax</span>
                        </div>
                    
                        <input type="hidden" name="contributor_receive_email" value="0" />
                    
                        <div class="field">
                            <span class="label_content">
                                <input type="checkbox" name="contributor_receive_email"
                                    value="1" $contributor_receive_email_checked>
                            </span>
                            
                            <span class="label">receive phet email</span>                            
                        </div>
                    </div>

                   <input class="button" name="Submit" type="submit" id="submit" value="Done" />
                 </fieldset>
            </form>
EOT;
    }
    
    function contributor_delete_contributor($contributor_id) {
        db_exec_query("DELETE FROM `contributor` WHERE `contributor_id`='$contributor_id' ");
        
        return true;
    }
    
    function contributor_update_contributor($contributor_id, $update_array) {
        return db_update_table('contributor', $update_array, 'contributor_id', $contributor_id);
    }
    
    function contributor_update_contributor_from_script_parameters($contributor_id) {
        $params = array();
        
        foreach($_REQUEST as $key => $value) {
            if ("$key" !== "contributor_id") {
                if (preg_match('/contributor_.*/', "$key") == 1) {
                    $params["$key"] = mysql_real_escape_string("$value");
                }
            }
        }
        
        contributor_update_contributor($contributor_id, $params);
    }
    
    function contributor_gather_fields_into_globals($contributor_id) {
        $contributor = contributor_get_contributor_by_id($contributor_id);
        
        foreach($contributor as $key => $value) {
            $GLOBALS["$key"] = "$value";
        }
    }

?>