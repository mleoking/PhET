<?php
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("web-utils.php");
	include_once("sim-utils.php");
	
	gather_sim_fields_into_globals($_REQUEST['sim_id']);

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
    
    ?>
    
	<style type=text/css>
        <!--
        .style1 {font-family: Arial, Helvetica, sans-serif}
        .style3 {
        	font-family: Arial, Helvetica, sans-serif;
        	font-weight: bold;
        	font-size: 18px;
        	color: #ED6907;
        }
        .style16 {color: #666666; font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; }
        .style17 {
        	color: #000000;
        	font-weight: bold;
        }
        .style18 {font-size: 10px}
        -->
    </style>

<p align="left" class="style3">Edit Simulation Parameters</p>
    
<form enctype=multipart/form-data action=update-sim.php method=post>
    <?php
    
    print("<input type=hidden name=sim_id value=$sim_id>");
    
    print_captioned_editable_area("Specify the name of the simulation",                "sim_name",       $sim_name,       "1");
    print_captioned_editable_area("Specify the URL that launches the simulation",      "sim_launch_url", $sim_launch_url, "1");
    print_captioned_editable_area("Specify the URL of the JPEG simulation screenshot", "sim_image_url",  $sim_image_url,  "1");

    print ("<div class=style16>Please select a rating for this simulation</div>
            <p>
                <input name=sim_rating type=radio value=0 $bmin_check>
                <img src=../images/sims/ratings/beta-minus-rating.gif width=16 height=16>
                <input name=sim_rating type=radio value=1 $bplus_check>
                <img src=../images/sims/ratings/beta-plus-rating.gif width=16 height=16>
                <input name=sim_rating type=radio value=2 $b_check>
                <img src=../images/sims/ratings/beta-rating.gif width=16 height=16>
                <input name=sim_rating type=radio value=3 $star_check>
                <img src=../images/sims/ratings/star-rating.gif width=16 height=16>
                <input name=sim_rating type=radio value=4 $tri_check>
                <img src=../images/sims/ratings/alpha-rating.gif width=16 height=14><br/>
            </p>");
  
    print_captioned_editable_area("Simulation Description", "sim_desc", $sim_desc, "10");
    print_captioned_editable_area("Enter the keywords to associated with the simulation*", 
                                  "sim_keywords", $sim_keywords, "2");
    print_captioned_editable_area("Enter the members of the design team*",               "sim_design_team",     $sim_design_team, "2");
    print_captioned_editable_area("Enter the libraries used by the simulation*",         "sim_libraries",       $sim_libraries,   "2");  
    print_captioned_editable_area("Enter the 'thanks to' for the simulation*",           "sim_thanks_to",       $sim_thanks_to,   "2");  
    print_captioned_editable_area("Enter the URL for the 'Ideas from PhET' PDF file",    "sim_phet_ideas_file", $sim_phet_ideas_file, "2");    
    print_captioned_editable_area("Enter the main topics*",                              "sim_main_topics",     $sim_main_topics, "2");  
    print_captioned_editable_area("Enter the subtopics*",                                "sim_subtopics",       $sim_subtopics,   "2");  
    print_captioned_editable_area("Enter the sample learning goals*",                    "sim_sample_goals",    $sim_sample_goals,"2");
    
    print("<div class=style16>Please select the categories you would like the Simulation to appear under:</div>");
      
    function print_category_checkbox($cat_id, $cat_name) {
        global $sim_id, $connection;

        $sql_cat        = "SELECT * FROM `simulation_listing` WHERE `sim_id`= '$sim_id' AND `cat_id`='$cat_id' ";
        $sql_result_cat = mysql_query($sql_cat, $connection);
        $row_cat        = mysql_num_rows($sql_result_cat);

        $is_checked = ($row_cat >= 1 ? "checked" : "");

        print "<input type=\"checkbox\" name=\"checkbox_cat_id_$cat_id\" value=\"true\" $is_checked>$cat_name<br/>";          
    }

    function print_category_checkboxes() {
        global $connection;

        $select_categories_st = "SELECT * FROM `category` ";
        $category_rows        = mysql_query($select_categories_st, $connection);

        while ($category_row=mysql_fetch_array($category_rows)) {
            $cat_id   = $category_row[0];
            $cat_name = $category_row[1];

            print_category_checkbox($cat_id, $cat_name);
        }
    }

    print_category_checkboxes();

    print ("</p><p><input type=submit value=Submit name=submit></p></form>");
    
    print("<div class=style16>*Separated by commas.</div>");
?>