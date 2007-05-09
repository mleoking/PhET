<?php

    include_once("db.inc");
    include_once("db-utils.php");
    include_once("web-utils.php");
    include_once("sys-utils.php");
    
    function contribution_add_new_contribution($contribution_title, $contributor_id, $file_tmp_name, $file_user_name) {
        $this_dir = dirname(__FILE__);

        $new_file_dir_rel = "./uploads/contributions";        
        $new_file_dir_abs = "$this_dir/uploads/contributions";
        
        mkdirs($new_file_dir);
        
        if (preg_match('/.+\\.(doc|txt|rtf|pdf|odt)/i', $file_user_name) == 1) {
            $contribution_type = "Activity";
        }
        else if (preg_match('/.+\\.(ppt|odp)/i', $file_user_name) == 1) {
            $contribution_type = "Lecture";
        }
        else {
            $contribution_type = "Support";
        }
        
        $new_name          = basename($file_tmp_name)."_$file_user_name";
        $new_file_path_rel = "$new_file_dir_rel/$new_name";
        $new_file_path_abs = "$new_file_dir_abs/$new_name";
        
        if (move_uploaded_file($file_tmp_name, $new_file_path_abs)) {
            $contribution_id = insert_row_into_table(
                'contribution',
                array(
                    'contribution_title' => $contribution_title,
                    'contribution_type'  => $contribution_type,
                    'contributor_id'     => $contributor_id
                )
            );
            
            $contribution_file_id = insert_row_into_table(
                'contribution_file',
                array(
                    'contribution_id'       => $contribution_id,
                    'contribution_file_url' => $new_file_path_rel
                )
            );
            
            return $contribution_id;
        }
        
        return FALSE;
    }
    
    function contribution_associate_contribution_with_simulation($contribution_id, $sim_id) {
        $simulation_contribution_id = insert_row_into_table(
            'simulation_contribution',
            array(
                'sim_id'          => $sim_id,
                'contribution_id' => $contribution_id
            )
        );
        
        return $simulation_contribution_id;
    }
    
    function contribution_set_approved($contribution_id, $status) {
        if ($status) {
            $status = '1';
        }
        else {
            $status = '0';
        }
        
        return update_table('contribution', array( 'contribution_approved' => $status ), 'contribution_id', $contribution_id);
    }
    
    function contribution_get_all_contributions($sim_id) {
        $contributions = array();
        
        $contribution_rows = run_sql_statement("SELECT * FROM `contribution` , `simulation_contribution` WHERE `contribution` . `contribution_id` = `simulation_contribution` . `contribution_id` AND `simulation_contribution` . `sim_id` = '$sim_id' ORDER BY `contribution_title` ASC");
        
        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contributions[] = $contribution;
        }
        
        return $contributions;
    }
    
    function contribution_get_approved_contributions($sim_id) {
        $contributions = contribution_get_all_contributions($sim_id);
        
        foreach($contributions as $index => $contribution) {
            if ($contribution['contribution_approved'] == '0') {
                unset($contributions[$index]);
            }
        }
        
        return $contributions;
    }
    
    function contributor_get_all_contributors() {
        $contributors = array();
        
        $contributor_rows = 
        run_sql_statement("SELECT * from `contributor` ORDER BY `contributor_name` ASC ");
        
        while ($contributor = mysql_fetch_assoc($contributor_rows)) {
            $contributors[] = $contributor;
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
        
        $contributor_rows = run_sql_statement("SELECT * from `contributor` WHERE `contributor_is_team_member`='1' ORDER BY `contributor_name` ASC ");
        
        while ($contributor = mysql_fetch_assoc($contributor_rows)) {
            $admins[] = $contributor;
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
    
    function contributor_add_new_blank_contributor($is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';
        
        run_sql_statement("INSERT INTO `contributor` (`contributor_is_team_member`) VALUES ('$team_member_field') ");
        
        return mysql_insert_id();
    }
    
    function contributor_add_new_contributor($username, $password, $is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';
        
        run_sql_statement("INSERT INTO `contributor` (`contributor_email`, `contributor_password`, `contributor_is_team_member`) VALUES ('$username', '$password', '$team_member_field') ");
        
        return mysql_insert_id();
    }
    
    function contributor_get_contributor_from_id($contributor_id) {
        $result = run_sql_statement("SELECT * FROM `contributor` WHERE `contributor_id`='$contributor_id' ");
        
        return mysql_fetch_assoc($result);
    }
    
    function contributor_print_full_edit_form($contributor_id, $script, $optional_message = null, 
                                              $standard_message = "<p>You may edit your profile information below.</p>") {
                                                  
        $contributor = contributor_get_contributor_from_id($contributor_id);
        
        gather_array_into_globals($contributor);
        
        global $contributor_name,        $contributor_title, $contributor_address,
               $contributor_office,      $contributor_city,  $contributor_state, 
               $contributor_postal_code, $contributor_primary_phone,
               $contributor_secondary_phone, $contributor_fax,
               $contributor_password,    $contributor_receive_email;
               
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
                    
                    <label for="contributor_password">
                        <input type="password" name="contributor_password" 
                            value="$contributor_password" tabindex="1" id="password" size="25"/>
                        
                        password:
                    </label>
                    
                    <label for="contributor_name">
                        name:
                        
                        <input type="text" name="contributor_name" 
                            value="$contributor_name" tabindex="2" id="name" size="25"/>
                    </label>
                    
                    <label for="contributor_title">
                        title:
                        
                        <input type="text" name="contributor_title"
                            value="$contributor_title" tabindex="3" id="title" size="25" />
                    </label>
                    
                    <label for="contributor_address">
                        address:
                        
                        <input type="text" name="contributor_address"
                            value="$contributor_address" tabindex="4" id="address" size="25" />
                    </label>
                    
                    <label for="contributor_office">
                        office:
                        
                        <input type="text" name="contributor_office"
                            value="$contributor_office" tabindex="5" id="office" size="15" />
                    </label>
                    
                    <label for="contributor_city">
                        city:
                        
                        <input type="text" name="contributor_city"
                            value="$contributor_city" tabindex="6" id="city" size="15" />
                    </label>
                    
                    <label for="contributor_state">
                        state or province:
                    
                        <input type="text" name="contributor_state"
                            value="$contributor_state" tabindex="7" id="state" size="15" />
                    </label>
                    
                    <label for="contributor_postal_code">
                        postal code:
                        
                        <input type="text" name="contributor_postal_code"
                            value="$contributor_postal_code" tabindex="8" id="postal_code" size="15" />
                    </label>
                    
                    
                    <label for="contributor_primary_phone">
                        primary phone:
                        
                        <input type="text" name="contributor_primary_phone"
                            value="$contributor_primary_phone" tabindex="9" id="primary_phone" size="12" />
                    </label>
                    
                    <label for="contributor_secondary_phone">
                        secondary phone:
                    
                        <input type="text" name="contributor_secondary_phone"
                            value="$contributor_secondary_phone" tabindex="10" id="secondary_phone" size="12" />

                    </label>
                    
                    <label for="contributor_fax">
                        <input type="text" name="contributor_fax"
                            value="$contributor_fax" tabindex="10" id="fax" size="11" />

                        fax:
                    </label>
                    
                    <input type="hidden" name="contributor_receive_email" value="0" />
                    
                    <label for="contributor_receive_email">
                        <input type="checkbox" name="contributor_receive_email"
                            value="1" tabindex="12" $contributor_receive_email_checked>
                            
                        receive email from phet:
                    </label>

                    <label for="submit">
                        <input name="Submit" type="submit" id="submit" tabindex="13" value="Done" />
                    </label>
                 </fieldset>
            </form>
EOT;
    }
    
    function contributor_delete_contributor($contributor_id) {
        run_sql_statement("DELETE FROM `contributor` WHERE `contributor_id`='$contributor_id' ");
        
        return true;
    }
    
    function contributor_update_contributor($contributor_id, $update_array) {
        return update_table('contributor', $update_array, 'contributor_id', $contributor_id);
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
        $contributor = contributor_get_contributor_from_id($contributor_id);
        
        foreach($contributor as $key => $value) {
            $GLOBALS["$key"] = "$value";
        }
    }

?>