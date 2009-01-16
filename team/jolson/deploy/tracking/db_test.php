<?php
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
	
?>
<p>
This is a test of your local emergency database system.
		
		host_java_os_name=Windows Vista
		host_java_os_version=6.0
		host_java_os_arch=x86
		host_java_vendor=Sun Microsystems Inc.
		host_java_version_major=1
		host_java_version_minor=6
		host_java_version_maintenance=0
		host_java_webstart_version=null
		host_java_timezone=America/Denver

	message_type=session
	message_version=1
	sim_type=java
	sim_project=balloons
	sim_name=balloons
	sim_sessions=14
	sim_major_version=1
	sim_minor_version=07
	sim_dev_version=00
	sim_svn_revision=26532
	sim_distribution_id=general
	sim_locale_language=en
	sim_locale_country=US
	sim_scenario=standalone-jar
	sim_dev=true
	host_os_name=Windows Vista
	host_os_version=6.0
	host_os_arch=x86
	host_java_vendor=Sun Microsystems Inc.
	host_java_version_major=1
	host_java_version_minor=6
	host_java_version_maintenance=0
	host_java_webstart_version=null
	host_locale_language=en
	host_locale_country=US
	host_timezone=America/Denver
	user_preference_file_creation_time=1229711605402
	user_total_sessions=394
	debug_sim_version=1.07.00 (26532)
	debug_host_java_version=1.6.0_10-rc2-b31
</p>