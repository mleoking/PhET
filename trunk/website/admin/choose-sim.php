<?php
    include_once("password-protection.php");
    include_once("db.inc");	
	
	//start drop down menu & form
	
	print ("<form action=\"edit-sim.php\" method=post><select name=\"sim_id\">");
	
    $select_simulations_st = "SELECT * FROM `simulation` ORDER BY `sim_name` ASC ";
    $simulation_table      = mysql_query($select_simulations_st);
	
    while ($sim = mysql_fetch_row($simulation_table)) {      
        $sim_id   = $sim[0];
        $simtitle = $sim[1];
	
        //print drop down menu
        print "<option value=\"$sim_id\">$simtitle";
    }

    // close drop down menu and form
    print "</select><input type=\"submit\" value=\"edit\"></form>";
?>
