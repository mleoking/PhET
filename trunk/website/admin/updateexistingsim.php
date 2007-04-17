<?php
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("web-utils.php");
	
    $sim_id          = $_REQUEST['sim_id'];
    $sql_sim        = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";
    $sql_result_sim = mysql_query($sql_sim);

	while ($row2 = mysql_fetch_row($sql_result_sim)) {
        $simtitle       = $row2[1];
	    $simrating      = $row2[2];
	    $simtype        = $row2[3];
	    $simsize        = $row2[4];
	    $simurl         = $row2[5];
	    $thumburl       = $row2[6];
	    $simdesc        = $row2[7];
	    $keywords       = $row2[8];
	    $sim_system_req      = $row2[9];
	    $sim_teaching_ideas  = $row2[10];
        $sim_user_tips       = $row2[11];
        $sim_learning_goals  = $row2[12];
	
        if ($sim_system_req == '1') { 
            $mac_ch = "checked";
        }
        else {
            $mac_ch = " ";
        }

        $bmin_check  = generate_check_status("0", $simrating);
        $b_check     = generate_check_status("1", $simrating);
        $bplus_check = generate_check_status("2", $simrating);
        $star_check  = generate_check_status("3", $simrating);
        $tri_check   = generate_check_status("4", $simrating);
        
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
	
<FORM ENCTYPE=multipart/form-data ACTION=updatesim2.php>
<p align=center class=style3>EDIT AN EXISTING SIMULATION</p>



<center>
<table width=655 border=0 bordercolor=#FFFFFF bgcolor=#FFFFFF>
  <tr bgcolor=#FFFFFF>
    <td colspan=2 class=style1><div align=center class=style16>
      <p align=left class=style17><u>SIMULATION</u>_____________________________________</p>
      <p align=left><strong>Simulation Title</strong>*: 
      <input type=hidden name=sim_id value=$sim_id>
        <input name=title type=text size=70 maxlength=70 value=\"$simtitle\">
          <br>
          <span class=style18>*make sure the title is unique or it will not get added to the database! </span><br>
          <br>
          <strong>Please include the URL of the <u>Simulation</u> File (jnlp, swf, htm, or html):</strong> <input name=simurl type=text size=70 value=\"$simurl\"><br>
          <br>
		  <strong>File size of the Simulation:</strong> <input name=simsize type=text size=10 value=\"$simsize\"> KB<br>
          <br>
		  <strong>Please include the URL of the <u>Thumbnail</u> File (gif, jpg, or jpeg):</strong> <input name=thumburl type=text size=70 value=\"$thumburl\"><br>
          <br><br><br>
	  		  <strong>If this Simulation is not compatible with Mac, please check this box:</strong>  <input type=checkbox name=mac_check value=mac_check id=mac_check $mac_ch> NO MAC
	  
      </p>
    </div></td>
  </tr>
  <tr bgcolor=#FFFFFF>
    <td colspan=2 class=style16><div align=left>
      <p><strong><span class=style17><u>OPTIONS</u>________________________________________</span></strong></p>
      <p align=left><strong> Please Select a Rating for this Sim:</strong> 
          <input name=rating type=radio value=0 $bmin_check>
          <img src=../web-pages/Design/Assets/images/beta-minus-rating.gif width=16 height=16>
          <input name=rating type=radio value=1 $bplus_check>
          <img src=../web-pages/Design/Assets/images/beta-plus-rating.gif width=16 height=16>
          <input name=rating type=radio value=2 $b_check>
          <img src=../web-pages/Design/Assets/images/beta-rating.gif width=16 height=16>
          <input name=rating type=radio value=3 $star_check>
          <img src=../web-pages/Design/Assets/images/star-rating.gif width=16 height=16>
          <input name=rating type=radio value=4 $tri_check>
          <img src=../web-pages/Design/Assets/images/alpha-rating.gif width=16 height=14><br>
          <span class=style18>*for more info on ratings, please click here </span><br>
      </p>
    </div>
      <p align=left class=style16><br>
  Simulation Description (Abstract) <br>
  <textarea name=simdesc cols=50>$simdesc</textarea>
      </p>
      <p align=left> </p>
      <p align=left class=style16><br>
  User Tips <br>
  <textarea name=sim_user_tips cols=50>$sim_user_tips</textarea>
      </p>
	   <p align=left class=style16><br>
  Sample Learning Goals <br>
  <textarea name=sim_learning_goals cols=50>$sim_learning_goals</textarea>
      </p>
      <strong>Please include the URL to the Teaching Ideas file (pdf):</strong> <input name=sim_teaching_ideas type=text size=70 value=\"$sim_teaching_ideas\"><br>
      </p></td>
  </tr>
  <tr bgcolor=#FFFFFF>
    <td height=320 colspan=2 class=style16><div align=left><p><span class=style17><u>CATEGORIES</u>_____________________________________</span>__</p>
      </div>
      <p align=left><span class=style18>Please select the categories you would like the Simulation to appear under (*can also be edited later)</span>
	  <br><font size=2>****(CATEGORY EDITING NOT YET ACTIVE)****</font>
	   </p>
      <p align=left>");
      
      function print_category_checkbox($catid, $catname, $checkname) {
          global $sim_id;
          
      	  $sql_cat        = "SELECT * FROM `simulation_listing` WHERE `sim_id`= '$sim_id' AND `category_id`='$catid' ";
          $sql_result_cat = mysql_query($sql_cat);
      	  $row_cat        = mysql_num_rows($sql_result_cat);

          if ($row_cat >= '1') {
              print "<input type=checkbox name=$checkname value=$checkname id=$checkname checked>$catname<br>";
          }
          else {
              print "<input type=checkbox name=$checkname value=$checkname id=$checkname>$catname<br>";
          }          
      }
      
      print_category_checkbox(0, "Top Sims",                            "check_topsims");      
      print_category_checkbox(1, "New Sims",                            "check_newsims");      
      print_category_checkbox(2, "Index",                               "check_index");      
      print_category_checkbox(3, "Motion",                              "check_motion");      
      print_category_checkbox(4, "Work, Energy, &amp; Power",           "check_energy");      
      print_category_checkbox(5, "Sound &amp; Waves",                   "check_sound");      
      print_category_checkbox(6, "Heat &amp; Thermo",                   "check_heat");      
      print_category_checkbox(7, "Electricity, Magnets &amp; Circuits", "check_electricity");      
      print_category_checkbox(8, "Light &amp; Radiation",               "check_light");      
      print_category_checkbox(9, "Quantum Phenomena",                   "check_quantum");      
      print_category_checkbox(10, "Chemistry",                          "check_chemistry");      
      print_category_checkbox(11, "Math Tools",                         "check_math");      
      print_category_checkbox(12, "Cutting-Edge Research",              "check_cuttingedge");
      
      print ("</td></tr></table><p align=center><input type=submit value=Submit name=B></form>");
  }

?>
