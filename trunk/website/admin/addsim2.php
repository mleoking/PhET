<?
    include_once("password-protection.php");
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
    include 'addnewsim2.php';
     exit();
    } */

    print ("<center><font size=3><strong><span class=style1>The following information was successfully added to the Database!</span></strong></font><br><font class=style1 size=3>(<a href=addnewsim2.php>click here to go back</a>)</font><br><br></center> ");
    print ("
    <div align=center>
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
            <strong>Keywords</strong>:<br> 
            <span class=style2>$keywords</span><br><br><br>
    		      <p align=left class=style16><br>
            <strong>User Tips</strong>:<br> 
            <span class=style2>$usertips</span><br><br><br>
    		<p align=left class=style16><br>
            <strong>Learning Goals</strong>:<br> 
            <span class=style2>$learninggoals</span><br><br><br>
    		<span class=style16><strong>URL to Teaching Ideas (pdf file):</strong></span> <span class=style2>$teachingideas</span><br><br>
    		<span class=style16>Compatible with Mac?</strong></span> <span class=style2>$mac_print</span></b>
              </p>
          </p><br>
          </td>
      </tr>");

    /*  insert into SIMULATIONS table  */
    $sql = "INSERT INTO `simtest` (`simname`, `rating`, `desc`, `type`, `url_sim`, `url_thumb`, `teachingideas`, `usertips`, `size`, `learninggoals`, `systemreq`, `keywords`) VALUES ('$simtitle','$simrating','$simdesc','$simtype', '$simurl', '$thumburl', '$teachingideas', '$usertips', '$simsize', '$learninggoals', '$mac', '$keywords') ";
    
    $sql_result = mysql_query($sql,$connection) or die (mysql_error());

    print ("
      <tr bgcolor=#FFFFFF>
        <td colspan=2 class=style16><div align=left>
          <p><span class=style17><u>CATEGORIES</u>_____________________________________</span>__</p>
          </div>"
    );
    
    function add_sim_to_category($catid, $catname, $checkname) {
        if (isset($HTTP_GET_VARS["$checkname"])) {
           $sql="INSERT INTO `simcat` (`sim_id`, `category`) VALUES ('$sim_id','$catid') ";
           $sql_result=  mysql_query($sql, $connection);
           
           print "$catname<br>";
       }
    }
    
    // start CATEGORY insert
    $sql        = "SELECT `simid` FROM `simtest` WHERE `simname`='$simtitle' ";
    $sql_result = mysql_query($sql);

    if ($row = mysql_fetch_row($sql_result)) {
       $sim_id = $row[0];

       add_sim_to_category(0, "Top Sims",                            "check_topsims");      
       add_sim_to_category(1, "New Sims",                            "check_newsims");      
       add_sim_to_category(2, "Index",                               "check_index");      
       add_sim_to_category(3, "Motion",                              "check_motion");      
       add_sim_to_category(4, "Work, Energy, &amp; Power",           "check_energy");      
       add_sim_to_category(5, "Sound &amp; Waves",                   "check_sound");      
       add_sim_to_category(6, "Heat &amp; Thermo",                   "check_heat");      
       add_sim_to_category(7, "Electricity, Magnets &amp; Circuits", "check_electricity");      
       add_sim_to_category(8, "Light &amp; Radiation",               "check_light");      
       add_sim_to_category(9, "Quantum Phenomena",                   "check_quantum");      
       add_sim_to_category(10, "Chemistry",                          "check_chemistry");      
       add_sim_to_category(11, "Math Tools",                         "check_math");      
       add_sim_to_category(12, "Cutting-Edge Research",              "check_cuttingedge");

       print ("</td></tr></table></div>");
    }
    else {
        print (mysql_error());
    }

    print "<br><br>";
?>
