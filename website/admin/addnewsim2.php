<?php
    include_once("password-protection.php");
    include_once("web-utils.php");

    $bmin_check  = generate_check_status("0", $simrating);
    $b_check     = generate_check_status("1", $simrating);
    $bplus_check = generate_check_status("2", $simrating);
    $star_check  = generate_check_status("3", $simrating);
    $tri_check   = generate_check_status("4", $simrating);

//TO DO: IF CHECKBOXES

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
	
<FORM ENCTYPE=multipart/form-data ACTION=addsim2.php>
<p align=center class=style3>ADD A SIMULATION TO THE PHET WEBSITE</p>
<font size=2>**Updates: Simulation System Requirements are now automatically selected by the simulation type, URL to Simulation file now includes html files, URL to Thumbnail file now includes jpg/jpeg files, Teaching Ideas link added, User Tips box added.<br>
**3/20/07: .png files now allowed for thumbnails, Sample Learning Goals field added</font><br><br>



<center>
<table width=655 border=0 bordercolor=#FFFFFF bgcolor=#FFFFFF>
  <tr bgcolor=#FFFFFF>
    <td colspan=2 class=style1><div align=center class=style16>
      <p align=left class=style17><u>SIMULATION</u>_____________________________________</p>
      <p align=left><strong>Simulation Title</strong>*: 
      
        <input name=title type=text size=70 maxlength=70 value=\"$simtitle\">
          <br>
          <span class=style18>*make sure the title is unique or it will not get added to the database! </span><br>
          <br>
          <strong>Please include the URL of the <u>Simulation</u> File (jnlp, swf, htm, or html):</strong> <input name=simurl type=text size=70 value=\"$simurl\"><br>
          <br>
		  <strong>File size of the Simulation:</strong> <input name=simsize type=text size=10 value=\"$simsize\"> KB<br>
          <br>
		  <strong>Please include the URL of the <u>Thumbnail</u> File (gif, jpg, jpeg, or png):</strong> <input name=thumburl type=text size=70 value=\"$thumburl\"><br>
          <br>
		  <strong>If this Simulation is not compatible with Mac, please check this box:</strong>  <input type=checkbox name=mac_check value=mac_check id=mac_check> NO MAC
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
      <p align=left class=style16><br>
  Simulation Keywords <br>
  <textarea name=keywords cols=50>$keywords</textarea>
      </p>
      <p align=left class=style16><br>
  User Tips <br>
  <textarea name=usertips cols=50>$usertips</textarea>
      </p>
	  <p align=left class=style16><br>
  Sample Learning Goals <br>
  <textarea name=learninggoals cols=50>$learninggoals</textarea>
      </p>
      <strong>Please include the URL to the Teaching Ideas file (pdf):</strong> <input name=teachingideas type=text size=70 value=\"$teachingideas\"><br>
      </p></td>
  </tr>
  <tr bgcolor=#FFFFFF>
    <td height=320 colspan=2 class=style16><div align=left>
      <p><span class=style17><u>CATEGORIES</u>_____________________________________</span>__</p>
      </div>
      <p align=left><span class=style18>Please select the categories you would like the Simulation to appear under (*can also be edited later)</span> </p>
      <p align=left>
        <input type=checkbox name=check_topsims value=check_topsims id=check_topsims> 
        Top Sims<br>
        <input type=checkbox name=check_newsims value=check_newsims id=check_newsims> 
        New Sims<br>
		<input type=checkbox name=check_index value=check_index id=check_index checked> 
INDEX
		</p>
      <p align=left>
        <input type=checkbox name=check_motion value=check_motion id=check_motion> 
        Motion<br>
        <input type=checkbox name=check_energy value=check_energy id=check_energy> 
        Work, Energy &amp; Power
<br>
<input type=checkbox name=check_sound value=check_sound id=check_sound> 
Sound &amp; Waves
<br>
<input type=checkbox name=check_heat value=check_heat id=check_heat> 
Heat &amp; Thermo
<br>
<input type=checkbox name=check_electricity value=check_electricity id=check_electricity> 
Electricity, Magnets &amp; Circuits
<br>
<input type=checkbox name=check_light value=check_light id=check_light> 
Light &amp; Radiation<br>
<input type=checkbox name=check_quantum value=check_quantum id=check_quantum> 
Quantum Phenomena<br>
<input type=checkbox name=check_chemistry value=check_chemistry id=check_chemistry> 
Chemistry<br>
<input type=checkbox name=check_math value=check_math id=check_math> 
Math Tools<br>
<input type=checkbox name=check_cuttingedge value=check_cuttingedge> 
Cutting Edge Research</p>
      </td>
    </tr>


</table>
<p align=center><input type=submit value=Submit name=B>

</form>

");

?>
