<?php

    include_once("db.inc");
    include_once("db-utils.php");
    
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
    
    function contributor_print_full_edit_form($contributor_id) {
        $contributor = contributor_get_contributor_from_id($contributor_id);
        
        print_hidden_input('contributor_id',            $contributor['contributor_id']);
        print "<p>E-mail";
        print_text_input(contributor_email,             $contributor['contributor_email']);
        print "Password";
        print_password_input(contributor_password,      $contributor['contributor_password']);
        print "</p>";
        print "<p>Name";
        print_text_input(contributor_name,              $contributor['contributor_name']);
        print "</p>";
        print "<p>Address";
        print_text_input(contributor_address,           $contributor['contributor_address'], 50);
        print "Office";
        print_text_input(contributor_office,            $contributor['contributor_office']);
        print "</p>";
        print "<p>City";
        print_text_input(contributor_city,              $contributor['contributor_city']);
        print "State";
        print_text_input(contributor_state,             $contributor['contributor_state'], 2);
        print "Postal Code";
        print_text_input(contributor_postal_code,       $contributor['contributor_postal_code'], 10);
        print "</p>";
        print "<p>Primary Phone";
        print_text_input(contributor_primary_phone,     $contributor['contributor_primary_phone'], 12);
        print "Secondary Phone";
        print_text_input(contributor_secondary_phone,   $contributor['contributor_secondary_phone'], 12);
        print "Fax";
        print_text_input(contributor_fax,               $contributor['contributor_fax'], 12);        
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