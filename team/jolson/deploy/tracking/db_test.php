<?php
/*
	include("db_util.php");
	$link = setup_mysql();
	
	
	$query1 = "INSERT INTO sessions (message_version, sim_type, sim_project, sim_name, sim_major_version, sim_minor_version, sim_dev_version, sim_svn_revision, sim_locale_language, sim_locale_country, sim_sessions_since, sim_sessions_ever, sim_usage_type, sim_distribution_tag, sim_dev, sim_scenario, host_locale_language, host_locale_country, host_simplified_os) VALUES (1, 1, 'pendulum-lab', 'pendulum-lab', 1, 0, 1, 22386, 'en', NULL, 1, 7, 'phet-website', 'none', true, NULL, 'en', NULL, 6);";
	mysql_query($query1);
	echo mysql_errno($link) . ": " . mysql_error($link). "\n";
	$session_id = mysql_insert_id();
	$query2 = "INSERT INTO flash_info (session_id, host_flash_version_type, host_flash_version_major, host_flash_version_minor, host_flash_version_revision, host_flash_version_build, host_flash_time_offset, host_flash_accessibility, host_flash_domain, host_flash_os) VALUES ("
		. $session_id . ", 'LNX', 9, 0, 124, 0, 420, false, 'localhost', 'Linux 2.6.20-17-generic');";
	mysql_query($query2);
	echo mysql_errno($link) . ": " . mysql_error($link). "\n";
	
	
	$query1 = "INSERT INTO sessions (message_version, sim_type, sim_project, sim_name, sim_major_version, sim_minor_version, sim_dev_version, sim_svn_revision, sim_locale_language, sim_locale_country, sim_sessions_since, sim_sessions_ever, sim_usage_type, sim_distribution_tag, sim_dev, sim_scenario, host_locale_language, host_locale_country, host_simplified_os) VALUES (1, 0, 'balloons', 'balloons', 1, 7, 0, 26532, 'en', 'US', 1, 25, NULL, NULL, true, 'standalone-jar', 'en', 'US', 2);";
	mysql_query($query1);
	echo mysql_errno($link) . ": " . mysql_error($link). "\n";
	$session_id = mysql_insert_id();
	$query2 = "INSERT INTO java_info (session_id, host_java_os_name, host_java_os_version, host_java_os_arch, host_java_vendor, host_java_version_major, host_java_version_minor, host_java_version_maintenance, host_java_webstart_version, host_java_timezone) VALUES ("
		. $session_id . ", 'Windows Vista', '6.0', 'x86', 'Sun Microsystems Inc.', 1, 6, 0, NULL, 'America/Denver');";
	mysql_query($query2);
	echo mysql_errno($link) . ": " . mysql_error($link). "\n";
*/

//echo date("YmdHis", time());

?>
<html>
<head>
<title>Test</title>
</head>
<body>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<div style="width: 40px; height: 100px; background-color: #000000; border: 1px solid black;">
	<div style="width: 5px; height: 100px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 95px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 90px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 85px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 80px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 75px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 70px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 65px; background-color: #FFFFFF; float: left;"></div>
</div>
</body>
</html>
