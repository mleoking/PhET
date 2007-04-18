<?php
    include_once("password-protection.php");
	include_once("db.inc");
	include_once("web-utils.php");
	include_once("db-utils.php");
	
    run_sql_statement("INSERT INTO `simulation` (`sim_name`, `sim_keywords`) VALUES ('New Simulation', 'keyword1, keyword2, keyword3') ");
     
    $sim_id = mysql_insert_id($connection);
    
    force_redirect("edit-sim.php?sim_id=$sim_id");
?>