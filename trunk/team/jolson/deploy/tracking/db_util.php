
<?php
	// connect to the statistics database
	function setup_mysql() {
		$link = mysql_connect("localhost", "www-data", "d3#r3m0nt$") or die(mysql_error());
		mysql_select_db("phet_stats_test_1") or die(mysql_error());
		return $link;
	}
	
	function phet_mysql_query($query) {
echo "<p>" . $query . "</p>";
		$result = mysql_query($query);
		
echo "<p>" . mysql_error() . "</p>";
		//$result | die();
		return $result;
	}
	
	// get the id value corresponding to a unique value. if it doesn't exist, create a new row
	function get_id_value($table_name, $table_field_id, $table_field_value, $table_value) {
		if($table_value != "NULL") {
			$query = "SELECT $table_field_id FROM $table_name WHERE $table_field_value = $table_value";
		} else {
			$query = "SELECT $table_field_id FROM $table_name WHERE $table_field_value IS NULL";
		}
		$result = phet_mysql_query($query);
		$num_rows = mysql_num_rows($result);
		if($num_rows == 0) {
			$insert_query = "INSERT INTO $table_name ($table_field_value) VALUES ($table_value)";
			phet_mysql_query($insert_query);
			return mysql_insert_id();
		}
		$row = mysql_fetch_row($result);
		return $row[0];
	}
	
	// surround a string with quotes
	function quo($str) {
		return "'" . $str . "'";
	}
	
	// used for inserting into tables. stores the field name, and value to be inserted
	class Field {
		public $field = 'default_field';
		public $value = 'default_value';
		public function __construct($field, $value) {
			$this->field = $field;
			$this->value = $value;
		}
	}
	
	function quote_null_if_none($value) {
		if($value == "none" || $value == "null" || $value == "undefined") {
			return "NULL";
		} else {
			return quo($value);
		}
	}
	
	// turn a table name and array of Field objects into an insert statement
	function query_from_values($table_name, $values) {
		$query = "INSERT INTO $table_name (";
		$first = true;
		foreach($values as $f) {
			if($first) {
				$first = false;
				$query .= $f->field;
			} else {
				$query .= ", " . $f->field;
			}
		}
		$query .= ") VALUES (";
		$first = true;
		foreach($values as $f) {
			if($first) {
				$first = false;
				$query .= $f->value;
			} else {
				$query .= ", " . $f->value;
			}
		}
		$query .= ");";
		return $query;
	}
	
	// insert data into the session table
	function insert_session(
		$messageVersion,
		$simType,
		$simProject,
		$simName,
		$simMajorVersion,
		$simMinorVersion,
		$simDevVersion,
		$simSvnRevision,
		$simLocaleLanguage,
		$simLocaleCountry,
		$simSessionsSince,
		$simSessionsEver,
		$simUsageType,
		$simDistributionTag,
		$simDev,
		$simScenario,
		$hostLocaleLanguage,
		$hostLocaleCountry,
		$hostSimplifiedOS
	) {
		$simProjectID = get_id_value("sim_project", "id", "name", quo($simProject));
		$simNameID = get_id_value("sim_name", "id", "name", quo($simName));
		$simUsageTypeID = get_id_value("usage_type", "id", "name", quote_null_if_none($simUsageType));
		$simDistributionTagID = get_id_value("distribution_tag", "id", "name", quote_null_if_none($simDistributionTag));
		$simScenarioID = get_id_value("scenario", "id", "name", quote_null_if_none($simScenario));
		$hostSimplifiedOSID = get_id_value("simplified_os", "id", "name", quo($hostSimplifiedOS));
		
		$values = array(
			new Field('message_version', $messageVersion),
			new Field('sim_type', $simType),
			new Field('sim_project', $simProjectID),
			new Field('sim_name', $simNameID),
			new Field('sim_major_version', $simMajorVersion),
			new Field('sim_minor_version', $simMinorVersion),
			new Field('sim_dev_version', $simDevVersion),
			new Field('sim_svn_revision', $simSvnRevision),
			new Field('sim_locale_language', quo($simLocaleLanguage)),
			new Field('sim_locale_country', quote_null_if_none($simLocaleCountry)),
			new Field('sim_sessions_since', $simSessionsSince),
			new Field('sim_sessions_ever', $simSessionsEver),
			new Field('sim_usage_type', $simUsageTypeID),
			new Field('sim_distribution_tag', $simDistributionTagID),
			new Field('sim_dev', $simDev),
			new Field('sim_scenario', $simScenarioID),
			new Field('host_locale_language', quo($hostLocaleLanguage)),
			new Field('host_locale_country', quote_null_if_none($hostLocaleCountry)),
			new Field('host_simplified_os', $hostSimplifiedOSID),
		);
		
		$query = query_from_values("session", $values);
		
		phet_mysql_query($query);
		
		return mysql_insert_id();
	}
	
	// insert data into the flash_info table
	function insert_flash_info(
		$sessionID,
		$hostFlashVersionType,
		$hostFlashVersionMajor,
		$hostFlashVersionMinor,
		$hostFlashVersionRevision,
		$hostFlashVersionBuild,
		$hostFlashTimeOffset,
		$hostFlashAccessibility,
		$hostFlashDomain,
		$hostFlashOS
	) {
		$hostFlashVersionTypeID = get_id_value("flash_version_type", "id", "name", quo($hostFlashVersionType));
		$hostFlashDomainID = get_id_value("flash_domain", "id", "name", quo($hostFlashDomain));
		$hostFlashOSID = get_id_value("flash_os", "id", "name", quo($hostFlashOS));
		
		$values = array(
			new Field('session_id', $sessionID),
			new Field('host_flash_version_type', $hostFlashVersionTypeID),
			new Field('host_flash_version_major', $hostFlashVersionMajor),
			new Field('host_flash_version_minor', $hostFlashVersionMinor),
			new Field('host_flash_version_revision', $hostFlashVersionRevision),
			new Field('host_flash_version_build', $hostFlashVersionBuild),
			new Field('host_flash_time_offset', $hostFlashTimeOffset),
			new Field('host_flash_accessibility', $hostFlashAccessibility),
			new Field('host_flash_domain', $hostFlashDomainID),
			new Field('host_flash_os', $hostFlashOSID)
		);
		
		$query = query_from_values("flash_info", $values);
		
		phet_mysql_query($query);
		
		return mysql_insert_id();
	}
	
	// insert data into the java_info table
	function insert_java_info(
		$sessionID,
		$hostJavaOSName,
		$hostJavaOSVersion,
		$hostJavaOSArch,
		$hostJavaVendor,
		$hostJavaVersionMajor,
		$hostJavaVersionMinor,
		$hostJavaVersionMaintenance,
		$hostJavaWebstartVersion,
		$hostJavaTimezone
	) {
		$hostJavaOSNameID = get_id_value("java_os_name", "id", "name", quo($hostJavaOSName));
		$hostJavaOSVersionID = get_id_value("java_os_version", "id", "name", quo($hostJavaOSVersion));
		$hostJavaOSArchID = get_id_value("java_os_arch", "id", "name", quo($hostJavaOSArch));
		$hostJavaVendorID = get_id_value("java_vendor", "id", "name", quo($hostJavaVendor));
		$hostJavaWebstartVersionID = get_id_value("java_webstart_version", "id", "name", quote_null_if_none($hostJavaWebstartVersion));
		$hostJavaTimezoneID = get_id_value("java_timezone", "id", "name", quo($hostJavaTimezone));
		
		$values = array(
			new Field('session_id', $sessionID),
			new Field('host_java_os_name', $hostJavaOSNameID),
			new Field('host_java_os_version', $hostJavaOSVersionID),
			new Field('host_java_os_arch', $hostJavaOSArchID),
			new Field('host_java_vendor', $hostJavaVendorID),
			new Field('host_java_version_major', $hostJavaVersionMajor),
			new Field('host_java_version_minor', $hostJavaVersionMinor),
			new Field('host_java_version_maintenance', $hostJavaVersionMaintenance),
			new Field('host_java_webstart_version', $hostJavaWebstartVersionID),
			new Field('host_java_timezone', $hostJavaTimezoneID)
		);
		
		$query = query_from_values("java_info", $values);
		
		phet_mysql_query($query);
		
		return mysql_insert_id();
	}
	
	// insert an entire flash message
	function insert_flash_message(
		$messageVersion,
		$simProject,
		$simName,
		$simMajorVersion,
		$simMinorVersion,
		$simDevVersion,
		$simSvnRevision,
		$simLocaleLanguage,
		$simLocaleCountry,
		$simSessionsSince,
		$simSessionsEver,
		$simUsageType,
		$simDistributionTag,
		$simDev,
		$simScenario,
		$hostLocaleLanguage,
		$hostLocaleCountry,
		$hostFlashVersionType,
		$hostFlashVersionMajor,
		$hostFlashVersionMinor,
		$hostFlashVersionRevision,
		$hostFlashVersionBuild,
		$hostFlashTimeOffset,
		$hostFlashAccessibility,
		$hostFlashDomain,
		$hostFlashOS
	) {
		$simType = 1;
		
		// calculate hostSimplifiedOS
		$hostSimplifiedOS = "Unknown";
		if($hostFlashVersionType == 'WIN') {
			$hostSimplifiedOS = "Windows - General";
			if(stripos($hostFlashOS, 'Vista') !== false) {
				$hostSimplifiedOS = "Windows - Vista";
			} else if(stripos($hostFlashOS, 'XP') !== false) {
				$hostSimplifiedOS = "Windows - XP";
			}
		} else if($hostFlashVersionType == 'MAC') {
			$hostSimplifiedOS = "Mac - General";
		} else if($hostFlashVersionType == 'LNX') {
			$hostSimplifiedOS = "Linux - General";
		} else if($hostFlashVersionType == 'UNIX') {
			$hostSimplifiedOS = "Unix - General";
		}
		
		// insert session
		$sessionID = insert_session(
			$messageVersion,
			$simType,
			$simProject,
			$simName,
			$simMajorVersion,
			$simMinorVersion,
			$simDevVersion,
			$simSvnRevision,
			$simLocaleLanguage,
			$simLocaleCountry,
			$simSessionsSince,
			$simSessionsEver,
			$simUsageType,
			$simDistributionTag,
			$simDev,
			$simScenario,
			$hostLocaleLanguage,
			$hostLocaleCountry,
			$hostSimplifiedOS
		);
		insert_flash_info(
			$sessionID,
			$hostFlashVersionType,
			$hostFlashVersionMajor,
			$hostFlashVersionMinor,
			$hostFlashVersionRevision,
			$hostFlashVersionBuild,
			$hostFlashTimeOffset,
			$hostFlashAccessibility,
			$hostFlashDomain,
			$hostFlashOS
		);
		
		return $sessionID;
	}
	
	// insert an entire java message
	function insert_java_message(
		$messageVersion,
		$simProject,
		$simName,
		$simMajorVersion,
		$simMinorVersion,
		$simDevVersion,
		$simSvnRevision,
		$simLocaleLanguage,
		$simLocaleCountry,
		$simSessionsSince,
		$simSessionsEver,
		$simUsageType,
		$simDistributionTag,
		$simDev,
		$simScenario,
		$hostLocaleLanguage,
		$hostLocaleCountry,
		$hostJavaOSName,
		$hostJavaOSVersion,
		$hostJavaOSArch,
		$hostJavaVendor,
		$hostJavaVersionMajor,
		$hostJavaVersionMinor,
		$hostJavaVersionMaintenance,
		$hostJavaWebstartVersion,
		$hostJavaTimezone
	) {
		$simType = 0;
		
		// calculate hostSimplifiedOS
		$hostSimplifiedOS = "Unknown";
		if(stripos($hostJavaOSName, 'Windows') !== false) {
			$hostSimplifiedOS = "Windows - General";
			if(stripos($hostJavaOSName, 'Vista') !== false) {
				$hostSimplifiedOS = "Windows - Vista";
			} else if(stripos($hostJavaOSName, 'XP') !== false) {
				$hostSimplifiedOS = "Windows - XP";
			}
		} else if(stripos($hostJavaOSName, 'Mac') !== false) {
			$hostSimplifiedOS = "Mac - General";
		} else if(stripos($hostJavaOSName, 'Linux') !== false) {
			$hostSimplifiedOS = "Linux - General";
		} else if(stripos($hostJavaOSName, 'Unix') !== false) {
			$hostSimplifiedOS = "Unix - General";
		}
		
		// insert session
		$sessionID = insert_session(
			$messageVersion,
			$simType,
			$simProject,
			$simName,
			$simMajorVersion,
			$simMinorVersion,
			$simDevVersion,
			$simSvnRevision,
			$simLocaleLanguage,
			$simLocaleCountry,
			$simSessionsSince,
			$simSessionsEver,
			$simUsageType,
			$simDistributionTag,
			$simDev,
			$simScenario,
			$hostLocaleLanguage,
			$hostLocaleCountry,
			$hostSimplifiedOS
		);
		insert_java_info(
			$sessionID,
			$hostJavaOSName,
			$hostJavaOSVersion,
			$hostJavaOSArch,
			$hostJavaVendor,
			$hostJavaVersionMajor,
			$hostJavaVersionMinor,
			$hostJavaVersionMaintenance,
			$hostJavaWebstartVersion,
			$hostJavaTimezone
		);
		
		return $sessionID;
	}
	
	// insert/update data for the user table
	function update_user(
		$userPreferencesFileCreationTime,
		$userTotalSessions
	) {
		$query = "SELECT user_preferences_file_creation_time FROM user WHERE user_preferences_file_creation_time = " . $userPreferencesFileCreationTime . ";";
		$result = phet_mysql_query($query);
		$num_rows = mysql_num_rows($result);
		if($num_rows === 0) {
			// first time this user is seen
			$values = array(
				new Field('user_preferences_file_creation_time', $userPreferencesFileCreationTime),
				new Field('user_total_sessions', $userTotalSessions),
				new Field('first_seen_month', quo(date("Y-m-01", time()))),
				new Field('last_seen_month', quo(date("Y-m-01", time())))
			);
			$insert_query = query_from_values("user", $values);
			phet_mysql_query($insert_query);
		} else {
			// user already in table, update values
			$update_query = "UPDATE user SET user_total_sessions = $userTotalSessions WHERE user_preferences_file_creation_time = $userPreferencesFileCreationTime";
			phet_mysql_query($update_query);
			$update_query = "UPDATE user SET last_seen_month = " . quo(date("Y-m-01", time())) . " WHERE user_preferences_file_creation_time = $userPreferencesFileCreationTime";
			phet_mysql_query($update_query);
		}
	}
?>

