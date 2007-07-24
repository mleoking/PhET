<?php
	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/sys-utils.php");	
	include_once(SITE_ROOT."admin/web-utils.php");
	
	$sim_id = $_REQUEST['sim_id'];
	
	$simulation = sim_get_sim_by_id($sim_id);
	
	if ($simulation['sim_type'] == SIM_TYPE_JAVA) {
		$sim_file_location = sim_get_run_offline_jar_location($simulation);
		
		$mime_type = "application/java-archive";

	}
	else {
		$sim_file_location = $simulation['sim_launch_url'];
		
		$mime_type = "application/x-Shockwave-Flash";
	}
	
	$sim_file = file_get_contents($sim_file_location);
	
	send_file_to_browser($sim_file_location, $sim_file, $mime_type, "attachment");
?>