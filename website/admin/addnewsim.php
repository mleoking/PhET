<?php
    include_once("password-protection.php");
    include_once("web-utils.php");

    $bmin_check  = generate_check_status("0", $simrating);
    $b_check     = generate_check_status("1", $simrating);
    $bplus_check = generate_check_status("2", $simrating);
    $star_check  = generate_check_status("3", $simrating);
    $tri_check   = generate_check_status("4", $simrating);

    $javacheck   = generate_check_status("0", $simtype);
    $flashcheck  = generate_check_status("1", $simtype);

    print ("
	
<style type=text/css>
<!--
.style1 {font-family: Arial, Helvetica, sans-serif}
.style2 {font-size: xx-small}
.style3 {
	font-family: Georgia, Times New Roman, Times, serif;
	font-weight: bold;
	font-size: 18px;
}
.style4 {color: #FFFFFF}
.style5 {font-family: Arial, Helvetica, sans-serif; color: #FFFFFF; }
.style6 {
	color: #FFFF99;
	font-weight: bold;
}
-->
</style>
	
<FORM ENCTYPE=multipart/form-data ACTION=addsim2.php>
<p align=center class=style3>ADD A SIMULATION TO THE PHET WEBSITE</p>



<center>
<table width=655 border=0 bordercolor=#FFFFFF bgcolor=#FFFFFF>
  <tr bgcolor=#003366>
    <td colspan=2 class=style1><div align=center class=style4>
      <p><span class=style6>Simulation Title</span>*: 
    
        <input name=title type=text size=70 maxlength=70 value=$simtitle>
        <br>
        <span class=style2>*make sure the title is unique or it will not get added to the database! </span><br>
</p>
      </div></td>
  </tr>
  <tr bgcolor=#003366>
    <td width=320 height=49 class=style5><span class=style6>Please Select a Rating for this Sim:</span><br>
      <input name=rating type=radio value=0 $bmin_check> 
      <img src=../web-pages/Design/Assets/images/beta-minus-rating.gif width=16 height=16>
      <input name=rating type=radio value=1 $bplus_check>      <img src=../web-pages/Design/Assets/images/beta-plus-rating.gif width=16 height=16>
      <input name=rating type=radio value=2 $b_check>
      <img src=../web-pages/Design/Assets/images/beta-rating.gif width=16 height=16>
      <input name=rating type=radio value=3 $star_check>
      <img src=../web-pages/Design/Assets/images/star-rating.gif width=16 height=16>
      <input name=rating type=radio value=4 $tri_check>
      <img src=../web-pages/Design/Assets/images/alpha-rating.gif width=16 height=14><br>
      <span class=style2>*for more info on ratings, please click here </span><br> </td>
    <td width=325 rowspan=3 class=style5><p><span class=style6><strong>Simulation URL:<br> 
            <input name=simurl type=text id=simurl size=50 value=$simurl>
            
            Simulation Thumbnail URL:<br>
            <strong>
            <input name=simthumb type=text id=simthumb size=50 value=$simthumb>
            </strong><br>
            <br>
      Simulation Description (Abstract)</strong></span><br>
          <textarea name=simdesc cols=50>$simdesc</textarea>
    </p>
      <p>        <span class=style6>System Requirements:</span><br>
          <textarea name=sysreq cols=50>$simsysreq</textarea> 
      </p></td>
  </tr>
  <tr bgcolor=#003366>
    <td height=67 class=style1><p class=style4><strong class=style6>Please Select the Type of Sim:</strong><br>
        <input name=simtype type=radio value=0 $javacheck>
        <img src=../web-pages/Design/Assets/images/java.png width=32 height=16>
        <input name=simtype type=radio value=1 $flashcheck>
        <img src=../web-pages/Design/Assets/images/flash.png width=32 height=16><br>
        <span class=style2>*note that .swf files do not work with Internet Explorer 7
        unless embedded in a .html file</span> <br>
      </p>
      </td>
    </tr>
  <tr bgcolor=#003366>
    <td height=21 class=style5><span class=style6>How big is the file size?<br> 
      (of the .swf or .jnlp file)</span><br>
      <input name=simsize type=text maxlength=6 width=48 value=$simsize>      
      <strong>kb</strong></td>
  </tr>
<tr bgcolor=#003366>
    <td height=311 colspan=2 class=style5><div align=center>
      <p><span class=style6>Please Select which of the following categories the Simulation should show up under:</span></p>
      <p align=left>
        <input type=checkbox name=check_topsims value=c> 
        Top Sims<br>
        <input type=checkbox name=check_newsims value=1> 
        New Sims<br>
		<input type=checkbox name=check_index value=1 checked> 
INDEX
		</p>
      <p align=left>
        <input type=checkbox name=check_motion value=1> 
        Motion<br>
        <input type=checkbox name=check_energy value=1> 
        Work, Energy &amp; Power
<br>
<input type=checkbox name=check_sound value=1> 
Sound &amp; Waves
<br>
<input type=checkbox name=check_heat value=1> 
Heat &amp; Thermo
<br>
<input type=checkbox name=check_electricity value=1> 
Electricity, Magnets &amp; Circuits
<br>
<input type=checkbox name=check_light value=1> 
Light &amp; Radiation<br>
<input type=checkbox name=check_quantum value=1> 
Quantum Phenomena<br>
<input type=checkbox name=check_chemistry value=1> 
Chemistry<br>
<input type=checkbox name=check_math value=1> 
Math Tools<br>
<input type=checkbox name=check_cuttingedge value=1> 
Cutting Edge Research<br><br>
    </p>
      </div></td>
  </tr>
  <tr bgcolor=#666666>
    <td colspan=2 class=style5>
	<p align=center><br>(*NOTE: You will have the chance to review your SIM before adding it to the database*)<br><input type=submit value=Submit name=B><br><br>
	</p></td></tr>
</table>


</form>

");

?>
