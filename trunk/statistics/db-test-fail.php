<?php
	
	// script to display the raw values in the database
	// not fun if you view this with 4 million entries or so (over 8 million rows)

    // require authentication to display
	include("db-auth.php");

	// necessary functions for database interaction
	include("db-stats.php");

	// connect to the database
	$link = setup_mysql();
	
	$sessionID = insert_flash_message(
		array (
			"message_version" => 1,
			"sim_project" => "failer",
			"sim_name" => "failer",
			"sim_major_version" => "1",
			"sim_minor_version" => "00",
			"sim_dev_version" => "01",
			"sim_svn_revision" => "28000",
			"sim_version_timestamp" => "1233818313",
			"sim_locale_language" => "en",
			"sim_locale_country" => "null",
			"sim_sessions_since" => "1",
			"sim_total_sessions" => "17",
			"sim_deployment" => "standalone-jar",
			"sim_distribution_tag" => "null",
			"sim_dev" => "true",
			"host_locale_language" => "en",
			"host_locale_country" => "null",
			"host_flash_version_type" => "LNX",
			"host_flash_version_major" => "totally_not_a_number",
			"host_flash_version_minor" => "5",
			"host_flash_version_revision" => "6",
			"host_flash_version_build" => "7",
			"host_flash_time_offset" => "420",
			"host_flash_accessibility" => "false",
			"host_flash_domain" => "localhost",
			"host_flash_os" => "Linux something-or-other"
		)
	);
	
	$sessionID = insert_flash_message(
		array (
			"message_version" => 1,
			"sim_project" => "okay-sim",
			"sim_name" => "okay-sim",
			"sim_major_version" => "1",
			"sim_minor_version" => "00",
			"sim_dev_version" => "01",
			"sim_svn_revision" => "28000",
			"sim_version_timestamp" => "1233818313",
			"sim_locale_language" => "en",
			"sim_locale_country" => "null",
			"sim_sessions_since" => "1",
			"sim_total_sessions" => "17",
			"sim_deployment" => "standalone-jar",
			"sim_distribution_tag" => "null",
			"sim_dev" => "true",
			"host_locale_language" => "en",
			"host_locale_country" => "null",
			"host_flash_version_type" => "LNX",
			"host_flash_version_major" => "9",
			"host_flash_version_minor" => "5",
			"host_flash_version_revision" => "6",
			"host_flash_version_build" => "7",
			"host_flash_time_offset" => "420",
			"host_flash_accessibility" => "false",
			"host_flash_domain" => "localhost",
			"host_flash_os" => "Linux something-or-other"
		)
	);
?>