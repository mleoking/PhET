<?php
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("web-utils.php");
	include_once("db-utils.php");
	
    run_sql_statement("INSERT INTO `simulation` (`sim_name`) VALUES ('New Simulation') ");
     
    $sim_id = mysql_insert_id($connection);
    
    forceRedirect("edit-sim.php?sim_id=$sim_id");
?>