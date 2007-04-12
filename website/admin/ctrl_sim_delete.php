<?
    include_once("password-protection.php");
    include_once("db.inc");

    print "<b>DELETE SIMULATION FROM DATABASE</b><br><br>";
    
    $simid  = $_REQUEST['simid'];
    $delete = $_REQUEST['delete'];

    function deletesim() {
        $simid      = $_REQUEST['simid'];
        // delete from SIMULATIONS TABLE
        $sql        = "DELETE FROM `simtest` WHERE `simid`='$simid' ";
        $sql_result = mysql_query($sql);

        // delete from CATEGORIES TABLE
        $sql        = "DELETE FROM `simcat` WHERE `sim_id`='$simid' ";
        $sql_result = mysql_query($sql);

        print "Successfully deleted the Simulation from the database<br><br><br><a href=ctrl_simsindex.php>click here to go back</a>";
        
        exit();
    }

    if ($delete == '1') { 
        deletesim();
    }

    // first select what SIMULATION to delete
    $sql        = "SELECT * FROM `simtest` WHERE `simid`='$simid' ";
    $sql_result = mysql_query($sql);

    while ($row = mysql_fetch_row($sql_result)) {       
        $sim_id    = $row[0];
        $simname   = $row[1];
        $rating    = $row[2];
        $type      = $row[3];
        $size      = $row[4];
        $url_sim   = $row[5];
        $url_thumb = $row[6];
        $desc      = $row[7];
        $keywords  = $row[8];
        $systemreq = $row[9];

        print "<b>Are you sure you want to delete this simulation?</b><br><br>";
        
        print "$sim_id;$simname;$rating;$type;$size;$url_sim;$url_thumb;$desc;$keywords;$systemreq<br><br>";
        
        print "<a href=ctrl_sim_delete.php?simid=$simid&delete=1>Yes</a>   |    <a href=ctrl_simsindex.php>NO</a>";
    }
		
    print "<br><br>";
?>
