<?php
	if (!defined('SITE_ROOT')) {
		include_once('../admin/global.php');
	}
    
    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/authentication.php");    
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php"); 
    include_once(SITE_ROOT."admin/sim-utils.php");  

	define('DEFAULT_CONTRIBUTOR_DESC', 'I am a teacher who uses PhET in my classes');
    
    function contribution_search_for_contributions($search_for) {
    	return db_search_for(
			'contribution', 
			$search_for, 
			array('contribution_title', 'contribution_desc', 'contribution_keywords', 'contribution_authors')
		);
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
    
    function contribution_get_files_listing_html($contribution_id) {
        $files_html = '<p>No files</p>';
        
        $files = contribution_get_contribution_files($contribution_id);
        
        if (count($files) > 0) {
            $files_html = '<ul>';
        
            foreach($files as $file) {
                $contribution_file_id   = $file['contribution_file_id'];
                $contribution_file_name = $file['contribution_file_name'];
                $contribution_file_size = $file['contribution_file_size'];
                
                $kb = ceil($contribution_file_size / 1024);
                    
                $files_html .= "<li><a href=\"../admin/get-upload.php?contribution_file_id=$contribution_file_id\">".
                               "$contribution_file_name</a>".
                               " - $kb KB</li>";
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
            
            $sim_url = sim_get_url_to_sim_page($sim_id);
            
            $simulations_html .= "<li>$delete <a href=\"$sim_url\">$sim_name</a></li>";
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

	function print_new_account_form($script, $button_label, $print_password = false, $wide = false, $other_html = '') {
		if (isset($GLOBALS['contributor_email'])) {
			$contributor_email 		   = $GLOBALS['contributor_email'];
			$contributor_name  		   = get_global_opt('contributor_name');
			$contributor_organization  = get_global_opt('contributor_organization');
			$contributor_desc          = get_global_opt('contributor_desc');
			$contributor_password      = get_global_opt('contributor_password');
		}
		else if (isset($_REQUEST['contributor_email'])) {
			$contributor_email 		   = $_REQUEST['contributor_email'];
			$contributor_name  		   = get_request_opt('contributor_name');
			$contributor_organization  = get_request_opt('contributor_organization');
			$contributor_desc          = get_request_opt('contributor_desc');
			$contributor_password      = get_request_opt('contributor_password');
		}
		else {
			$contributor_email 		   = '';
			$contributor_name  		   = '';
			$contributor_organization  = '';
			$contributor_desc          = DEFAULT_CONTRIBUTOR_DESC;
			$contributor_password      = '';
		}
		
		print <<<EOT
			<form method="post" action="$script">
				<fieldset>
					<legend>$button_label</legend>
					
					<table class="form">							
						<tr>
							<td>description*</td>	

							<td>
EOT;

						contributor_print_desc_list($contributor_desc, $wide);

						print <<<EOT
							</td>
						</tr>

						<tr>
							<td>email*</td>		<td><input id="contributor_email_uid" type="text" size="15" name="contributor_email" value="$contributor_email" onkeyup="javascript:on_email_change_guess_name();" class="always-enabled"/></td>
						</tr>
EOT;

						if ($print_password) {
							print <<<EOT
								<tr>
									<td>password*</td>		<td><input id="contributor_password_uid" type="password" size="15" name="contributor_password"  value="$contributor_password" class="always-enabled"/></td>
								</tr>
EOT;
						}

						print <<<EOT

						<tr>
							<td>name*</td>		<td><input id="contributor_name_uid" type="text" size="15" name="contributor_name"  value="$contributor_name" class="always-enabled"/></td>
						</tr>

						<tr>
							<td>organization*</td> <td><input id="contributor_organization_uid" type="text" size="15" name="contributor_organization"  value="$contributor_organization" class="always-enabled"/></td>
						</tr>

						<tr>
							<td colspan="2"><input type="submit" name="submit" value="$button_label" class="always-enabled auto-width"/></td>
						</tr>
					</table>
					
					<div>
						<input type="hidden" name="create_new_account" value="1" class="always-enabled" />

						$other_html
					</div>
				</fieldset>
			</form>
EOT;
	}
    
    function print_contribute_login_form($script, $contribution_id, $referrer, $other_html = '') {
		if (isset($GLOBALS['contributor_email'])) {
			$contributor_email 		   = $GLOBALS['contributor_email'];
			$contributor_password      = $GLOBALS['contributor_password'];
		}
		else if (isset($_REQUEST['contributor_email'])) {
			$contributor_email 		   = $_REQUEST['contributor_email'];
			$contributor_password      = $_REQUEST['contributor_password'];
		}
		else {
			$contributor_email 		   = '';
			$contributor_password      = '';
		}
		
        print <<<EOT
            <div id="twofacelogin" class="table_container">
			$other_html
			
            <table class="top">
                <tr>
                    <td>
                        <form method="post" action="$script">
                            <fieldset>
                                <legend>Login</legend>

                                <table class="form">
                                    <div class="horizontal_center">

                                        <tr>
                                            <td>email*</td>

                                            <td>
                                                <input type="text" size="15" name="contributor_email" value="$contributor_email" class="always-enabled" />
                                            </td>
                                        </tr>

                                        <tr>
                                            <td>password*</td>

                                            <td>
                                                <input type="password" size="15" name="contributor_password" value="$contributor_password"  class="always-enabled" />
                                            </td>
                                        </tr>

                                        <td colspan="2">
                                            <input type="submit" name="submit" value="Login" class="always-enabled auto-width"/>
                                        </td>
                                    </div>
                                </table>
                                
                                <input type="hidden" name="referrer"        value="$referrer"        class="always-enabled"/>
                                <input type="hidden" name="contribution_id" value="$contribution_id" class="always-enabled"/>
								<input type="hidden" name="login_required"  value="true"             class="always-enabled"/>
                            </fieldset>
                        </form>
                    </td>

                    <td>
EOT;

					$other_fields = <<<EOT
					<input type="hidden" name="referrer"        value="$referrer"        class="always-enabled"/>
                    <input type="hidden" name="contribution_id" value="$contribution_id" class="always-enabled"/>
					<input type="hidden" name="login_required"  value="true"             class="always-enabled"/>
EOT;

					print_new_account_form("$script", "New Account", true, false, $other_fields);

					print <<<EOT
                    </td>
                </tr>
            </table>
            </div>

            <script type="text/javascript">
                 /*<![CDATA[*/
            
                // This code will disable everything on the page, except the login stuff
                $(document).ready(
                    function() {
                        $('input').not('.always-enabled').disable();
                        $('select').not('.always-enabled').disable();
						$('textarea').not('.always-enabled').disable();
                        $('input.button').enable();
                    }
                );
                
                /*]]>*/
            </script>
EOT;
    }
    
    function contribution_establish_multiselect_associations_from_script_params($contribution_id) {
        $files_to_keep = array();
        
        // Now have to process multiselect controls:
        foreach($_REQUEST as $key => $value) {
            $matches = array();
            
            if (is_multiple_selection_control("$key")) {
                contribution_create_multiselect_association($contribution_id, $key, $value);
            }
            else if (is_deletable_item_control("$key")) {
                // We have to keep a file; extract the ID from the name:
                $contribution_file_id = get_deletable_item_control_id("$key");
                
                $files_to_keep[] = "$contribution_file_id";
            }
            else if (preg_match('/sim_id_([0-9]+)/i', "$key", $matches) == 1) {
                $sim_id = $matches[1];
                
                contribution_associate_contribution_with_simulation($contribution_id, $sim_id);
            }
        }
        
        return $files_to_keep;
    }
    
    function contribution_print_full_edit_form($contribution_id, $script, $referrer, $button_name = 'Update') {
        global $contributor_authenticated;
        
        $contribution = contribution_get_contribution_by_id($contribution_id);

        if (!$contribution) {
            // Allow 'editing' of non-existent contributions:
            $contribution = db_get_blank_row('contribution');
        }
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        do_authentication(false);
        
        if ($contributor_id <= 0 && isset($GLOBALS['contributor_id'])) {
            // The contribution didn't have any owner; assume the owner is the current editor:
            $contributor_id = $GLOBALS['contributor_id'];
        }

        $contributor_is_team_member = false;

        // Set reasonable defaults:
        if ($contributor_authenticated) {
			$contributor_is_team_member = $GLOBALS['contributor_is_team_member'];
			
            if ($contribution_authors_organization == '') {
                $contribution_authors_organization = $GLOBALS['contributor_organization'];
            }
            if ($contribution_contact_email == '') {
                $contribution_contact_email = $GLOBALS['contributor_email'];
            }
            if ($contribution_authors == '') {
                $contribution_authors = $GLOBALS['contributor_name'];
            }
        }
        if ($contribution_keywords == '' && isset($GLOBALS['sim_id'])) {
            $simulation = sim_get_sim_by_id($GLOBALS['sim_id']);
            
            $contribution_keywords = $simulation['sim_keywords'];
        }
        
        $all_contribution_types = contribution_get_all_template_type_names();
        $contribution_types     = contribution_get_type_names_for_contribution($contribution_id);
        
        if (!$contributor_authenticated) {
            if (!isset($_REQUEST['loginmethod']) || strtolower($_REQUEST['loginmethod']) == 'static') {
                if ($contribution_id == -1) {
                    print_contribute_login_form('../teacher_ideas/contribute.php', -1, $referrer);
                }
                else {
                    print_contribute_login_form('../teacher_ideas/edit-contribution.php', $contribution_id, $referrer);
                }
            }
        }

        print <<<EOT
            <form id="contributioneditform" method="post" action="$script" enctype="multipart/form-data">
                <fieldset>
                    <legend>Required</legend>

					<table class="form">
EOT;

        if (!$contributor_authenticated) {
            if (isset($_REQUEST['loginmethod']) && strtolower($_REQUEST['loginmethod']) == 'dynamic') {
                contributor_print_quick_login();
            }
        }

		if ($contributor_is_team_member) {
			print <<<EOT
					<tr>
						<td>
	                        contributor*
	                    </td>
	
						<td>
EOT;

							$contributor_names = array();

							foreach (contributor_get_all_contributors() as $c) {
								if (strlen(trim($c['contributor_name'])) > 0) {
									$contributor_names[$c['contributor_id']] = $c['contributor_name'];
								}
							}
			
							$current_contributor = contributor_get_contributor_by_id($contributor_id);
							$current_contributor_name = $current_contributor['contributor_name'];
			
							print_single_selection(
					            "new_contributor_id",
					            $contributor_names,
					            $current_contributor_name
					        );

			print <<<EOT
						</td>
					</tr>
EOT;
		}

        print <<<EOT

                    <tr>
						<td>
                            authors*
                        </td>

                        <td>
                            <input type="text" name="contribution_authors" value="$contribution_authors" size="30" />
							<span class="example"><br/> e.g. John Smith, Mary Jane</span>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            authors organization*
                        </td>

                        <td>
                            <input type="text" name="contribution_authors_organization" value="$contribution_authors_organization" size="30"/>
                        </td>
                    </tr>
                    
                    <tr>
	                    <td>
	                        contact email*
	                    </td>
	
                        <td>
                            <input type="text" name="contribution_contact_email" value="$contribution_contact_email" size="30" />
                        </td>
                    </tr>         

                    <tr>
	                    <td>
	                        title*
	                    </td>
	
                        <td>
                            <input type="text" name="contribution_title" value="$contribution_title" size="30"/>
                        </td>
                    </tr>
                    
                    <tr>
	                    <td>
	                        keywords*
	                    </td>

                        <td>
                            <input type="text" name="contribution_keywords" value="$contribution_keywords" size="30" />
                        </td>
                    </tr>
EOT;

			if ($contributor_is_team_member) {
				print <<<EOT
					<tr>
						<td>
		                    from phet*
		                </td>
		
						<td>
EOT;

				print_checkbox(
		            "contribution_from_phet",
		            "",
		            $contribution_from_phet
		        );
	
				print <<<EOT
						</td>
		            </tr>
EOT;
			}

		print <<<EOT

					<tr>
						<td>simulation(s)*</td>
						
                    	<td>
EOT;

					        print_multiple_selection(
								'Simulation',
					            sim_get_all_sim_names(),
					            contribution_get_associated_simulation_listing_names($contribution_id)
					        );
                    
        print <<<EOT
						</td>
					</tr>
                    
                    <tr>
						<td>type(s)</td>
						
						<td>
                    
EOT;
        					print_multiple_selection('Type', $all_contribution_types, $contribution_types);
        
        print <<<EOT
						</td>
					</tr>
					
					<tr>
                    	<td>grade level(s)*</td>

						<td>
                    
EOT;

					        print_multiple_selection(
								'Level',
					            contribution_get_all_template_level_names(),
					            contribution_get_level_names_for_contribution($contribution_id)
					        );

        print <<<EOT
						</td>
					</tr>
                    
                    <tr>
						<td>existing files*</td>
						
						<td>
EOT;
    
					        print_deletable_list(
								'File',
					            contribution_get_contribution_file_names($contribution_id)
					        );
    
        print <<<EOT
						</td>
					</tr>
					
					<tr>
						<td>new files*</td>
						
						<td>
                        	<input type="file" name="contribution_file_url" class="multi" />
 						</td>
					</tr>

                    <tr>
						<td colspan="2">
                        	<input name="submit" class="button" type="submit" value="$button_name" />	
						</td>
                    </tr>
				</table>
				
                </fieldset>

                <fieldset>
                    <legend>Optional</legend>
                                        
					<table class="form">
                    <tr>
	                    <td>
	                        description
	                    </td>

                        <td>
                            <textarea name="contribution_desc" rows="5" cols="40">$contribution_desc</textarea>
                        </td>
                    </tr>                    
                    
                    <tr>
                        <td>
							subject areas
						</td>
						
						<td>

EOT;

					        print_multiple_selection(
								'Subject',
					            contribution_get_all_template_subject_names(),
					            contribution_get_subject_names_for_contribution($contribution_id)
					        );
        
        print <<<EOT
						</td>
                    </tr>
                    
                    <tr>
	                    <td>
	                        duration
	                    </td>
	
						<td>
EOT;

					        print_single_selection(
					            "contribution_duration",
					            array(
					                "0"     => "NA",
					                "30"    => "30 minutes",
					                "60"    => "60 minutes",
									"90"    => "90 minutes",
					                "120"   => "120 minutes"
					            ),
					            $contribution_duration
					        );

        print <<<EOT
						</td>
                    </tr>
                    
                    <tr>
	                    <td>
	                        answers included
	                    </td>
	
						<td>
EOT;

					        print_checkbox(
					            "contribution_answers_included",
					            "",
					            $contribution_answers_included
					        );

        print <<<EOT
						</td>
                    </tr>
                    
					</table>
					
					<p>Please describe how the contribution complies with the <a href="http://www.nap.edu/readingroom/books/nses/html/6a.html">K-12 National Science Standards</a>:</p>

EOT;

   					contribution_print_standards_compliance($contribution_standards_compliance);

        print <<<EOT
					<table class="form">
						<tr>
							<td colspan="2">
	                        	<input name="submit" class="button" type="submit" value="$button_name" />
			                    <input type="hidden" name="referrer"        value="$referrer" />
			                    <input type="hidden" name="contribution_id" value="$contribution_id" />
			                    <input type="hidden" name="action"          value="update" />
							</td>
	                    </tr>
					</table>
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
            
            $sim_url = sim_get_url_to_sim_page($sim_id);
            
            $sim_list .= '<a href="'.$sim_url.'">'.$sim_name.'</a>';
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

	function contribution_get_simulation_listings_as_list($contribution_id) {
		$names = contribution_get_associated_simulation_listing_names($contribution_id);
		
		$list = '';
		
		$is_first = true;
		
		foreach ($names as $name) {
			if ($is_first) {
				$is_first = false;
			}
			else {
				$list .= ', ';
			}
			
			$list .= $name;
		}
		
		return $list;
	}

    function contribution_print_summary3($contribution, $print_sims = true) {    
        global $referrer;
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        $sim_list = "None";
        
        if (isset($sim_name) && trim($sim_name) != '') {
            $sim_list = '';
            
            $is_first = true;
            
            foreach(explode(',', $sim_name) as $sim) {
				$sim = trim($sim);

				$cur_sim_link = sim_get_link_to_sim_page_by_name($sim);
                
                if ($is_first) {
                    $is_first = false;
                }
                else {
                    $sim_list .= '<br/>';
                }
                
                $sim_list .= $cur_sim_link;
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

		$parsed_name = parse_name($contribution_author);
		
		$contribution_author  = $parsed_name['full_name'];
        $author_first_initial = $parsed_name['first_initial'];
        $author_last_name     = $parsed_name['last_name'];

        $time = strtotime($contribution_date_updated);
        
        $contribution_date_updated = date('n/y', $time);       

		if ($author_first_initial == '') {
        	$author_abbr = "$author_last_name";
		}
		else {
			$author_abbr = "$author_first_initial. $author_last_name";
		}
		
		$author_html = "<abbr title=\"$contribution_author\">$author_abbr</abbr>";
        
        $title_html = <<<EOT
                <a href="../teacher_ideas/view-contribution.php?contribution_id=$contribution_id&amp;referrer=$referrer">$contribution_title</a>
EOT;

		if ($contribution_from_phet == 1) {
			$title_html = "${title_html} ".FROM_PHET_IMAGE_HTML;
		}
        
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
            
            $contribution_file_names[$name] = $contribution_file['contribution_file_name'];
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
        if ($contribution_id == -1) {
            // No one owns this contribution:
            return true;
        }

        $contribution = contribution_get_contribution_by_id($contribution_id);
        $contributor  = contributor_get_contributor_by_id($contributor_id);        
        
        return $contribution['contributor_id'] == $contributor_id || 
               $contributor['contributor_is_team_member'] == '1' ||
               $contribution['contributor_id'] == '-1';
    }

    function contribution_get_contributions_for_contributor_id($contributor_id) {
        return db_get_rows_by_condition('contribution', array('contributor_id' => $contributor_id));
    }

	function contribution_get_coauthored_contributions_for_contributor_id($contributor_id) {
		$contributor = contributor_get_contributor_by_id($contributor_id);
		
		if ($contributor['contributor_is_team_member'] == 0) return array();
		
        $contributions = contribution_get_all_contributions();
        
        $filtered = array();
        
        foreach($contributions as $contribution) {
            if ($contribution['contributor_id'] != $contributor_id && strpos($contribution['contribution_authors'], $contributor['contributor_name'])) {
                $filtered[] = $contribution;
            }
        }
        
        return $filtered;
    }
    
    function contribution_get_other_manageable_contributions_for_contributor_id($contributor_id) {
		$contributor = contributor_get_contributor_by_id($contributor_id);
		
		if ($contributor['contributor_is_team_member'] == 0) return array();
		
        $contributions = contribution_get_all_contributions();
        
        $filtered = array();
        
        foreach($contributions as $contribution) {
            if ($contribution['contributor_id'] != $contributor_id && !strpos($contribution['contribution_authors'], $contributor['contributor_name'])) {
                $filtered[] = $contribution;
            }
        }
        
        return $filtered;
    }
    
    function contribution_delete_contribution($contribution_id) {
        $condition = array( 'contribution_id' => $contribution_id );
        
        db_delete_row('contribution',            $condition);
        db_delete_row('contribution_file',       $condition);
        db_delete_row('simulation_contribution', $condition);
        db_delete_row('contribution_comment',    $condition);            
        db_delete_row('contribution_flagging',   $condition);            
        db_delete_row('contribution_nomination', $condition);
        
        contribution_delete_all_multiselect_associations('contribution_level',   $contribution_id);
		contribution_delete_all_multiselect_associations('contribution_type',    $contribution_id);
		contribution_delete_all_multiselect_associations('contribution_subject', $contribution_id);
        
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
                $contribution_type = "Homework";
            }
            else if (preg_match('/.+\\.(ppt|odp)/i', $file_user_name) == 1) {
                $contribution_type = "Lecture";
            }
            else {
                $contribution_type = "Support";
            }
        }
        else {
            $contribution_type = "Lab";
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
    
    function contribution_get_contribution_file_by_id($contribution_file_id) {
        return db_get_row_by_id('contribution_file', 'contribution_file_id', $contribution_file_id);
    }
    
    function contribution_get_contribution_file_name($contribution_file_id) {
        eval(get_code_to_create_variables_from_array(contribution_get_contribution_file_by_id($contribution_file_id)));
        
        return $contribution_file_name;
    }
    
    function contribution_get_contribution_file_contents($contribution_file_id) {
        eval(get_code_to_create_variables_from_array(contribution_get_contribution_file_by_id($contribution_file_id)));
        
        if ($contribution_file_contents != '') {
            return base64_decode($contribution_file_contents);
        }
        
        return file_get_contents(SITE_ROOT.$contribution_file_url);
    }
    
    function contribution_get_contribution_file_link($contribution_file_id) {
        
    }
    
    function contribution_add_new_file_to_contribution($contribution_id, $file_tmp_name, $file_user_name) {    
        $file_size = filesize($file_tmp_name);
        
        $contribution_file_id = db_insert_row(
                'contribution_file',
                array(
                    'contribution_id'            => $contribution_id,
                    'contribution_file_name'     => $file_user_name,
                    'contribution_file_contents' => base64_encode(file_get_contents($file_tmp_name)),
                    'contribution_file_size'     => $file_size
                )
            );
            
        return $contribution_file_id;
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
        
        $contribution_level_rows = db_exec_query("SELECT * FROM `contribution_level` WHERE `contribution_level_is_template`='1' ORDER BY `contribution_level_order` ASC ");
        
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
        
        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_order` ASC ");
        
        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];
            
            $name = create_multiselect_control_name('contribution_subject', $id);
            
            $subjects[$name] = format_for_html($contribution_subject);
        }
        
        return $subjects;
    }
    
    function contribution_get_all_template_subject_names() {
        $subjects = array();
        
        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_order` ASC ");
        
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
        
        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_type_is_template` = '1' ORDER BY `contribution_type_order` ASC ");
        
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

	function contribution_get_specific_contributions($sim_names, $type_descs, $level_descs) {
		$contributions = array();
		
		$sim_names   = array_remove($sim_names,   'all');
		$type_descs  = array_remove($type_descs,  'all');
		$level_descs = array_remove($level_descs, 'all');
        
        $query = "SELECT * FROM `contribution`";

		$where = '';

		$query .= " LEFT JOIN `simulation_contribution` ON `contribution`.`contribution_id`=`simulation_contribution`.`contribution_id`";
		$query .= " LEFT JOIN `simulation` ON `simulation_contribution`.`sim_id`=`simulation`.`sim_id`";

		if (count($sim_names) > 0) {
			$where .= ' WHERE';
			
			$where .= db_form_alternation_where_clause('simulation', 'sim_name', $sim_names);
		}

		$query .= " LEFT JOIN `contribution_type` ON `contribution`.`contribution_id`=`contribution_type`.`contribution_id`";
		
		if (count($type_descs) > 0) {			
			if (strlen($where) > 0) $where .= " AND";
			else $where .= ' WHERE';
			
			$where .= db_form_alternation_where_clause('contribution_type', 'contribution_type_desc', $type_descs);
		}
		
		$query .= " LEFT JOIN `contribution_level` ON `contribution`.`contribution_id`=`contribution_level`.`contribution_id`";
		
		if (count($level_descs) > 0) {	
			if (strlen($where) > 0) $where .= " AND";
			else $where .= ' WHERE';			
			
			$where .= db_form_alternation_where_clause('contribution_level', 'contribution_level_desc', $level_descs);
		}
		
		$query .= "$where ORDER BY `contribution`.`contribution_title` ASC";
		
		$contribution_rows = db_exec_query($query);
		         
         while ($contribution = mysql_fetch_assoc($contribution_rows)) {
             $contribution_id = $contribution['contribution_id'];
             
             $contributions["$contribution_id"] = format_for_html($contribution);
         }
         
         return $contributions;
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
		if (strlen(trim($username)) == 0) return false;
		
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return true;
            }
        }
        
        return false;
    }

    function contributor_send_password_reminder($username) {
        $contributor = contributor_get_contributor_by_email($username);

		if ($contributor) {
	        $contributor_name     = $contributor['contributor_name'];
	        $contributor_password = $contributor['contributor_password'];
	
			if (strlen($contributor_password) == 0) {
				$pass = "You haven't chosen a password (don't enter anything in the login dialog).\n";
			}
			else {
				$pass = "Your password is \"$contributor_password\" (without the quotation marks)\n";
			}
        
	        mail($username, 
	             "PhET Password Reminder", 
	             "\n".
	             "Dear $contributor_name, \n".
	             "\n".
				 "Your login e-mail is \"$username\".\n".
	             $pass.
	             "\n".
	             "Regards,\n".
	             "\n".
	             "The PhET Team \n",
        
	             "From: The PhET Team <phethelp@colorado.edu>");
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

    function contributor_get_id_from_contributor_email($username) {
        $contributors = contributor_get_all_contributors();
        
        foreach($contributors as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return $contributor['contributor_id'];
            }
        }
        
        return false;        
    }
    
    function contributor_get_id_from_contributor_email_and_password($username, $password) {
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
    
    function contributor_get_id_from_contributor_email_and_password_hash($username, $password_hash) {
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
        return contributor_get_id_from_contributor_email_and_password_hash($username, $password_hash) !== false;
    }
    
    function contributor_is_valid_admin_login($username, $password_hash) {
        if (!contributor_is_admin_username($username)) return false;
        
        return contributor_is_valid_login($username, $password_hash);
    }
    
    function contributor_print_quick_login() {
        print <<<EOT
            <div id="quick_login_uid">
                <div class="field">
                    <span class="label_content">                
                        <input type="text" size="20" name="contributor_name" id="contributor_name_uid" onchange="javascript:on_name_change();"/> 
                    </span>
            
                    <span class="label">your name</span>                            
                </div>

                <div id="required_login_info_uid"> 
                    <div class="field">
                        <span class="label_content">
                            <input type="button" name="enter" value="Enter" />
                        </span>
                    </div>
                </div>

                <br/>
                
            </div>
EOT;
    }
    
    function contributor_add_new_blank_contributor($is_team_member = false) {
        $contributor_is_team_member = $is_team_member ? '1' : '0';
        
        return db_insert_row(
            'contributor',
            array(
                'contributor_is_team_member' => $contributor_is_team_member,
				'contributor_receive_email'  => '1'
            )
        );
    }
    
    function contributor_add_new_contributor($username, $password, $is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';
        
        return db_insert_row('contributor', 
            array(
                'contributor_email'             => $username,
                'contributor_password'          => $password,
                'contributor_is_team_member'    => $team_member_field,
				'contributor_receive_email'     => '1'
            )
        );
    }
    
    function contributor_get_contributor_by_id($contributor_id) {
        return db_get_row_by_id('contributor', 'contributor_id', $contributor_id);
    }
    
    function contributor_get_contributor_by_name($contributor_name) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_name`='$contributor_name' ");
        
        if (!$result) {
            return false;
        }
        
        return format_for_html(mysql_fetch_assoc($result));
    }
    
    function contributor_get_contributor_by_email($contributor_email) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_email` LIKE '%$contributor_email%' ");
        
        if (!$result) {
            return false;
        }
        
        return format_for_html(mysql_fetch_assoc($result));
    }

	function contributor_print_desc_list($contributor_desc, $wide) {
		if ($wide) {
			$wide_style = ' auto-width';
		}
		else {
			$wide_style = '';
		}
		
		print_single_selection(
			'contributor_desc', 
			array(
				DEFAULT_CONTRIBUTOR_DESC,
				'I am a teacher interested in using PhET in the future',
				'I am a student who uses PhET',
				'I am a student interested in using PhET in the future',
				'I am just interested in physics',
				'Other'
			)
			, 
			$contributor_desc,
			"class=\"always-enabled$wide_style\""
		);
	}
    
    function contributor_print_full_edit_form($contributor_id, $script, $optional_message = null, 
                                              $standard_message = "<p>You may edit your profile information below.</p>") {

                                                  
        $contributor = contributor_get_contributor_by_id($contributor_id);
        
        eval(get_code_to_create_variables_from_array($contributor));

		if ($contributor_desc == '') {
			$contributor_desc = DEFAULT_CONTRIBUTOR_DESC;
		}
        
        $contributor_receive_email_checked = $contributor_receive_email == '1' ? 'checked="checked"' : '';     
        
        print <<<EOT
            <form method="post" action="$script">
                <fieldset>
                    <legend>Profile for $contributor_name</legend>
EOT;

        if ($optional_message !== null) {
            print "$optional_message";
        }

        print <<<EOT
                    $standard_message
                    
					<table class="form">
                    	<tr>
                        	<td>password*</td>
                            
							<td>
                                <input type="password" name="contributor_password" value="$contributor_password" size="25"/>                        
                            </td>
                        </tr>
                    
                        <tr>
                            <td>name*</td>

                            <td>
                                <input type="text" name="contributor_name" value="$contributor_name" size="25"/>
                            </td>
                        </tr>
                    
                        <tr>
                            <td>organization*</td>

                            <td>
                                <input type="text" name="contributor_organization" value="$contributor_organization" size="25"/>
                            </td>
                        </tr>

						<tr>
							<td>description*</td>
							
							<td>
EOT;
								contributor_print_desc_list($contributor_desc, true);
						
						print <<<EOT
							</td>
						</tr>
                    
                        <tr>
                            <td>title</td>

                            <td>
                                <input type="text" name="contributor_title" value="$contributor_title" size="25" />
                            </td>
                        </tr>
                    
                        <tr>
                        	<td>address</td>

                            <td>
                                <input type="text" name="contributor_address" value="$contributor_address" size="25" />
                            </td>
                        </tr>
                    
                        <tr>
                            <td>office</td>

                            <td>
                                <input type="text" name="contributor_office" value="$contributor_office" size="15" />
                            </td>
                        </tr>
                    
                        <tr>
                            <td>city</td>

                            <td>
                                <input type="text" name="contributor_city"
                                    value="$contributor_city" id="city" size="15" />
                            </td>
                        </tr>
                    
                        <tr>
                            <td>state/province</td>

                            <td>
                                <input type="text" name="contributor_state"
                                    value="$contributor_state" id="state" size="15" />
                            </td>
                        </tr>

                        <tr>
                            <td>country</td>

                            <td>
                                <input type="text" name="contributor_country"
                                    value="$contributor_country" id="state" size="15" />
                            </td>
                        </tr>
                    
                        <tr>
                            <td>postal code</td>

                            <td>
                                <input type="text" name="contributor_postal_code"
                                    value="$contributor_postal_code" id="postal_code" size="10" />
                            </td>
                        </tr>
                    
                        <tr>
                            <td>primary phone</td>

                            <td>
                                <input type="text" name="contributor_primary_phone"
                                    value="$contributor_primary_phone" id="primary_phone" size="12" />
                            </td>
                        </tr>
                    
                        <tr>
                            <td>secondary phone</td>

                            <td>
                                <input type="text" name="contributor_secondary_phone"
                                    value="$contributor_secondary_phone" id="secondary_phone" size="12" />
                            </td>
                        </tr>
                    
                        <tr>
                            <td>fax</td>

                            <td>
                                <input type="text" name="contributor_fax"
                                    value="$contributor_fax" id="fax" size="12" />
                            </td>                        
                        </tr>
                    
                        <tr>
                            <td>receive phet email</td>                            

                            <td>
								<input type="hidden"   name="contributor_receive_email" value="0" />
                                <input type="checkbox" name="contributor_receive_email" value="1" $contributor_receive_email_checked />
                            </td>
                        </tr>
                    </table>

					<p class="footnote">The PhET newsletter is sent 4 times per year to announce major changes to the simulations. You may unsubscribe at any time.</p>

                   <input class="button" name="Submit" type="submit" id="submit" value="Done" />

					<input type="hidden" name="contributor_id" value="$contributor_id" />
                 </fieldset>
            </form>
EOT;
    }
    
    function contributor_delete_contributor($contributor_id) {
        return db_delete_row('contributor', 
            array(
                'contributor_id' => $contributor_id
            )
        );
    }
    
    function contributor_update_contributor($contributor_id, $update_array) {
        return db_update_table('contributor', $update_array, 'contributor_id', $contributor_id);
    }
    
    function contributor_update_contributor_from_script_parameters($contributor_id) {
        $params = gather_script_params_into_array('contributor_');
        
        contributor_update_contributor($contributor_id, $params);
    }
    
    function contributor_gather_fields_into_globals($contributor_id) {
        $contributor = contributor_get_contributor_by_id($contributor_id);
        
        foreach($contributor as $key => $value) {
            $GLOBALS["$key"] = "$value";
        }
    }

?>