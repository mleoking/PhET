<?php
	include("db-tracking-login.php");
	
	// used for every mysql query that needs to be made
	// useful for debugging and error catching
	function phet_mysql_query($query) {
		//print "<p>" . $query . "</p>";
		
		// actually execute the query
		$result = mysql_query($query);
		
		//print "<p>" . mysql_error() . "</p>";
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
	function insert_session($data) {
		// get IDs from normalized tables
		$sim_project_ID = get_id_value("sim_project", "id", "name", quo($data['sim_project']));
		$sim_name_ID = get_id_value("sim_name", "id", "name", quo($data['sim_name']));
		$sim_deployment_ID = get_id_value("deployment", "id", "name", quote_null_if_none($data['sim_deployment']));
		$sim_distribution_tag_ID = get_id_value("distribution_tag", "id", "name", quote_null_if_none($data['sim_distribution_tag']));
		$host_simplified_os_ID = get_id_value("simplified_os", "id", "name", quo($data['host_simplified_os']));
		
		$values = array(
			new Field('message_version', $data['message_version']),
			new Field('sim_type', $data['sim_type']),
			new Field('sim_project', $sim_project_ID),
			new Field('sim_name', $sim_name_ID),
			new Field('sim_major_version', $data['sim_major_version']),
			new Field('sim_minor_version', $data['sim_minor_version']),
			new Field('sim_dev_version', $data['sim_dev_version']),
			new Field('sim_svn_revision', $data['sim_svn_revision']),
			new Field('sim_locale_language', quo($data['sim_locale_language'])),
			new Field('sim_locale_country', quote_null_if_none($data['sim_locale_country'])),
			new Field('sim_sessions_since', $data['sim_sessions_since']),
			new Field('sim_sessions_ever', $data['sim_sessions_ever']),
			new Field('sim_deployment', $sim_deployment_ID),
			new Field('sim_distribution_tag', $sim_distribution_tag_ID),
			new Field('sim_dev', $data['sim_dev']),
			new Field('host_locale_language', quo($data['host_locale_language'])),
			new Field('host_locale_country', quote_null_if_none($data['host_locale_country'])),
			new Field('host_simplified_os', $host_simplified_os_ID),
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
			new Field('session_id', $data['session_id']),
			new Field('host_flash_version_type', $host_flash_version_type_ID),
			new Field('host_flash_version_major', $data['host_flash_version_major']),
			new Field('host_flash_version_minor', $data['host_flash_version_minor']),
			new Field('host_flash_version_revision', $data['host_flash_version_revision']),
			new Field('host_flash_version_build', $data['host_flash_version_build']),
			new Field('host_flash_time_offset', $data['host_flash_time_offset']),
			new Field('host_flash_accessibility', $data['host_flash_accessibility']),
			new Field('host_flash_domain', $host_flash_domain_ID),
			new Field('host_flash_os', $host_flash_os_ID)
		);
		
		// build query from values to be inserted
		$query = query_from_values("session_flash_info", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
		return mysql_insert_id();
	}
	
	// insert data into the java_info table
	function insert_java_info($data) {
		// get IDs from normalized tables
		$host_java_os_name_ID = get_id_value("java_os_name", "id", "name", quo($data['host_java_os_name']));
		$host_java_os_version_ID = get_id_value("java_os_version", "id", "name", quo($data['host_java_os_version']));
		$host_java_os_arch_ID = get_id_value("java_os_arch", "id", "name", quo($data['host_java_os_arch']));
		$host_java_vendor_ID = get_id_value("java_vendor", "id", "name", quo($data['host_java_vendor']));
		$host_java_webstart_version_ID = get_id_value("java_webstart_version", "id", "name", quote_null_if_none($data['host_java_webstart_version']));
		$host_java_timezone_ID = get_id_value("java_timezone", "id", "name", quo($data['host_java_timezone']));
		
		$values = array(
			new Field('session_id', $data["session_id"]),
			new Field('host_java_os_name', $host_java_os_name_ID),
			new Field('host_java_os_version', $host_java_os_version_ID),
			new Field('host_java_os_arch', $host_java_os_arch_ID),
			new Field('host_java_vendor', $host_java_vendor_ID),
			new Field('host_java_version_major', $data['host_java_version_major']),
			new Field('host_java_version_minor', $data['host_java_version_minor']),
			new Field('host_java_version_maintenance', $data['host_java_version_maintenance']),
			new Field('host_java_webstart_version', $host_java_webstart_version_ID),
			new Field('host_java_timezone', $host_java_timezone_ID)
		);
		
		// build query from values to be inserted
		$query = query_from_values("session_java_info", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
		return mysql_insert_id();
	}
	
	// insert an entire flash message
	function insert_flash_message($data) {
		// this is a Flash sim
		$data['sim_type'] = quo("flash");
		
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
		} else if($type == 'LNX') {
			$data['host_simplified_os'] = "Linux - General";
		} else if($type == 'UNIX') {
			$data['host_simplified_os'] = "Unix - General";
		}
		
		// store the ID of the inserted session
		$sessionID = insert_session($data);
		
		$data['session_id'] = $sessionID;
		
		insert_flash_info($data);
		
		return $sessionID;
	}
	
	// insert an entire java message
	function insert_java_message($data) {
		// this is a Java sim
		$data["sim_type"] = quo("java");
		
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
		} else if(stripos($osname, 'Linux') !== false) {
			$data['host_simplified_os'] = "Linux - General";
		} else if(stripos($osname, 'Unix') !== false) {
			$data['host_simplified_os'] = "Unix - General";
		}
		
		// store the ID of the inserted session
		$sessionID = insert_session($data);
		
		$data['session_id'] = $sessionID;
		
		insert_java_info($data);
		
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