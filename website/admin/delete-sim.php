<?
	include_once("../admin/global.php");
	
    include_once(SITE_ROOT."admin/password-protection.php");
    include_once(SITE_ROOT."admin/db.inc");	
	include_once(SITE_ROOT."admin/site-utils.php");

    function deletesim() {
        $sim_id      = $_REQUEST['sim_id'];
        // delete from SIMULATIONS TABLE
        $sql        = "DELETE FROM `simulation` WHERE `sim_id`='$sim_id' ";
        $sql_result = mysql_query($sql);

        // delete from CATEGORIES TABLE
        $sql        = "DELETE FROM `simulation_listing` WHERE `sim_id`='$sim_id' ";
        $sql_result = mysql_query($sql);

        print "<p>Successfully deleted the simulation from the database. <a href=\"list-sims.php\">Click here to go back.</a></p>";
    }

	function print_page() {
		print "<h1>Delete Simulation</h1>";
		
	    $sim_id = $_REQUEST['sim_id'];
	    $delete = isset($_REQUEST['delete']) ? $_REQUEST['delete'] : 0;

	    if ($delete == '1') { 
	        deletesim();
	    }
		else {
		    // first select what SIMULATION to delete
		    $sql        = "SELECT * FROM `simulation` WHERE `sim_id`='$sim_id' ";
		    $sql_result = mysql_query($sql);

		    while ($row = mysql_fetch_assoc($sql_result)) {       
		        $sim_name = $row['sim_name'];

		        print "<p><b>Are you sure you want to delete the simulation \"$sim_name\"?</b></p>";
        
		        print "<p><a href=delete-sim.php?sim_id=$sim_id&delete=1>Yes</a> | <a href=list-sims.php>NO</a></p>";
		    }
		}
	}
	
	print_site_page('print_page', 9);
?>
