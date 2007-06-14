<?php

    include_once("password-protection.php");
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("db-utils.php");    
    include_once("site-utils.php");
    include_once("sim-utils.php");    
    
    $simulation = array();

    // Update every field that was passed in as a _REQUEST parameter and 
    // which starts with 'sim_':
    foreach($_REQUEST as $key => $value) {
        if (preg_match('/sim_.*/', $key) == 1) {
            if (preg_match('/^sim_.+_url$/', $key) == 1) {
                // Maybe the user uploaded something instead of specifying a url:
                $value = process_url_upload_control($key, $value);
            }
            else {
                // Get rid of escape characters:
                $value = str_replace('\\', '', $value);
            }
            
            $simulation[$key] = $value;
        }
    }
    
    eval(get_code_to_create_variables_from_array($simulation));
    
    // The sorting name should not be prefixed by such words as 'the', 'an', 'a':
    $simulation['sim_sorting_name'] = get_sorting_name($sim_name);
    
    sim_update_sim($simulation);
    
    // Update size of simulation:    
    sim_auto_calc_sim_size($simulation['sim_id'])
    
    // Now we have to update the categories manually:
    $category_rows = mysql_query("SELECT * FROM `category` ");
    
    while ($category_row = mysql_fetch_assoc($category_rows)) {
        $cat_id   = $category_row['cat_id'];
        $cat_name = $category_row['cat_name'];
        
        $key_to_check_for = "checkbox_cat_id_$cat_id";

        $should_be_in_category = isset($_REQUEST["$key_to_check_for"]);
               
        db_exec_query("DELETE FROM `simulation_listing` WHERE `cat_id`='$cat_id' AND `sim_id`='$sim_id' ");
                
        if ($should_be_in_category) {
            db_exec_query("INSERT INTO `simulation_listing` (`sim_id`, `cat_id`) VALUES('$sim_id', '$cat_id')");
        }
    }  
    
    // Cleanup junk, if any:
    mysql_query('DELETE FROM `simulation` WHERE `sim_name`=\'New Simulation\' ');
    
    function print_success_message() {
        global $sim_name;
        
        print <<<EOT
            <h1>Update Successful</h1>
            
            <p>The simulation "$sim_name" was successfully updated.</p>
EOT;
    }
    
    print_site_page('print_success_message', 9);
    
    force_redirect("edit-sim.php?sim_id=$sim_id", 3);
?>