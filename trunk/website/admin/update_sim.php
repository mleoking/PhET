<?php
    include_once("password-protection.php");
    include_once("db.inc");	
	
	//start drop down menu & form
	
	print ("<form action=updateexistingsim.php method=post><select name=sim_id>");
	
    $sim_id              = $_REQUEST['sim_id'];
    $select_simulations_st = "SELECT * FROM `simulation` ORDER BY `sim_name` ASC ";
    $simulation_table      = mysql_query($select_simulations_st);
	
    while ($sim = mysql_fetch_row($simulation_table)) {      
        $sim_id    = $sim[0];
        $simtitle = $sim[1];
	
        //print drop down menu
        print "<OPTION VALUE=\"$sim_id\">$simtitle";
    }

    // close drop down menu and form
    print "</select><input type=submit value=\"EDIT\"></form>";
?>
