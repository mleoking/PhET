<?php
	
	// receives XML tracking messages, and inserts them into the database
	// can also record raw and parsed information for debugging purposes
	// (DISABLE RAW LOGGING FOR PRODUCTION)
	
	include("db-tracking.php");
	
	// whether or not logging the messages is enabled. DO NOT ENABLE FOR LIVE VERSION
	$raw_tracking = true;
	
	
	
	print "<html>";
	
	// load the xml from postdata
	$xml = simplexml_load_string($HTTP_RAW_POST_DATA);
	
	
	// if we want to track the raw messages that are coming through, this will be used
	if($raw_tracking) {
		$rawfile = fopen('raw-log.txt', 'a');
		fwrite($rawfile, $HTTP_RAW_POST_DATA . "\n");
		fclose($rawfile);
		
		$filename = "tracking-log.txt";
		$file = fopen($filename, 'a');
		$str = date('y/m/d h:i:s A') . ":\n";
		
		// for each attribute, print out its key and value
		foreach($xml->attributes() as $key => $value) {
			$str .= "\t" . $key . "=" . urldecode($value) . "\n";
		}
		
		$str .= "\n";
		
		fwrite($file, $str);
		fclose($file);
	}
	
	
	// extracts information from the XML, decodes it, and sanitizes it to prevent injection
	function sanitize($xml, $str) {
		return mysql_real_escape_string(urldecode($xml[$str]));
	}
	
	if($xml["sim_type"] == "flash") {
		
		if($xml["message_version"] == "1") {
			$link = setup_mysql();
			
			// create/update entry in user database
			update_user(
				sanitize($xml, "user_preference_file_creation_time"),
				sanitize($xml, "user_total_sessions")
			);
			
			// extract flash version information
			$version_string = urldecode($xml["host_flash_version"]);
			
			// extract everything up until the first space
			$version_left = substr($version_string, 0, stripos($version_string, " "));
			
			// extract everything after the first space
			$version_right = substr($version_string, stripos($version_string, " ") + 1);
			
			// split everything into commas (that was after the first space)
			$version_numbers = explode(",", $version_right);
			
			insert_flash_message(
				array (
					"message_version" => 1,
					"sim_project" => urldecode($xml["sim_project"]),
					"sim_name" => urldecode($xml["sim_name"]),
					"sim_major_version" => urldecode($xml["sim_major_version"]),
					"sim_minor_version" => urldecode($xml["sim_minor_version"]),
					"sim_dev_version" => urldecode($xml["sim_dev_version"]),
					"sim_svn_revision" => urldecode($xml["sim_svn_revision"]),
					"sim_locale_language" => urldecode($xml["sim_locale_language"]),
					"sim_locale_country" => urldecode($xml["sim_locale_country"]),
					"sim_sessions_since" => urldecode($xml["sim_sessions_since"]),
					"sim_sessions_ever" => urldecode($xml["sim_sessions_ever"]),
					"sim_deployment" => urldecode($xml["sim_deployment"]),
					"sim_distribution_tag" => urldecode($xml["sim_distribution_tag"]),
					"sim_dev" => urldecode($xml["sim_dev"]),
					"host_locale_language" => urldecode($xml["host_locale_language"]),
					"host_locale_country" => "none",
					"host_flash_version_type" => $version_left,
					"host_flash_version_major" => $version_numbers[0],
					"host_flash_version_minor" => $version_numbers[1],
					"host_flash_version_revision" => $version_numbers[2],
					"host_flash_version_build" => $version_numbers[3],
					"host_flash_time_offset" => urldecode($xml["host_flash_time_offset"]),
					"host_flash_accessibility" => urldecode($xml["host_flash_accessibility"]),
					"host_flash_domain" => urldecode($xml["host_flash_domain"]),
					"host_flash_os" => urldecode($xml["host_flash_os"])
				)
			);
			
		}
	} else if($xml["sim_type"] == "java") {
		
		if($xml["message_version"] == "1") {
			$link = setup_mysql();
			
			// create/update entry in user database
			update_user(
				sanitize($xml, "user_preference_file_creation_time"),
				sanitize($xml, "user_total_sessions")
			);
			
			insert_java_message(
				array (
					"message_version" => 1,
					"sim_project" => urldecode($xml["sim_project"]),
					"sim_name" => urldecode($xml["sim_name"]),
					"sim_major_version" => urldecode($xml["sim_major_version"]),
					"sim_minor_version" => urldecode($xml["sim_minor_version"]),
					"sim_dev_version" => urldecode($xml["sim_dev_version"]),
					"sim_svn_revision" => urldecode($xml["sim_svn_revision"]),
					"sim_locale_language" => urldecode($xml["sim_locale_language"]),
					"sim_locale_country" => urldecode($xml["sim_locale_country"]),
					"sim_sessions_since" => urldecode($xml["sim_sessions_since"]),
					"sim_sessions_ever" => urldecode($xml["sim_sessions_ever"]),
					"sim_deployment" => urldecode($xml["sim_deployment"]),
					"sim_distribution_tag" => urldecode($xml["sim_distribution_tag"]),
					"sim_dev" => urldecode($xml["sim_dev"]),
					"host_locale_language" => urldecode($xml["host_locale_language"]),
					"host_locale_country" => urldecode($xml["host_locale_country"]),
					"host_os_name" => urldecode($xml["host_os_name"]),
					"host_os_version" => urldecode($xml["host_os_version"]),
					"host_os_arch" => urldecode($xml["host_os_arch"]),
					"host_java_vendor" => urldecode($xml["host_java_vendor"]),
					"host_java_version_major" => urldecode($xml["host_java_version_major"]),
					"host_java_version_minor" => urldecode($xml["host_java_version_minor"]),
					"host_java_version_maintenance" => urldecode($xml["host_java_version_maintenance"]),
					"host_java_webstart_version" => urldecode($xml["host_java_webstart_version"]),
					"host_java_timezone" => urldecode($xml["host_java_timezone"])
				)
			);
			
		}
		
	}
	
	print "<p>Received Successfully</p>";
	
	print "</html>";
	
?>