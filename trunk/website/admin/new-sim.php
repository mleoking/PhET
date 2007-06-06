<?php
    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/password-protection.php");
    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
	
    db_exec_query("INSERT INTO `simulation` (`sim_name`, `sim_keywords`) VALUES ('New Simulation', 'keyword1, keyword2, keyword3') ");
     
    $sim_id = mysql_insert_id($connection);
    
    force_redirect("edit-sim.php?sim_id=$sim_id");
?>