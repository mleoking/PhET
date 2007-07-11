<?php

    include_once('db-utils.php');
    include_once('web-utils.php');
    include_once('login-info.php');

    $old = mysql_connect(OLD_DB_HOSTNAME, OLD_DB_USERNAME, OLD_DB_PASSWORD);
    $new = mysql_connect(NEW_DB_HOSTNAME, NEW_DB_USERNAME, NEW_DB_PASSWORD);
    
    $connection = null;
    
    function switch_to_old() {
        global $old, $connection;
        
        mysql_select_db(OLD_DB_NAME, $old);
        
        $connection = $old;
    }
    
    function switch_to_new() {
        global $new, $connection;
        
        mysql_select_db(NEW_DB_NAME, $new);
        
        $connection = $new;
    }
    
    function switch_to($c) {
        if ($c == $GLOBALS['old']) {
            mysql_select_db(OLD_DB_NAME, $GLOBALS['old']);
        }
        else if ($c == $GLOBALS['new']) {
            mysql_select_db(NEW_DB_NAME, $GLOBALS['new']);
        }
        else {
            die();
        }
    }
    
    function is_connected_to_old() {
        return $GLOBALS['connection'] == $GLOBALS['old'];
    }
    
    function is_connected_to_new() {
        return $GLOBALS['connection'] == $GLOBALS['new'];
    }
    
    function get_connected_to() {
        return $GLOBALS['connection'];
    }
    
    function create_contributor_if_necessary($contributor) {
        switch_to_new();
        
        $email = $contributor['contributor_email'];
        
        if (!$email || $email == '') return -1;
        
        $row = db_get_row_by_condition('contributor', array('contributor_email' => $email) );
        
        if (!$row) {
            if (!isset($contributor['contributor_password'])) {
                $contributor['contributor_password'] = web_create_random_password();
            }
            
            db_insert_row('contributor', $contributor);
            
            switch_to_old();
            
            return create_contributor_if_necessary($contributor);
        }
        
        switch_to_old();
        
        return $row['contributor_id'];
    }
    
    function get_shorthand($string) {
        // Strip out common words and non-characters:
        $string = preg_replace('/(\(.+\))/i', '', $string);
        $string = preg_replace('/\b(the)|(a)|(\W)|(\d)|(\s)\b/i', '', $string);
        
        return $string;
    }
    
    function compare_sim_names($name1, $name2) {
        $name1 = strtolower($name1);
        $name2 = strtolower($name2);
        
        if ($name1 == $name2) return 0;
        
        if (strpos($name1, $name2) || strpos($name2, $name1)) return 1;
        
        $name1 = get_shorthand($name1);
        $name2 = get_shorthand($name2);
        
        // Compare again:
        if (strpos($name1, $name2) || strpos($name2, $name1)) return 2;
        
        // Still failed. Count number of different characters:
        $name1_to_count = count_chars($name1);
        $name2_to_count = count_chars($name2);
        
        $diff_count = 0;
        
        foreach($name1_to_count as $char => $count) {
            if (!isset($name2_to_count[$char])) {
                // The second name doesn't have this character:
                $diff_count += $count;
            }
            else {
                // The second name DOES have this character:
                $that_count = $name2_to_count[$char];
                
                $diff_count += abs($count - $that_count);
            }
        }
        
        foreach($name2_to_count as $char => $count) {
            if (!isset($name1_to_count[$char])) {
                // The first name doesn't have this character:
                $diff_count += $count;
            }
            else {
                // The first name DOES have this character -- already counted.
            }
        }
        
        return 3 + $diff_count;
    }
    
    function get_sim_id_by_sim_name($name1) {
        $name1 = strtolower($name1);
        
        switch_to_new();
        
        $sims = db_get_all_rows('simulation');
        
        $best_match_sim = $sims[0];
        $best_score     = 9999999;
        
        foreach($sims as $sim) {
            $name2 = strtolower($sim['sim_name']);
            
            $score = compare_sim_names($name1, $name2);
            
            print "Compared sim names: $name1, $name2  -- $score <br/>";
            
            if ($score < $best_score) {
                $best_score     = $score;
                $best_match_sim = $sim;
            }
        }
        
        switch_to_old();
        
        return $best_match_sim['sim_id'];
    }
    
    function convert_old_simulation_listings($old_contrib, $new_contribution_id) {
        $old_sim_listings = db_get_rows_by_condition(
            'contribution_simulation', 
            array('contribution' => $old_contrib['id'])
        );
        
        if ($old_sim_listings) {
            foreach($old_sim_listings as $old_sim_listing) {
                $old_sim_id = $old_sim_listing['simulation'];
                
                $old_sim = db_get_row_by_id('LU_simulation', 'id', $old_sim_id);
                
                $new_sim_id = get_sim_id_by_sim_name($old_sim['description']);
                
                if ($new_sim_id) {
                    switch_to_new();
                    
                    db_insert_row('simulation_contribution',
                        array(
                            'sim_id'          => $new_sim_id,
                            'contribution_id' => $new_contribution_id
                        )
                    );
                    
                    switch_to_old();
                }
            }
        }
    }
    
    function convert_old_standards_compliance($old_contrib) {
        $id = $old_contrib['id'];
        
        $str = '';
        
        $rows = db_get_rows_by_condition('contribution_natl_stds', array( 'contribution' => $id ) );
        
        if ($rows) {
            foreach($rows as $std) {
                $sid = $std['natl_std'];
            
                if ($sid == 1) {
                    $str .= '[checkbox_standards_1]';
                }
                else if ($sid == 8) {
                    $str .= '[checkbox_standards_2]';
                }
                else if ($sid == 15) {
                    $str .= '[checkbox_standards_3]';
                }
                else if ($sid == 2) {
                    $str .= '[checkbox_standards_4]';
                }
                else if ($sid == 9) {
                    $str .= '[checkbox_standards_5]';
                }
                else if ($sid == 16) {
                    $str .= '[checkbox_standards_6]';
                }
                else if ($sid == 3) {
                    $str .= '[checkbox_standards_7]';
                }
                else if ($sid == 10) {
                    $str .= '[checkbox_standards_8]';
                }
                else if ($sid == 17) {
                    $str .= '[checkbox_standards_9]';
                }
                else if ($sid == 4) {
                    $str .= '[checkbox_standards_10]';
                }
                else if ($sid == 11) {
                    $str .= '[checkbox_standards_11]';
                }
                else if ($sid == 18) {
                    $str .= '[checkbox_standards_12]';
                }
                else if ($sid == 5) {
                    $str .= '[checkbox_standards_13]';
                }
                else if ($sid == 12) {
                    $str .= '[checkbox_standards_14]';
                }
                else if ($sid == 19) {
                    $str .= '[checkbox_standards_15]';
                }
                else if ($sid == 6) {
                    $str .= '[checkbox_standards_16]';
                }
                else if ($sid == 13) {
                    $str .= '[checkbox_standards_17]';
                }
                else if ($sid == 20) {
                    $str .= '[checkbox_standards_18]';
                }
                else if ($sid == 7) {
                    $str .= '[checkbox_standards_19]';
                }
                else if ($sid == 14) {
                    $str .= '[checkbox_standards_20]';
                }
                else if ($sid == 21) {
                    $str .= '[checkbox_standards_21]';
                }
            }
        }
        
        return $str;
    }
    
    function convert_old_contribution_type($old_contrib, $new_contribution_id) {
        $id = $old_contrib['id'];
        
        $rows = db_get_rows_by_condition('contribution_activity', array('contribution' => $id));
        
        foreach($rows as $old_activity_template) {
            $old_activity = db_get_row_by_id('LU_activity', 'id', $old_activity_template['activity']);
            
            $type = $old_activity['type'];
            
            switch_to_new();
            
            db_insert_row('contribution_type',
                array(
                    'contribution_type_desc'        => $type,
                    'contribution_type_desc_abbrev' => abbreviate($type),
                    'contribution_id'               => $new_contribution_id 
                )
            );
            
            switch_to_old();
        }
    }
    
    function convert_old_contribution_file($new_contribution_id, $old_name, $old_data, $old_size) {    
        switch_to_new();
        
        $id = db_insert_row(
            'contribution_file', 
            array(
                'contribution_file_url'      => '',
                'contribution_file_size'     => $old_size,
                'contribution_file_name'     => $old_name,
                'contribution_file_contents' => base64_encode($old_data),
                'contribution_id'            => $new_contribution_id
            )
        );
        
        switch_to_old();
        
        return $id;
    }
    
    function convert_old_contribution_files($old_contrib, $new_contribution_id) {
        $old_contrib_id = $old_contrib['id'];
        
        $result = db_exec_query("SELECT * FROM `asset` WHERE `contribution`='$old_contrib_id' ");
        
        if ($result) {
            $file_num = 1;
            
            while($old_asset = mysql_fetch_assoc($result)) {
                print "Converting contribution file $file_num...<br/>";
                
                flush();
                
                $old_name = $old_asset['filename'];
                $old_data = base64_decode($old_asset['file_data']);
                $old_size = $old_asset['filesize'];
            
                convert_old_contribution_file($new_contribution_id, $old_name, $old_data, $old_size);
                
                $file_num++;
            }
        }
    }
    
    function convert_old_contribution_level($old_contrib, $new_contribution_id) {
        $old_contrib_id = $old_contrib['id'];
        
        $courses = db_get_rows_by_condition('course', array( 'contribution' => $old_contrib_id ));
        
        foreach($courses as $course) {
            $level_id = $course['course_level'];
            
            $level = db_get_row_by_id('LU_course_level', 'id', $level_id);
            
            $desc = $level['level'];
            
            switch_to_new();
            
            db_insert_row('contribution_level',
                array(
                    'contribution_level_desc'        => $desc,
                    'contribution_level_desc_abbrev' => abbreviate($desc),
                    'contribution_id'                => $new_contribution_id 
                )
            );
            
            switch_to_old();
        }
    }
    
    function convert_old_contribution_topic($old_contrib, $new_contribution_id) {
        $old_contrib_id = $old_contrib['id'];
        
        $topic_templates = db_get_rows_by_condition('contribution_topic', array( 'contribution' => $old_contrib_id ));
        
        foreach($topic_templates as $topic_template) {
            $topic_id = $topic_template['topic']; // $topic_template['area'] // -- ????????????????????
            
            $topic = db_get_row_by_id('LU_topic', 'id', $topic_id);
            
            $desc = $topic['topic'];
            
            switch_to_new();
            
            db_insert_row('contribution_subject',
                array(
                    'contribution_subject_desc'        => $desc,
                    'contribution_subject_desc_abbrev' => abbreviate($desc),
                    'contribution_id'                  => $new_contribution_id 
                )
            );
            
            switch_to_old();
        }
    }
    
    switch_to_old();
    
    $all_old_contribs = db_get_all_rows('contribution');
    
    $cur_sim_num = 1;
    
    foreach($all_old_contribs as $old_contrib) {
        print "Converting simulation contribution $cur_sim_num of ".count($all_old_contribs)."...<br/>";
        
        flush();
        
        $cur_sim_num++;
        
        $old_contrib_id = $old_contrib['id'];
        
        $new_contrib = array();
        
        $new_contrib['contribution_title']    = $old_contrib['title'];
        $new_contrib['contribution_desc']     = $old_contrib['summary'];
        $new_contrib['contribution_approved'] = '1';
        
        $ai = $old_contrib['Answers_Included'];
        
        if (strtolower($ai == 'no') || $ai == 0) {
            $new_contrib['contribution_answers_included'] = '0';
        }
        else {
            $new_contrib['contribution_answers_included'] = '1';
        }
        
        $new_contrib['contribution_date_created'] = $old_contrib['timestamp'];
        $new_contrib['contribution_date_updated'] = $old_contrib['updated'];
        
        $is_phet_activity = $old_contrib['phet_activity']             == 1 || 
                            strtolower($old_contrib['phet_activity']) == 'yes';
        
        $old_author = db_get_row_by_id('author', 'id', $old_contrib['author']);
        
        /*
        
        Array ( [id] => 328 [last_name] => De Goes [first_name] => John [email_addr] => degoes@colorado.edu [school_affiliation] => University of Colorado [pedagogical_use] => 1 [emailshare] => 1 [admin] => 1 )
        
        */
        $first_name = $old_author['first_name'];
        $last_name  = $old_author['last_name'];
        $email      = $old_author['email_addr'];
        $org        = $old_author['school_affiliation'];
        $is_team    = $old_author['admin'];
        
        $contributor = array();
        
        $contributor['contributor_name']            = "$first_name $last_name";
        $contributor['contributor_email']           = $email;
        $contributor['contributor_organization']    = $org;
        $contributor['contributor_is_team_member']  = $is_team;
        
        $old_author_password = db_get_row_by_id('author_pw', 'id', $old_contrib['author']);
        
        if ($old_author_password) {
            $contributor['contributor_password'] = $old_author_password['password'];
        }
        
        $contributor_id = create_contributor_if_necessary($contributor);
        
        $new_contrib['contribution_authors']        = $contributor['contributor_name'];
        $new_contrib['contribution_contact_email']  = $contributor['contributor_email'];        
        $new_contrib['contributor_id']              = $contributor_id;
        $new_contrib['contribution_authors_organization'] = $org;        
        
        if (strlen($old_contrib['coauthors']) > 0) {
            $new_contrib['contribution_authors'] .= ', '.$old_contrib['coauthors'];
        }
        
        $contribution_time_id = db_get_row_by_id('contribution_time', 'contribution', $old_contrib_id);
        
        if ($contribution_time_id) {
            $contribution_time = db_get_row_by_id('LU_time', 'id', $contribution_time_id);
            
            if ($contribution_time) {
                $time_in_secs = strtotime($contribution_time['time'], 0);
            
                $time = $time_in_secs / 60;
            
                $new_contrib['contribution_duration'] = $time;
            }
        }
        
        $contribution_standards_compliance = convert_old_standards_compliance($old_contrib);
        
        $new_contrib['contribution_standards_compliance'] = $contribution_standards_compliance;
        
        switch_to_new();
        
        $new_contribution_id = db_insert_row(
            'contribution',
            $new_contrib
        );
        
        switch_to_old();
        
        convert_old_simulation_listings($old_contrib, $new_contribution_id);
        convert_old_contribution_type  ($old_contrib, $new_contribution_id);
        convert_old_contribution_files ($old_contrib, $new_contribution_id);
        convert_old_contribution_level ($old_contrib, $new_contribution_id);
        convert_old_contribution_topic ($old_contrib, $new_contribution_id);
    }
    
    print "Conversion complete!<br/>";
?>