
<html><?php
	
	// receives XML tracking messages, and inserts them into the database
	// can also record raw and parsed information for debugging purposes
	// (DISABLE RAW LOGGING FOR PRODUCTION)
	
	include("db_util.php");
	
	// whether or not logging the messages is enabled. DO NOT ENABLE FOR LIVE VERSION
	$raw_tracking = false;
	
	
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
		
		$flashFields = array("message_type", "message_version", "user_preference_file_creation_time", "user_total_sessions", "sim_type", "sim_project", "sim_name", "sim_major_version", "sim_minor_version", "sim_dev_version", "sim_svn_revision", "sim_locale_language", "sim_locale_country", "sim_sessions_since", "sim_sessions_ever", "sim_usage_type", "sim_distribution_tag", "sim_dev", "host_flash_os", "host_flash_version", "host_locale_language", "host_flash_time_offset", "host_flash_accessibility", "host_flash_domain");
		if($xml["sim_type"] == "flash") {
			// dump it into tracking-log.txt
			foreach($flashFields as &$field) {
				$str .= "\t" . $field . "=" . urldecode($xml[$field]) . "\n";
			}
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
			$version_left = substr(urldecode($xml["host_flash_version"]), 0, stripos(urldecode($xml["host_flash_version"]), " "));
			$version_right = substr(urldecode($xml["host_flash_version"]), stripos(urldecode($xml["host_flash_version"]), " ") + 1);
			$version_numbers = explode(",", $version_right);
			
			// insert the flash message
			insert_flash_message(
				1, //$messageVersion,
				sanitize($xml, "sim_project"), //$simProject,
				sanitize($xml, "sim_name"), //$simName,
				sanitize($xml, "sim_major_version"), //$simMajorVersion,
				sanitize($xml, "sim_minor_version"), //$simMinorVersion,
				sanitize($xml, "sim_dev_version"), //$simDevVersion,
				sanitize($xml, "sim_svn_revision"), //$simSvnRevision,
				sanitize($xml, "sim_locale_language"), //$simLocaleLanguage,
				sanitize($xml, "sim_locale_country"), //$simLocaleCountry,
				sanitize($xml, "sim_sessions_since"), //$simSessionsSince,
				sanitize($xml, "sim_sessions_ever"), //$simSessionsEver,
				sanitize($xml, "sim_usage_type"), //$simUsageType,
				sanitize($xml, "sim_distribution_tag"), //$simDistributionTag,
				sanitize($xml, "sim_dev"), //$simDev,
				"none", //$simScenario,
				sanitize($xml, "host_locale_language"), //$hostLocaleLanguage,
				"none", //$hostLocaleCountry,
				mysql_real_escape_string($version_left), //$hostFlashVersionType,
				mysql_real_escape_string($version_numbers[0]), //$hostFlashVersionMajor,
				mysql_real_escape_string($version_numbers[1]), //$hostFlashVersionMinor,
				mysql_real_escape_string($version_numbers[2]), //$hostFlashVersionRevision,
				mysql_real_escape_string($version_numbers[3]), //$hostFlashVersionBuild,
				sanitize($xml, "host_flash_time_offset"), //$hostFlashTimeOffset,
				sanitize($xml, "host_flash_accessibility"), //$hostFlashAccessibility,
				sanitize($xml, "host_flash_domain"), //$hostFlashDomain,
				sanitize($xml, "host_flash_os") //$hostFlashOS
			);
			
			
		}
	}
	
	echo "<p>Received Successfully</p>";
	
?>
</html>
