<?php
	
	// receives XML statistics messages, and inserts them into the database
	// can also record raw and parsed information for debugging purposes
	// (DISABLE RAW LOGGING FOR PRODUCTION)
	
	include("db-stats.php");
	
	// connect to statistics database
	$link = setup_mysql();
	
	// whether or not logging the messages is enabled.
	// TODO: DO NOT ENABLE FOR LIVE VERSION
	$raw_logging = true;
	$raw_logging_verbose = false;
	
	// load the xml from postdata
	$raw_post_data = $HTTP_RAW_POST_DATA;
	$xml = simplexml_load_string($raw_post_data);
	
	function fail_me($str) {
		global $raw_post_data;
		print "<error-message>{$str}</error-message><success>false</success></statistics-result></xml>";
		message_error($raw_post_data, $str);
		exit;
	}
	
	function warn_me($str) {
		print "<warning-message>{$str}</warning-message>";
	}
	
	print "<xml><statistics-result>";
	
	
	
	
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
	
	// error checking for an integer field
	//
	// all digits => okay
	// null | "null" | NaN => NULL
	// anything else causes failure
	function int_decode($field) {
		global $xml;
		
		$raw = $xml[$field];
		
		// turn %20 => ' ', etc.
		$decoded = urldecode($raw);
		
		if($raw === null) {
			warn_me("field {$field} was not specified, replacing with null");
			if($field == "sim_version_timestamp") {
				return "0";
				warn_me("sim_version_timestamp cannot be null, replacing with '0'");
			}
			return "NULL";
		}
		
		if($decoded === "null" || $decoded === "NaN") {
			if($field == "sim_version_timestamp") {
				return "0";
				warn_me("sim_version_timestamp cannot be {$decoded}, replacing with '0'");
			}
			return "NULL";
		}
		
		if(ctype_digit($decoded)) {
			// is a numeric string, this is OK
			return $decoded;
		} else {
			fail_me("value for {$field} ({$decoded}) is non-null and non-numeric");
		}
	}
	
	// brief error checking, used for Flash fields. Does not pull out of $xml, no null checks
	function int_verify($value, $name) {
		if(ctype_digit($value)) {
			// string is numeric
			return $value;
		} else {
			fail_me("field {$name} ({$value}) failed to parse as a number");
		}
	}
	
	// error checking for a string field
	//
	// null | "null" => NULL
	// anything else is returned as is
	function string_decode($field) {
		global $xml;
		
		$raw = $xml[$field];
		
		// turn %20 => ' ', etc.
		$decoded = urldecode($raw);
		
		if($raw === null) {
			warn_me("field {$field} was not specified, replacing with null");
			return "NULL";
		}
		
		if($decoded === "null") {
			return "NULL";
		}
		
		return $decoded;
	}
	
	// error checking for a boolean field
	//
	// null | "null" => NULL
	// "1" | "0" | "true" | "false" => true | false
	// everything else will raise an error
	function bool_decode($field) {
		global $xml;
		
		// turn %20 => ' ', etc.
		$decoded = urldecode($xml[$field]);
		
		if($decoded === null) {
			warn_me("boolean field {$field} was not specified, replacing with null");
			return "NULL";
		}
		
		if($decoded === "null") {
			return "NULL";
		}
		
		if(
			$decoded === "true"
			|| $decoded === "false"
			|| $decoded === "0"
			|| $decoded === "1"
		) {
			// we are OK, this is a good boolean
			return $decoded;
		}
		
		fail_me("field {$field} value {$decoded} is not boolean");
	}
	
	if($xml["sim_type"] == "flash") {
	    // message from a flash simulation
		
		if($xml["message_version"] == "0") {
			
			// extract flash version information (version is something like "LNX 9,0,124,0")
			$version_string = urldecode($xml["host_flash_version"]);
			
			// find position of first space
			$space_position = stripos($version_string, " ");
			if($space_position === false || $space_position == 0) { fail_me("Bad Flash version information"); }
			
			// extract everything up until the first space ("LNX")
			$version_left = substr($version_string, 0, $space_position);
			
			// extract everything after the first space ("9,0,124,0")
			$version_right = substr($version_string, $space_position + 1);
			if(empty($version_right)) { fail_me("Bad Flash version information"); }
			
			// split right side on commas (array("9", "0", "124", "0"))
			$version_numbers = explode(",", $version_right);
			if(count($version_numbers) != 4) { fail_me("Bad Flash version information"); }
			
			$sessionID = insert_flash_message(
				array (
					"message_version" => 1,
					"sim_project" => string_decode("sim_project"),
					"sim_name" => string_decode("sim_name"),
					"sim_major_version" => int_decode("sim_major_version"),
					"sim_minor_version" => int_decode("sim_minor_version"),
					"sim_dev_version" => int_decode("sim_dev_version"),
					"sim_svn_revision" => int_decode("sim_svn_revision"),
					"sim_version_timestamp" => int_decode("sim_version_timestamp"),
					"sim_locale_language" => string_decode("sim_locale_language"),
					"sim_locale_country" => string_decode("sim_locale_country"),
					"sim_sessions_since" => int_decode("sim_sessions_since"),
					"sim_total_sessions" => int_decode("sim_total_sessions"),
					"sim_deployment" => string_decode("sim_deployment"),
					"sim_distribution_tag" => string_decode("sim_distribution_tag"),
					"sim_dev" => bool_decode("sim_dev"),
					"host_locale_language" => string_decode("host_locale_language"),
					"host_locale_country" => "NULL",
					"host_flash_version_type" => $version_left,
					"host_flash_version_major" => int_verify($version_numbers[0], "host_flash_version_major"),
					"host_flash_version_minor" => int_verify($version_numbers[1], "host_flash_version_minor"),
					"host_flash_version_revision" => int_verify($version_numbers[2], "host_flash_version_revision"),
					"host_flash_version_build" => int_verify($version_numbers[3], "host_flash_version_build"),
					"host_flash_time_offset" => string_decode("host_flash_time_offset"),
					"host_flash_accessibility" => string_decode("host_flash_accessibility"),
					"host_flash_domain" => string_decode("host_flash_domain"),
					"host_flash_os" => string_decode("host_flash_os")
				)
			);
			
			if(empty($sessionID)) {
				fail_me("flash insertion error");
			}
			
			// create/update entry in user database
			$userMessage = update_user(
				int_decode("user_preference_file_creation_time"),
				int_decode("user_installation_timestamp"),
				int_decode("user_total_sessions")
			);
			
			if(!empty($userMessage)) {
				fail_me($userMessage);
			}
			
		} else {
			fail_me("message_version was not accepted");
		}
		
	} else if($xml["sim_type"] == "java") {
		
		if($xml["message_version"] == "0") {
			
			$sessionID = insert_java_message(
				array (
					"message_version" => 1,
					"sim_project" => string_decode("sim_project"),
					"sim_name" => string_decode("sim_name"),
					"sim_major_version" => int_decode("sim_major_version"),
					"sim_minor_version" => int_decode("sim_minor_version"),
					"sim_dev_version" => int_decode("sim_dev_version"),
					"sim_svn_revision" => int_decode("sim_svn_revision"),
					"sim_version_timestamp" => int_decode("sim_version_timestamp"),
					"sim_locale_language" => string_decode("sim_locale_language"),
					"sim_locale_country" => string_decode("sim_locale_country"),
					"sim_sessions_since" => int_decode("sim_sessions_since"),
					"sim_total_sessions" => int_decode("sim_total_sessions"),
					"sim_deployment" => string_decode("sim_deployment"),
					"sim_distribution_tag" => string_decode("sim_distribution_tag"),
					"sim_dev" => bool_decode("sim_dev"),
					"host_locale_language" => string_decode("host_locale_language"),
					"host_locale_country" => string_decode("host_locale_country"),
					"host_java_os_name" => string_decode("host_os_name"),
					"host_java_os_version" => string_decode("host_os_version"),
					"host_java_os_arch" => string_decode("host_os_arch"),
					"host_java_vendor" => string_decode("host_java_vendor"),
					"host_java_version_major" => int_decode("host_java_version_major"),
					"host_java_version_minor" => int_decode("host_java_version_minor"),
					"host_java_version_maintenance" => int_decode("host_java_version_maintenance"),
					"host_java_webstart_version" => string_decode("host_java_webstart_version"),
					"host_java_timezone" => string_decode("host_java_timezone")
				)
			);
			
			if(empty($sessionID)) {
				fail_me("java insertion error");
			}
			
			// create/update entry in user database
			$userMessage = update_user(
				int_decode("user_preference_file_creation_time"),
				int_decode("user_installation_timestamp"),
				int_decode("user_total_sessions")
			);
			
			if(!empty($userMessage)) {
				fail_me($userMessage);
			}
			
		} else {
			fail_me("message_version was not accepted");
		}
		
	} else {
		fail_me("sim_type was not java or flash");
	}
	
	print "<success>true</success>";
	
	print "</statistics-result></xml>";
	
?>