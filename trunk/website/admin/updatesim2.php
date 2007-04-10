<?
   ini_set('display_errors', '1');

	include_once("db.inc");

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
.style2 {color: #CC6600}
.style17 {
	color: #000000;
	font-weight: bold;
}
.style18 {font-size: 10px}
-->
    </style>

");

    include_once("sim-utils.php");
    
    gather_verify_sim_vars();

    //CONFIRM PAGE
    /* if ($confirm == null) { print "<center><b>Is everything here correct for your simulation before submitting it to the database?</b>";
    include 'updateexistingsim.php';
     exit();
    } */

    print ("<center><font size=3><strong><span class=style1>The following information was successfully edited in the Database!</span></strong></font><br><font class=style1 size=3>(<a href=updateexistingsim.php?simid=?>click here to go back</a>)</font><br><br></center> ");
    
    print ("<div align=center>
        <table width=755 border=0 bordercolor=#FFFFFF bgcolor=#FFFFFF>
          <tr bgcolor=#FFFFFF>
            <td colspan=2 class=style16>
              <p align=left><strong><span class=style17><u>SIMULATION</u>_____________________________________</span></strong></p>
              <p align=left><span class=style16><strong>Simulation Title</strong></span>*: </b><span class=style2>$simtitle</span>        
                     <br>
                  <span class=style4>*make sure the title is unique or it will not get added to the database!</span><br>
                  <br>
                  <span class=style16><strong>URL to Simulation (jnlp file or swf file):</strong></span> <span class=style2>$simurl</span>
                  <br><span class=style16><strong>Type:</strong></span> <span class=style2>$printtype</span><br>
        		  <br><span class=style16><strong>Size:</strong></span> <span class=style2>$simsize</span><br>
                  <br>
        		  <span class=style16><strong>URL to Thumbnail (gif file):</strong></span> <span class=style2>$thumburl</span><br>
        		  <img src=$thumburl width=130 height=97>
                  <br>
              </p>
            </td>
          </tr>
          <tr bgcolor=#FFFFFF>
            <td colspan=2 class=style16><div align=left>
              <p><strong><span class=style17><u>OPTIONS</u>________________________________________</span></strong></p>
              <p align=left><strong> Rating for this Sim:</strong> <span class=style2>$rate_sim</span>          
                         <br>
                  <span class=style18>*for more info on ratings, please click here </span><br>
              </p>
            </div>
              <p align=left class=style16><br>
                <strong>Simulation Description (Abstract)</strong>:<br> 
                <span class=style2>$simdesc</span><br>
                  </p>
        		      <p align=left class=style16><br>
                <strong>User Tips</strong>:<br> 
                <span class=style2>$usertips</span><br><br><br>
        		<span class=style16><strong>URL to Teaching Ideas (pdf file):</strong></span> <span class=style2>$teachingideas</span>
                  </p>
              </p><br>
              </td>
          </tr>"
    );

    /*  update SIMULATIONS table  */
    $update_simtest_st = "UPDATE `simtest` SET `simname`='$simtitle', `rating`='$simrating', `desc`='$simdesc', `type`='$simtype', `url_sim`='$simurl', `url_thumb`='$thumburl', `teachingideas`='$teachingideas', `usertips`='$usertips', `size`='$simsize', `learninggoals`='$learninggoals', `systemreq`='$mac_check' WHERE `simid`='$simid'";

    mysql_query($update_simtest_st, $connection);

    print ("<tr bgcolor=#FFFFFF>
        <td colspan=2 class=style16><div align=left>
          <p><span class=style17><u>CATEGORIES</u>_____________________________________</span>__</p>
          </div>"
    );

    print "<br><br>";
?>
