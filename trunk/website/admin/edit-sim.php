<?php
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("web-utils.php");
	include_once("sim-utils.php");
	
	$sim_id             = $_REQUEST['sim_id'];
    $select_sim_st      = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";
    $simulation         = mysql_fetch_row(mysql_query($select_sim_st));
    
    $sim_name           = $simulation[1];
    $sim_rating         = $simulation[2];
    $sim_type           = $simulation[3];
    $sim_size           = $simulation[4];
    $sim_launch_url     = $simulation[5];
    $sim_image_url      = $simulation[6];
    $sim_desc           = $simulation[7];
    $sim_keywords       = $simulation[8];
    $sim_system_req     = $simulation[9];
    $sim_teaching_ideas = $simulation[10];
    $sim_user_tips      = $simulation[11];
    $sim_learning_goals = $simulation[12];

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
    
    print ("
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
	
<form enctype=multipart/form-data action=update-sim.php method=post>
<p align=center class=style3>EDIT AN EXISTING SIMULATION</p>




<center>
<table width=655 border=0 bordercolor=#FFFFFF bgcolor=#FFFFFF>
  <tr bgcolor=#FFFFFF>
    <td colspan=2 class=style1><div align=center class=style16>
      <p align=left class=style17><u>SIMULATION</u>_____________________________________</p>
      <p align=left><strong>Simulation Title</strong>*: 
        <input type=hidden name=sim_id value=$sim_id>
        <input name=sim_name type=text size=70 maxlength=70 value=\"$sim_name\">
          <br/>
          <span class=style18>*make sure the title is unique or it will not get added to the database! </span><br/>
          <br/>
          <strong>Please include the URL of the <u>Simulation</u> File (jnlp, swf, htm, or html):</strong> 
          <input name=sim_launch_url type=text size=70 value=\"$sim_launch_url\"><br/>
          <br/>
		  <strong>File size of the Simulation:</strong> 
		  <input name=sim_size type=text size=10 value=\"$sim_size\"> KB<br/>
          <br/>
		  <strong>Please include the URL of the <u>Thumbnail</u> File (gif, jpg, or jpeg):</strong>
		  <input name=sim_image_url type=text size=70 value=\"$sim_image_url\"><br/>
          <br/><br/><br/>
	  		  <strong>If this Simulation is not compatible with Mac, please check this box:</strong>
	  		  <input type=checkbox name=mac_check value=mac_check id=mac_check $mac_ch> NO MAC
	  
      </p>
    </div></td>
  </tr>
  <tr bgcolor=#FFFFFF>
    <td colspan=2 class=style16><div align=left>
      <p><strong><span class=style17><u>OPTIONS</u>________________________________________</span></strong></p>
      
      <p align=left><strong> Please Select a Rating for this Sim:</strong> 
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
          <span class=style18>*for more info on ratings, please click here </span><br/>
      </p>
    </div>
      <p align=left class=style16><br/>
  Simulation Description (Abstract) <br/>");
  
  print_editable_area("sim_desc", $sim_desc);
  
  print("
      </p>
      <p align=left> </p>
      <p align=left class=style16><br/>
  User Tips <br/>
  <textarea name=sim_user_tips cols=50>$sim_user_tips</textarea>
      </p>
	   <p align=left class=style16><br/>
  Sample Learning Goals <br/>
  <textarea name=sim_learning_goals cols=50>$sim_learning_goals</textarea>
      </p>
      <strong>Please include the URL to the Teaching Ideas file (pdf):</strong> 
      <input name=sim_teaching_ideas type=text size=70 value=\"$sim_teaching_ideas\"><br/>
      </p></td>
  </tr>
  <tr bgcolor=#FFFFFF>
    <td height=320 colspan=2 class=style16><div align=left><p><span class=style17><u>CATEGORIES</u>_____________________________________</span>__</p>
      </div>
      <p align=left><span class=style18>Please select the categories you would like the Simulation to appear under:</span>
	  <br/><font size=2>****(CATEGORY EDITING NOT YET ACTIVE)****</font>
	   </p>
      <p align=left>");
      
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
      
      print ("</td></tr></table><p align=center><input type=submit value=Submit name=submit></form>");
?>