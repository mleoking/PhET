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
	
	function print_site_content() {
	    $simulation = sim_get_sim_by_id($_REQUEST['sim_id']);
	    
	    if (!$simulation) {
	        print "<h1>No Simulation Found</h1><p>There is no simulation with the specified id.</p>";
	        return;
	    }
	    
	    $sim_code = get_code_to_create_variables_from_array($simulation);
	    
        eval($sim_code);

        if ($sim_system_req == SIM_SYSTEM_REQ_NO_MAC) { 
            $mac_ch = "checked";
        }
        else {
            $mac_ch = " ";
        }

        $bmin_check  = generate_check_status(SIM_RATING_BETA_MINUS, $sim_rating);
        $b_check     = generate_check_status(SIM_RATING_BETA,       $sim_rating);
        $bplus_check = generate_check_status(SIM_RATING_BETA_PLUS,  $sim_rating);
        $star_check  = generate_check_status(SIM_RATING_CHECK,      $sim_rating);
        $tri_check   = generate_check_status(SIM_RATING_ALPHA,      $sim_rating);
    
        print <<<EOT
            <h1>Edit Simulation Parameters</h1>
    
            <form enctype="multipart/form-data" action="update-sim.php" method="post">
    
                <input type="hidden" name="sim_id" value="$sim_id" />
EOT;
    
        print_captioned_editable_area("Specify the name of the simulation",             "sim_name",         $sim_name,       "1");
    
        print_captioned_editable_area("Specify the URL that launches the simulation",   "sim_launch_url",   $sim_launch_url, "1");
    
        print_captioned_url_upload_control("Specify the URL of the JPEG simulation screenshot", "sim_image_url",  $sim_image_url,  "2");   
    
        print_captioned_url_upload_control("Specify the URL of the animated GIF preview",
                                           "sim_animated_image_url", $sim_animated_image_url, "2");

        print <<<EOT
    <div>Please select a rating for this simulation</div>
                <p>
                    <input name="sim_rating" type="radio" value="0" $bmin_check />
                    <img src="../images/sims/ratings/beta-minus-rating.gif" width="16" height="16" />
                    <input name="sim_rating" type="radio" value="1" $bplus_check />
                    <img src=../images/sims/ratings/beta-plus-rating.gif width="16" height="16" />
                    <input name="sim_rating" type="radio" value="2" $b_check />
                    <img src=../images/sims/ratings/beta-rating.gif width="16" height="16" />
                    <input name="sim_rating" type="radio" value="3" $star_check />
                    <img src=../images/sims/ratings/check_Icon.gif width="16" height="16" />
                    <input name="sim_rating" type="radio" value="4" $tri_check />
                    <img src=../images/sims/ratings/alpha-rating.gif width="16" height="14" />
                </p>
EOT;
  
        print_captioned_editable_area("Simulation Description", "sim_desc", $sim_desc, "10");
        print_captioned_editable_area("Enter the keywords to associated with the simulation*", 
                                      "sim_keywords", $sim_keywords, "2");
        print_captioned_editable_area("Enter the members of the design team*",               "sim_design_team",     $sim_design_team, "2");
        print_captioned_editable_area("Enter the libraries used by the simulation*",         "sim_libraries",       $sim_libraries,   "2");  
        print_captioned_editable_area("Enter the 'thanks to' for the simulation*",           "sim_thanks_to",       $sim_thanks_to,   "2");  

        print_captioned_url_upload_control("Enter the URL for the teacher's guide PDF", "sim_teachers_guide_url", $sim_teachers_guide_url, "2");    
    
        print_captioned_editable_area("Enter the main topics*",                              "sim_main_topics",     $sim_main_topics, "2");  

        print_captioned_editable_area("Enter the sample learning goals*",                    "sim_sample_goals",    $sim_sample_goals,"2");
    
        print("<div>Please select the categories you would like the Simulation to appear under:</div>");

        print_category_checkboxes();
    
        print <<<EOT
            </p>
            
            <p>
                <input type="submit" value="Update" name="submit">
            </p>
            
            </form>
    
            <div>*Separated by commas.</div>
EOT;
    }
    
    print_site_page('print_site_content', 9);
?>