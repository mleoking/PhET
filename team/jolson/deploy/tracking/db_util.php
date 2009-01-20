<?php
	// connect to the statistics database
	// TODO: change this password, it has been on subversion
	function setup_mysql() {
		$link = mysql_connect("localhost", "www-data", "d3#r3m0nt$") or die(mysql_error());
		mysql_select_db("phet_stats_test_2") or die(mysql_error());
		return $link;
	}
	
	// used for every mysql query that needs to be made
	// useful for debugging and error catching
	function phet_mysql_query($query) {
		//echo "<p>" . $query . "</p>";
		
		// actually execute the query
		$result = mysql_query($query);
		
		//echo "<p>" . mysql_error() . "</p>";
		//$result | die();
		return $result;
	}
	
	// get the id value corresponding to a unique value. if it doesn't exist, create a new row
	//
	// IMPORTANT: $table_field_id should be the AUTO_INCREMENT field for using this function
	//
	// example: get_id_value('flash_os', 'id', 'name', 'Mac OS X');
	// this would check to see whether 'Mac OS X' existed in the flash_os table under name.
	// if it exists, it returns the value of 'id' for that row
	// if it does not exist, it is inserted, and the value of the auto_increment field (usually id) is returned
	function get_id_value($table_name, $table_field_id, $table_field_value, $table_value) {
		// POSSIBLE TODO: update with things similar to "INSERT INTO java_vendor (name) SELECT 'Not Sun' FROM java_vendor WHERE NOT EXISTS (SELECT id FROM java_vendor WHERE name = 'Not Sun');"
		
		// we cannot use "= NULL" since NULL != NULL, thus we need a separate check if our
		// value is NULL
		if($table_value != "NULL") {
			// selects all rows which have that value (should be just 1, but will not error if there are more)
			$query = "SELECT $table_field_id FROM $table_name WHERE $table_field_value = $table_value";
		} else {
			// selects all rows which have that value (should be just 1, but will not error if there are more)
			$query = "SELECT $table_field_id FROM $table_name WHERE $table_field_value IS NULL";
		}
		
		// execute the query
		$result = phet_mysql_query($query);
		
		// the number of rows that match the SELECT statement above
		// should be either 0 or 1, however this will work if more are selected
		// if 0, our value is not in the table
		$num_rows = mysql_num_rows($result);
		
		if($num_rows == 0) {
			// our value is not in the table, so we need to insert it
			$insert_query = "INSERT INTO $table_name ($table_field_value) VALUES ($table_value)";
			phet_mysql_query($insert_query);
			
			// return the value of the auto_increment field (should be ID).
			// this allows us to not execute another query
			return mysql_insert_id();
		} else {
			// our value is in the table. fetch the first row (should be the only row)
			$row = mysql_fetch_row($result);
			
			// return the ID
			return $row[0];
		}
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
	
	// return either a quoted string, or NULL if the value is one of the strings mapped to NULL
	function quote_null_if_none($value) {
		if($value == "none" || $value == "null" || $value == "undefined") {
			return "NULL";
		} else {
			return quo($value);
		}
	}
	
	// turn a table name and array of Field objects into an insert statement into that table
	function query_from_values($table_name, $values) {
		$query = "INSERT INTO $table_name (";
		$first = true;
		
		// build field names
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
		
		// build values
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
		$simDeployment,
		$simDistributionTag,
		$simDev,
		$hostLocaleLanguage,
		$hostLocaleCountry,
		$hostSimplifiedOS
	) {
		// get IDs from normalized tables
		$simProjectID = get_id_value("sim_project", "id", "name", quo($simProject));
		$simNameID = get_id_value("sim_name", "id", "name", quo($simName));
		$simDeploymentID = get_id_value("deployment", "id", "name", quote_null_if_none($simDeployment));
		$simDistributionTagID = get_id_value("distribution_tag", "id", "name", quote_null_if_none($simDistributionTag));
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
			new Field('sim_deployment', $simDeploymentID),
			new Field('sim_distribution_tag', $simDistributionTagID),
			new Field('sim_dev', $simDev),
			new Field('host_locale_language', quo($hostLocaleLanguage)),
			new Field('host_locale_country', quote_null_if_none($hostLocaleCountry)),
			new Field('host_simplified_os', $hostSimplifiedOSID),
		);
		
		// build query from values to be inserted
		$query = query_from_values("session", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
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
		// get IDs from normalized tables
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
		
		// build query from values to be inserted
		$query = query_from_values("flash_info", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
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
		// get IDs from normalized tables
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
		
		// build query from values to be inserted
		$query = query_from_values("java_info", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
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
		$simDeployment,
		$simDistributionTag,
		$simDev,
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
		// this is a Flash sim
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
			$simDeployment,
			$simDistributionTag,
			$simDev,
			$hostLocaleLanguage,
			$hostLocaleCountry,
			$hostSimplifiedOS
		);
		
		// insert into flash_into with the session ID from above
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
		$simDeployment,
		$simDistributionTag,
		$simDev,
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
		// this is a Java sim
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
			$simDeployment,
			$simDistributionTag,
			$simDev,
			$hostLocaleLanguage,
			$hostLocaleCountry,
			$hostSimplifiedOS
		);
		
		// insert into java_into with the session ID from above
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
		// we need to find out whether an entry exists for this particular file creation time
		$query = "SELECT user_preferences_file_creation_time FROM user WHERE user_preferences_file_creation_time = " . $userPreferencesFileCreationTime . ";";
		$result = phet_mysql_query($query);
		
		// number of rows that match the above query. should be 1 if the user has been seen before,
		// and 0 if they haven't been seen
		$num_rows = mysql_num_rows($result);
		
		if($num_rows === 0) {
			// first time this user is seen
			
			// values to be inserted
			$values = array(
				new Field('user_preferences_file_creation_time', $userPreferencesFileCreationTime),
				new Field('user_total_sessions', $userTotalSessions),
				new Field('first_seen_month', quo(date("Y-m-01", time()))), // current year and month
				new Field('last_seen_month', quo(date("Y-m-01", time()))) // current year and month
			);
			$insert_query = query_from_values("user", $values);
			phet_mysql_query($insert_query);
		} else {
			// user already in table, update values
			
			// update total sessions
			$update_query = "UPDATE user SET user_total_sessions = $userTotalSessions WHERE user_preferences_file_creation_time = $userPreferencesFileCreationTime";
			phet_mysql_query($update_query);
			
			// update last_seen_month with current year and month
			$update_query = "UPDATE user SET last_seen_month = " . quo(date("Y-m-01", time())) . " WHERE user_preferences_file_creation_time = $userPreferencesFileCreationTime";
			phet_mysql_query($update_query);
		}
	}
?>