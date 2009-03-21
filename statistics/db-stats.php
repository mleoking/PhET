<?php
	
	// code that is included for all statistics-database functionality
	// handles insertion of messages

	// include information needed to log into the database
	include("db-stats-login.php");
	include("db-revision.php");
	
	// define sim_type to correspond with website code
	$SIM_TYPE_JAVA = "java";
	$SIM_TYPE_FLASH = "flash";
	
	// TODO: uncomment for the live version:
	//error_reporting(0);
	//ini_set('display_errors', 0);
	
	// used for every mysql query that needs to be made
	// useful for debugging and error catching
	function phet_mysql_query($query) {
		//print "<debug-message>" . htmlentities($query) . "</debug-message>";
		
		// actually execute the query
		$result = mysql_query($query);
		
		if(mysql_errno()) {
			// TODO: remove after development is over, could expose some inner workings of DB? Ask Dano
			//print "<warning-message>" . htmlentities(mysql_error()) . "</warning-message>";
		} else {
			//print "<debug-message>affected: " . htmlentities(mysql_affected_rows()) . "</debug-message>";
		}
		return $result;
	}
	
	// get the id value corresponding to a unique value. if it doesn't exist, create a new row
	//
	// IMPORTANT: $table_field_id should be the AUTO_INCREMENT field for using this function
	// IMPORTANT: mysql_real_escape_string should be called on $table_value beforehand, since
	// strings should be quoted before they reach here (mysql_real_escape_string here would
	// ruin those quotes)
	//
	// example: get_id_value('flash_os', 'id', 'name', 'Mac OS X');
	// this would check to see whether 'Mac OS X' existed in the flash_os table under name.
	// if it exists, it returns the value of 'id' for that row
	// if it does not exist, it is inserted, and the value of the auto_increment field (usually id) is returned
	function get_id_value($table_name, $table_field_id, $table_field_value, $table_value) {
		
		if($table_value != "NULL") {
			// if not null, do a conditional insert into the normalized table
			// (null should already be in the table, and the insert will only take place if the value is not in the table)
			$query = <<<BOO
INSERT INTO {$table_name} ({$table_field_value})
SELECT DISTINCT {$table_value}
FROM {$table_name}
WHERE NOT EXISTS (
	SELECT {$table_field_id}
	FROM {$table_name}
	WHERE name = {$table_value}
);
BOO;
			phet_mysql_query($query);
		}
		
		// craft a select statement that will find the ID corresponding to the value
		// TODO: possibly save a query by assuming ID for NULL is 1?  (should work now, but if things change...)
		$query = "SELECT {$table_field_id} FROM {$table_name} WHERE {$table_field_value} " . ($table_value == "NULL" ? "IS NULL" : "= {$table_value}");
		
		$result = phet_mysql_query($query);
		
		$row = mysql_fetch_row($result);
		
		$id = $row[0];
		
		/*
		if(empty($id)) {
			// either 0 or something unknown. either way, an error occurred
			print "<warning-message>Could not find correct ID</warning-message>";
		}
		*/
		
		return $id;
	}
	
	// surround a string with quotes, and escape it (unless it is null)
	function quo($str) {
		if($str === "NULL") {
			// do not quote NULL
			return "NULL";
		} else {
			return "'" . mysql_real_escape_string($str) . "'";
		}
	}
	
	// turn a table name and associative array into an insert statement into that table
	// IMPORTANT: everything in $values should be safe for mysql (use mysql_real_escape_string
	// on things that came from external input)
	function query_from_values($table_name, $values) {
		$query = "INSERT INTO {$table_name} (";
		$query .= join(', ', array_keys($values));
		$query .= ") VALUES (";
		$query .= join(', ', array_values($values));
		$query .= ");";
		return $query;
	}
	
	// insert data into the session table
	function insert_session($data) {
		global $serverVersion;
		
		// get IDs from normalized tables
		$sim_type_ID = get_id_value("sim_type", "id", "name", quo($data['sim_type']));
		$sim_project_ID = get_id_value("sim_project", "id", "name", quo($data['sim_project']));
		$sim_name_ID = get_id_value("sim_name", "id", "name", quo($data['sim_name']));
		$sim_deployment_ID = get_id_value("deployment", "id", "name", quo($data['sim_deployment']));
		$sim_distribution_tag_ID = get_id_value("distribution_tag", "id", "name", quo($data['sim_distribution_tag']));
		$host_simplified_os_ID = get_id_value("simplified_os", "id", "name", quo($data['host_simplified_os']));
		
		$values = array(
			'timestamp' => 'NOW()',
			'message_version' => mysql_real_escape_string($data['message_version']),
			'server_revision' => $serverVersion,
			'sim_type' => $sim_type_ID,
			'sim_project' => $sim_project_ID,
			'sim_name' => $sim_name_ID,
			'sim_major_version' => mysql_real_escape_string($data['sim_major_version']),
			'sim_minor_version' => mysql_real_escape_string($data['sim_minor_version']),
			'sim_dev_version' => mysql_real_escape_string($data['sim_dev_version']),
			'sim_revision' => mysql_real_escape_string($data['sim_revision']),
			'sim_version_timestamp' => 'FROM_UNIXTIME(' . mysql_real_escape_string($data['sim_version_timestamp']) . ')',
			'sim_locale_language' => quo($data['sim_locale_language']),
			'sim_locale_country' => quo($data['sim_locale_country']),
			'sim_sessions_since' => mysql_real_escape_string($data['sim_sessions_since']),
			'sim_total_sessions' => mysql_real_escape_string($data['sim_total_sessions']),
			'sim_deployment' => $sim_deployment_ID,
			'sim_distribution_tag' => $sim_distribution_tag_ID,
			'sim_dev' => mysql_real_escape_string($data['sim_dev']),
			'host_locale_language' => quo($data['host_locale_language']),
			'host_locale_country' => quo($data['host_locale_country']),
			'host_simplified_os' => $host_simplified_os_ID,
		);
		
		// build query from values to be inserted
		$query = query_from_values("session", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
		return mysql_insert_id();
	}
	
	// insert data into the flash_info table
	function insert_flash_info($data) {
		// get IDs from normalized tables
		$host_flash_version_type_ID = get_id_value("flash_version_type", "id", "name", quo($data['host_flash_version_type']));
		$host_flash_domain_ID = get_id_value("flash_domain", "id", "name", quo($data['host_flash_domain']));
		$host_flash_os_ID = get_id_value("flash_os", "id", "name", quo($data['host_flash_os']));
		
		$values = array(
			'session_id' => mysql_real_escape_string($data['session_id']),
			'host_flash_version_type' => $host_flash_version_type_ID,
			'host_flash_version_major' => mysql_real_escape_string($data['host_flash_version_major']),
			'host_flash_version_minor' => mysql_real_escape_string($data['host_flash_version_minor']),
			'host_flash_version_revision' => mysql_real_escape_string($data['host_flash_version_revision']),
			'host_flash_version_build' => mysql_real_escape_string($data['host_flash_version_build']),
			'host_flash_time_offset' => mysql_real_escape_string($data['host_flash_time_offset']),
			'host_flash_accessibility' => mysql_real_escape_string($data['host_flash_accessibility']),
			'host_flash_domain' => $host_flash_domain_ID,
			'host_flash_os' => $host_flash_os_ID
		);
		
		// build query from values to be inserted
		$query = query_from_values("session_flash_info", $values);
		
		phet_mysql_query($query);
		
		// return how many rows were affected. should be 1, otherwise an error occurred
		return mysql_affected_rows();
	}
	
	// insert data into the java_info table
	function insert_java_info($data) {
		// get IDs from normalized tables
		$host_java_os_name_ID = get_id_value("java_os_name", "id", "name", quo($data['host_java_os_name']));
		$host_java_os_version_ID = get_id_value("java_os_version", "id", "name", quo($data['host_java_os_version']));
		$host_java_os_arch_ID = get_id_value("java_os_arch", "id", "name", quo($data['host_java_os_arch']));
		$host_java_vendor_ID = get_id_value("java_vendor", "id", "name", quo($data['host_java_vendor']));
		$host_java_webstart_version_ID = get_id_value("java_webstart_version", "id", "name", quo($data['host_java_webstart_version']));
		$host_java_timezone_ID = get_id_value("java_timezone", "id", "name", quo($data['host_java_timezone']));
		
		$values = array(
			'session_id' => mysql_real_escape_string($data["session_id"]),
			'host_java_os_name' => $host_java_os_name_ID,
			'host_java_os_version' => $host_java_os_version_ID,
			'host_java_os_arch' => $host_java_os_arch_ID,
			'host_java_vendor' => $host_java_vendor_ID,
			'host_java_version_major' => mysql_real_escape_string($data['host_java_version_major']),
			'host_java_version_minor' => mysql_real_escape_string($data['host_java_version_minor']),
			'host_java_version_maintenance' => mysql_real_escape_string($data['host_java_version_maintenance']),
			'host_java_webstart_version' => $host_java_webstart_version_ID,
			'host_java_timezone' => $host_java_timezone_ID
		);
		
		// build query from values to be inserted
		$query = query_from_values("session_java_info", $values);
		
		phet_mysql_query($query);
		
		// return how many rows were affected. should be 1, otherwise an error occurred
		return mysql_affected_rows();
	}
	
	// insert an entire flash message
	// returns the session ID of the inserted session (or 0 for failure)
	function insert_flash_message($data) {
		// this is a Flash sim
		$data['sim_type'] = 'flash';
		
		// calculate hostSimplifiedOS
		$data['host_simplified_os'] = "Unknown";
		$type = $data['host_flash_version_type'];
		if($type == 'WIN') {
			$data['host_simplified_os'] = "Windows - General";
			$os = $data['host_flash_os'];
			if(stripos($os, 'Vista') !== false) {
				$data['host_simplified_os'] = "Windows - Vista";
			} else if(stripos($os, 'XP') !== false) {
				$data['host_simplified_os'] = "Windows - XP";
			}
		} else if($type == 'MAC') {
			$data['host_simplified_os'] = "Mac - General";
			if(stripos($data['host_flash_os'], ' 10') !== false || stripos($data['host_flash_os'], 'OS X') !== false) {
				$data['host_simplified_os'] = "Mac - OS X";
			}
		} else if($type == 'LNX') {
			$data['host_simplified_os'] = "Linux - General";
		} else if($type == 'UNIX') {
			$data['host_simplified_os'] = "Unix - General";
		}
		
		// store the ID of the inserted session
		$sessionID = insert_session($data);
		
		if(empty($sessionID)) {
			// either 0 or false, either way the insertion failed
			
			// we did not succeed, so inform the caller
			// don't even try to insert a flash message,
			// the PK is non-null and would fail anyways
			return 0;
		}
		
		$data['session_id'] = $sessionID;
		
		// returned rows should be 1
		$rowsAffected = insert_flash_info($data);
		
		// remove session entry if flash insertion fails
		if($rowsAffected < 1) {
			phet_mysql_query("DELETE FROM session WHERE id = {$sessionID}");
		}
		
		// if we failed, return 0, otherwise return the session ID
		if($rowsAffected < 1) {
			return 0;
		} else {
			return $sessionID;
		}
	}
	
	// insert an entire java message
	// returns the session ID of the inserted session (or 0 for failure)
	function insert_java_message($data) {
		// this is a Java sim
		$data["sim_type"] = 'java';
		
		// calculate hostSimplifiedOS
		$data['host_simplified_os'] = "Unknown";
		$osname = $data['host_java_os_name'];
		if(stripos($osname, 'Windows') !== false) {
			$data['host_simplified_os'] = "Windows - General";
			if(stripos($osname, 'Vista') !== false) {
				$data['host_simplified_os'] = "Windows - Vista";
			} else if(stripos($osname, 'XP') !== false) {
				$data['host_simplified_os'] = "Windows - XP";
			}
		} else if(stripos($osname, 'Mac') !== false) {
			$data['host_simplified_os'] = "Mac - General";
			if(stripos($data['host_java_os_version'], '10') === 0 || stripos($data['host_java_os_name'], 'OS X') !== false) {
				$data['host_simplified_os'] = "Mac - OS X";
			}
		} else if(stripos($osname, 'Linux') !== false) {
			$data['host_simplified_os'] = "Linux - General";
		} else if(stripos($osname, 'Unix') !== false) {
			$data['host_simplified_os'] = "Unix - General";
		}
		
		// store the ID of the inserted session
		$sessionID = insert_session($data);
		
		if(empty($sessionID)) {
			// either 0 or false, either way the insertion failed
			
			// we did not succeed, so inform the caller
			// don't even try to insert a flash message,
			// the PK is non-null and would fail anyways
			return 0;
		}
		
		$data['session_id'] = $sessionID;
		
		// returned rows should be 1
		$rowsAffected = insert_java_info($data);
		
		// remove session entry if java insertion fails
		if($rowsAffected < 1) {
			phet_mysql_query("DELETE FROM session WHERE id = {$sessionID}");
		}
		
		// if we failed, return 0, otherwise return the session ID
		if($rowsAffected < 1) {
			return 0;
		} else {
			return $sessionID;
		}
	}
	
	// insert/update data for the user table
	// should return an error string if there was an error, otherwise will return ""
	function update_user($userPreferencesFileCreationTime, $userInstallTimestamp, $userTotalSessions) {
		// TODO: add success return value, use mysql_affected_rows, and mysql_errno to detect any errors
		
		// escaped versions
		$safe_time = mysql_real_escape_string($userPreferencesFileCreationTime);
		$safe_install_timestamp = mysql_real_escape_string($userInstallTimestamp);
		$safe_sessions = mysql_real_escape_string($userTotalSessions);
		// we need to find out whether an entry exists for this particular file creation time AND install timestamp
		if(empty($safe_install_timestamp) || $safe_install_timestamp == "NULL" || $safe_install_timestamp == "none") {
			// not from an installation, set timestamp value to NULL
			$safe_install_timestamp = "NULL";
			$query = "SELECT user_preferences_file_creation_time, user_installation_timestamp FROM user WHERE (user_preferences_file_creation_time = {$safe_time} AND user_installation_timestamp IS NULL);";
		} else {
			$query = "SELECT user_preferences_file_creation_time, user_installation_timestamp FROM user WHERE (user_preferences_file_creation_time = {$safe_time} AND user_installation_timestamp = {$safe_install_timestamp});";
		}
		$result = phet_mysql_query($query);
		
		// number of rows that match the above query. should be 1 if the user has been seen before,
		// and 0 if they haven't been seen
		$num_rows = mysql_num_rows($result);
		
		if(mysql_errno()) {
			return "update_user SELECT failed";
		}
		
		if($num_rows === 0) {
			// first time this user is seen
			// values to be inserted
			$values = array(
				'user_preferences_file_creation_time' => $safe_time,
				'user_installation_timestamp' => $safe_install_timestamp,
				'user_total_sessions' => $safe_sessions,
				'first_seen_month' => quo(date("Y-m-01", time())), // current year and month
				'last_seen_month' => quo(date("Y-m-01", time())) // current year and month
			);
			$insert_query = query_from_values("user", $values);
			phet_mysql_query($insert_query);
			
			if(mysql_errno()) {
				return "update_user INSERT failed";
			}
		} else {
			// user already in table, update values
			
			// test whether install timestamp is the same (either NULL or with a value)
			$timestamp_test = "user_installation_timestamp " . ($safe_install_timestamp == "NULL" ? "IS NULL" : "= {$safe_install_timestamp}");
			
			// update total sessions
			$update_query = "UPDATE user SET user_total_sessions = {$safe_sessions} WHERE (user_preferences_file_creation_time = {$safe_time} AND {$timestamp_test})";
			phet_mysql_query($update_query);
			
			if(mysql_errno()) {
				return "update_user UPDATE 1 failed";
			}
			
			// update last_seen_month with current year and month
			$last_seen_month = quo(date("Y-m-01", time()));
			$update_query = "UPDATE user SET last_seen_month = {$last_seen_month} WHERE (user_preferences_file_creation_time = {$safe_time} AND {$timestamp_test})";
			phet_mysql_query($update_query);
			
			if(mysql_errno()) {
				return "update_user UPDATE 2 failed";
			}
		}
		
		return "";
	}
	
	// record an error
	function message_error($raw_post_data, $info) {
		global $serverVersion;
		
		$values = array(
			'timestamp' => 'NOW()',
			'server_revision' => $serverVersion,
			'raw_post_data' => quo($raw_post_data),
			'mysql_error' => quo(mysql_error()),
			'error_info' => quo($info)
		);
		
		$query = query_from_values("message_error", $values);
		mysql_query($query);
	}
?>