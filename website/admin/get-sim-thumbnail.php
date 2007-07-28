<?php
	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/sys-utils.php");
	
	$sim_id = $_REQUEST['sim_id'];
	
	$sim = sim_get_sim_by_id($sim_id);
	
	$file = sim_get_thumbnail($sim);
	
	send_file_to_browser($file, file_get_contents($file), 'image/jpeg');
?>