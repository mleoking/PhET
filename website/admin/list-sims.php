<?
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("web-utils.php");
	include_once("sim-utils.php");

    print "<b>Simulation Listing</b><br/><br/>";
    
    // start selecting SIMULATIONS from database table
    $select_all_sims_st  = "SELECT * FROM `simulation` ORDER BY `sim_name` ASC ";
    $query_result = mysql_query($select_all_sims_st);

    while ($simulation = mysql_fetch_assoc($query_result)) {
        print "<table>";
        
        foreach($simulation as $key => $value) {
            print "<tr><td>$key</td><td>$value</td></tr>";
        }
        
        gather_array_into_globals($simulation);
        
        print "<tr><td><a href=\"delete-sim.php?sim_id=$sim_id&amp;delete=0\">delete</a></td></tr>";
        print "<tr><td><a href=\"edit-sim.php?sim_id=$sim_id\">edit</a></td></tr>";
        print "</table><br/><br/>";
    }
?>
