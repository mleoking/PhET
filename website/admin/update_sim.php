<?php
    ini_set('display_errors', '1');

	include_once("db.inc");	
	
	//start drop down menu & form
	
	print ("<form action=updateexistingsim.php method=post><select name=simid>");
	
    $simid              = $_REQUEST['simid'];
    $select_simtests_st = "SELECT * FROM `simtest` ORDER BY `simname` ASC ";
    $simtest_table      = mysql_query($select_simtests_st);
	
    while ($sim = mysql_fetch_row($simtest_table)) {      
        $simid    = $sim[0];
        $simtitle = $sim[1];
	
        //print drop down menu
        print "<OPTION VALUE=\"$simid\">$simtitle";
    }

    // close drop down menu and form
    print "</select><input type=submit value=\"EDIT\"></form>";
?>
