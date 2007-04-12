<?php
    include_once("password-protection.php");
    ini_set('display_errors', '1');


	print ("
	
	<style type=text/css>
<!--
.style1 {font-family: Arial, Helvetica, sans-serif}
-->
</style>
	
<FORM ENCTYPE=multipart/form-data ACTION=addsim2.php METHOD=POST>
<p align=center class=style1>ADD A SIMULATION TO THE PHET WEBSITE</p>



<center>
<table width=613 border=0 bordercolor=#FFFFFF bgcolor=#FFFFFF>
  <tr>
    <td colspan=2 class=style1><div align=center>
      <p>Simulation Title: 
    
        <input type=text name=title>
        <br>
        <br>
</p>
      </div></td>
  </tr>
  <tr>
    <td width=306 height=49 class=style1>Please Select a Rating for this Sim:<br>
      <input name=rating type=radio value=0> <img src=../web-pages/Design/Assets/images/beta-minus-rating.gif width=16 height=16>
      <input name=rating type=radio value=1>      <img src=../web-pages/Design/Assets/images/beta-plus-rating.gif width=16 height=16>
      <input name=rating type=radio value=2>
      <img src=../web-pages/Design/Assets/images/beta-rating.gif width=16 height=16>
      <input name=rating type=radio value=3>
      <img src=../web-pages/Design/Assets/images/star-rating.gif width=16 height=16>
      <input name=rating type=radio value=4>
      <img src=../web-pages/Design/Assets/images/alpha-rating.gif width=16 height=14><br>
      <br> </td>
    <td width=297 rowspan=3 class=style1><p>Simulation Description (Abstract)<br>
          <textarea name=simdesc></textarea>
    </p>
      <p>        System Requirements:<br>
          <textarea name=sysreq></textarea> 
      </p></td>
  </tr>
  <tr>
    <td height=67 class=style1><p>Please Select the Type of Sim:<br>
        <input name=type type=radio value=0>
        <img src=../web-pages/Design/Assets/images/java.png width=32 height=16>
        <input name=type type=radio value=1>
        <img src=../web-pages/Design/Assets/images/flash.png width=32 height=16><br>
      </p>
      </td>
    </tr>
  <tr>
    <td height=21 class=style1>How big is the file size (.swf or .jnlp)? <br>
      <input name=size type=text maxlength=6 width=48>      
      <strong>kb</strong></td>
  </tr>

  
</table>
<p align=center><input type=submit value=Submit name=B1><input
type=reset value=Reset name=B2>
</form>

");


?>