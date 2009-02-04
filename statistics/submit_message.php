<?php
	
	// receives XML statistics messages, and inserts them into the database
	// can also record raw and parsed information for debugging purposes
	// (DISABLE RAW LOGGING FOR PRODUCTION)
	
	include("db-stats.php");
	
	// whether or not logging the messages is enabled.
	// TODO: DO NOT ENABLE FOR LIVE VERSION
	$raw_logging = true;
	$raw_logging_verbose = true;
	
	
	$success = true;
	$fail_reason = "";
	
	print "<html>";
	
	// load the xml from postdata
	$xml = simplexml_load_string($HTTP_RAW_POST_DATA);
	
	
	// if we want to track the raw messages that are coming through, this will be used
	if($raw_logging) {
		$rawfile = fopen('raw-log.txt', 'a');
		fwrite($rawfile, $HTTP_RAW_POST_DATA . "\n");
		fclose($rawfile);
		
		$filename = "parsed-log.txt";
		$file = fopen($filename, 'a');
		$str = date('y/m/d h:i:s A') . ":\n";
		
		// for each attribute, print out its key and value
		foreach($xml->attributes() as $key => $value) {
			$str .= "\t" . $key . "=" . urldecode($value) . "\n";
			
			// for message debugging
			if($raw_logging_verbose) {
				$str .= "\t\traw = \"{$value}\"\n";
				if(urldecode($value) === null) { $str .= "\t\tnull\n"; }
				if(urldecode($value) === false) { $str .= "\t\tfalse\n"; }
				$decoded = urldecode($value);
				$is_empty = empty($decoded);
				if($is_empty) {
					$str .= "\t\tempty\n";
				}
			}
		}
		
		$str .= "\n";
		
		fwrite($file, $str);
		fclose($file);
	}
	
	
	
	if($xml["sim_type"] == "flash") {
	    // message from a flash simulation
		
		if($xml["message_version"] == "1") {
		    // connect to mysql
			$link = setup_mysql();
			
			// create/update entry in user database
			update_user(
				urldecode($xml["user_preference_file_creation_time"]),
				urldecode($xml["user_installation_timestamp"]),
				urldecode($xml["user_total_sessions"])
			);
			
			// extract flash version information (version is something like "LNX 9,0,124,0")
			$version_string = urldecode($xml["host_flash_version"]);
			
			// find position of first space
			$space_position = stripos($version_string, " ");
			if($space_position === false || $space_position == 0) { die("Error: 451524"); }
			
			// extract everything up until the first space ("LNX")
			$version_left = substr($version_string, 0, $space_position);
			
			// extract everything after the first space ("9,0,124,0")
			$version_right = substr($version_string, $space_position + 1);
			if(empty($version_right)) { die("Error: 725378"); }
			
			// split right side on commas (array("9", "0", "124", "0"))
			$version_numbers = explode(",", $version_right);
			if(count($version_numbers) != 4) { die("Error: 981403"); }
			
			$sessionID = insert_flash_message(
				array (
					"message_version" => 1,
					"sim_project" => urldecode($xml["sim_project"]),
					"sim_name" => urldecode($xml["sim_name"]),
					"sim_major_version" => urldecode($xml["sim_major_version"]),
					"sim_minor_version" => urldecode($xml["sim_minor_version"]),
					"sim_dev_version" => urldecode($xml["sim_dev_version"]),
					"sim_svn_revision" => urldecode($xml["sim_svn_revision"]),
					"sim_version_timestamp" => urldecode($xml["sim_version_timestamp"]),
					"sim_locale_language" => urldecode($xml["sim_locale_language"]),
					"sim_locale_country" => urldecode($xml["sim_locale_country"]),
					"sim_sessions_since" => urldecode($xml["sim_sessions_since"]),
					"sim_total_sessions" => urldecode($xml["sim_total_sessions"]),
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
			
			if(empty($sessionID)) {
				$success = false;
				$fail_reason = "message error";
			}
			
		}
	} else if($xml["sim_type"] == "java") {
		
		if($xml["message_version"] == "1") {
		    // connect to mysql
			$link = setup_mysql();
			
			// create/update entry in user database
			update_user(
				urldecode($xml["user_preference_file_creation_time"]),
				urldecode($xml["user_installation_timestamp"]),
				urldecode($xml["user_total_sessions"])
			);
			
			$sessionID = insert_java_message(
				array (
					"message_version" => 1,
					"sim_project" => urldecode($xml["sim_project"]),
					"sim_name" => urldecode($xml["sim_name"]),
					"sim_major_version" => urldecode($xml["sim_major_version"]),
					"sim_minor_version" => urldecode($xml["sim_minor_version"]),
					"sim_dev_version" => urldecode($xml["sim_dev_version"]),
					"sim_svn_revision" => urldecode($xml["sim_svn_revision"]),
					"sim_version_timestamp" => urldecode($xml["sim_version_timestamp"]),
					"sim_locale_language" => urldecode($xml["sim_locale_language"]),
					"sim_locale_country" => urldecode($xml["sim_locale_country"]),
					"sim_sessions_since" => urldecode($xml["sim_sessions_since"]),
					"sim_total_sessions" => urldecode($xml["sim_total_sessions"]),
					"sim_deployment" => urldecode($xml["sim_deployment"]),
					"sim_distribution_tag" => urldecode($xml["sim_distribution_tag"]),
					"sim_dev" => urldecode($xml["sim_dev"]),
					"host_locale_language" => urldecode($xml["host_locale_language"]),
					"host_locale_country" => urldecode($xml["host_locale_country"]),
					"host_java_os_name" => urldecode($xml["host_os_name"]),
					"host_java_os_version" => urldecode($xml["host_os_version"]),
					"host_java_os_arch" => urldecode($xml["host_os_arch"]),
					"host_java_vendor" => urldecode($xml["host_java_vendor"]),
					"host_java_version_major" => urldecode($xml["host_java_version_major"]),
					"host_java_version_minor" => urldecode($xml["host_java_version_minor"]),
					"host_java_version_maintenance" => urldecode($xml["host_java_version_maintenance"]),
					"host_java_webstart_version" => urldecode($xml["host_java_webstart_version"]),
					"host_java_timezone" => urldecode($xml["host_java_timezone"])
				)
			);
			
			if(empty($sessionID)) {
				$success = false;
				$fail_reason = "message error";
			}
			
		}
		
	}
	
	if($success) {
		print "<p>Received Successfully</p>";
	} else {
		print "<p>Failure: {$fail_reason}</p>";
	}
	
	print "</html>";
	
?>