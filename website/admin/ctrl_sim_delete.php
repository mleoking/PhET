<?
    include_once("password-protection.php");
    include_once("db.inc");

    print "<b>DELETE SIMULATION FROM DATABASE</b><br><br>";
    
    $sim_id  = $_REQUEST['sim_id'];
    $delete = $_REQUEST['delete'];

    function deletesim() {
        $sim_id      = $_REQUEST['sim_id'];
        // delete from SIMULATIONS TABLE
        $sql        = "DELETE FROM `simulation` WHERE `sim_id`='$sim_id' ";
        $sql_result = mysql_query($sql);

        // delete from CATEGORIES TABLE
        $sql        = "DELETE FROM `simulation_listing` WHERE `sim_id`='$sim_id' ";
        $sql_result = mysql_query($sql);

        print "Successfully deleted the Simulation from the database<br><br><br><a href=ctrl_simsindex.php>click here to go back</a>";
        
        exit();
    }

    if ($delete == '1') { 
        deletesim();
    }

    // first select what SIMULATION to delete
    $sql        = "SELECT * FROM `simulation` WHERE `sim_id`='$sim_id' ";
    $sql_result = mysql_query($sql);

    while ($row = mysql_fetch_row($sql_result)) {       
        $sim_id    = $row[0];
        $sim_name   = $row[1];
        $rating    = $row[2];
        $type      = $row[3];
        $size      = $row[4];
        $sim_launch_url   = $row[5];
        $sim_image_url = $row[6];
        $sim_desc      = $row[7];
        $keywords  = $row[8];
        $sim_system_req = $row[9];

        print "<b>Are you sure you want to delete this simulation?</b><br><br>";
        
        print "$sim_id;$sim_name;$rating;$type;$size;$sim_launch_url;$sim_image_url;$sim_desc;$keywords;$sim_system_req<br><br>";
        
        print "<a href=ctrl_sim_delete.php?sim_id=$sim_id&delete=1>Yes</a>   |    <a href=ctrl_simsindex.php>NO</a>";
    }
		
    print "<br><br>";
?>
