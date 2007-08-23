<?php
    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/password-protection.php");
	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");		
	
	function print_category_checkbox($cat_id, $cat_name, $cat_is_visible) {
        $sim_id = $_REQUEST['sim_id'];

        $sql_cat        = "SELECT * FROM `simulation_listing` WHERE `sim_id`= '$sim_id' AND `cat_id`='$cat_id' ";
        $sql_result_cat = mysql_query($sql_cat);
        $row_cat        = mysql_num_rows($sql_result_cat);

        $is_checked = ($row_cat >= 1 ? "checked" : "");
    
        if ($cat_is_visible == '1') {
            print "<input type=\"checkbox\" name=\"checkbox_cat_id_$cat_id\" value=\"true\" $is_checked>$cat_name<br/>";          
        }
        else {
            print "<input type=\"checkbox\" name=\"checkbox_cat_id_$cat_id\" value=\"true\" $is_checked><strong>$cat_name</strong><br/>";          
        }
    }

    function print_category_checkboxes() {
        global $connection;

        $select_categories_st = "SELECT * FROM `category` ORDER BY `cat_order` ASC ";
        $category_rows        = mysql_query($select_categories_st, $connection);

        while ($category_row=mysql_fetch_assoc($category_rows)) {
            $cat_id         = $category_row['cat_id'];
            $cat_name       = $category_row['cat_name'];
            $cat_is_visible = $category_row['cat_is_visible'];

            print_category_checkbox($cat_id, $cat_name, $cat_is_visible);
        }
    }
    
    function print_rating_checkbox($rating, $selected) {
        global $SIM_RATING_TO_IMAGE_HTML;
        
        $image_html   = $SIM_RATING_TO_IMAGE_HTML[$rating];
        $check_status = generate_check_status($rating, $selected);
        
        print "<input name=\"sim_rating\" type=\"radio\" value=\"$rating\" $check_status /> $image_html";
    }

	function print_type_checkbox($type, $selected) {
		global $SIM_TYPE_TO_IMAGE_HTML;
		
		$image_html   = $SIM_TYPE_TO_IMAGE_HTML[$type];
        $check_status = generate_check_status($type, $selected);
        
        print "<input name=\"sim_type\" type=\"radio\" value=\"$type\" $check_status /> $image_html";
	}
	
	function print_site_content() {
	    $simulation = sim_get_sim_by_id($_REQUEST['sim_id']);
	    
	    if (!$simulation) {
	        print "<h1>No Simulation Found</h1><p>There is no simulation with the specified id.</p>";
	        return;
	    }
	    
	    eval(get_code_to_create_variables_from_array($simulation));
    
        print <<<EOT
            <h1>Edit Simulation Parameters</h1>
    
            <form enctype="multipart/form-data" action="update-sim.php" method="post">
    
                <input type="hidden" name="sim_id" value="$sim_id" />
EOT;
    
        print_captioned_editable_area("Specify the name of the simulation", "sim_name", $sim_name, "1");
        
        print_captioned_editable_area("Specify the <em>dir-name</em> of the simulation", "sim_dirname", $sim_dirname, "1");
            
        print_captioned_editable_area("Specify the <em>flavor-name</em> of the simulation", "sim_flavorname", $sim_flavorname,      "1");
        
        print_captioned_url_upload_control("Specify the URL of the animated GIF preview",
                                           "sim_animated_image_url", $sim_animated_image_url, "2");

        print <<<EOT
    <div>Please select a rating for this simulation</div>
                <p>
EOT;

        print_rating_checkbox(SIM_RATING_ALPHA,         $sim_rating);
        print_rating_checkbox(SIM_RATING_BETA,          $sim_rating);
        print_rating_checkbox(SIM_RATING_CHECK,         $sim_rating);

print <<<EOT
                </p>    

	<div>Please select the type of the simulation</div>
	
		<p>
EOT;

		print_type_checkbox(SIM_TYPE_JAVA,  $sim_type);
		print_type_checkbox(SIM_TYPE_FLASH, $sim_type);

		print "</p>";
        
        print "<div>";
        
        print_checkbox(
            "sim_crutch", 
            "<img src=\"".SIM_CRUTCH_IMAGE."\" alt=\"\" />".
            "Check here if the simulation is <strong>not</strong> standalone", 
            $sim_crutch
        );
        
        print "</div>";
  
        print_captioned_editable_area("Simulation Description", "sim_desc", $sim_desc, "10");
        print_captioned_editable_area("Enter the keywords to associated with the simulation*", 
                                      "sim_keywords", $sim_keywords, "2");
        print_captioned_editable_area("Enter the members of the design team*",               "sim_design_team",     $sim_design_team, "4");
        print_captioned_editable_area("Enter the libraries used by the simulation*",         "sim_libraries",       $sim_libraries,   "4");  
        print_captioned_editable_area("Enter the 'thanks to' for the simulation*",           "sim_thanks_to",       $sim_thanks_to,   "4");  

        print_captioned_url_upload_control("Enter the URL for the teacher's guide PDF", "sim_teachers_guide_url", $sim_teachers_guide_url, "2");    
    
        print_captioned_editable_area("Enter the main topics*",                              "sim_main_topics",     $sim_main_topics, "4");  

        print_captioned_editable_area("Enter the sample learning goals*",                    "sim_sample_goals",    $sim_sample_goals,"6");
    
        print("<div>Please select the categories you would like the Simulation to appear under:</div>");

        print_category_checkboxes();
    
        print <<<EOT
            </p>
            
            <p>
                <input type="submit" value="Update" name="submit">
            </p>
            
            </form>
    
            <div>*Separated by commas or asterisks. Asterisk separation has precedence over comma separation.</div>
EOT;
    }
    
    print_site_page('print_site_content', 9);
?>