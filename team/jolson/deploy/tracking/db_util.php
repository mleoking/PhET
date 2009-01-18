
<?php
	// connect to the statistics database
	function setup_mysql() {
		$link = mysql_connect("localhost", "www-data", "d3#r3m0nt$") or die(mysql_error());
		mysql_select_db("phet_stats_test_1") or die(mysql_error());
		return $link;
	}
	
	// get the id value corresponding to a unique value. if it doesn't exist, create a new row
	function get_id_value($table_name, $table_field_id, $table_field_value, $table_value) {
		$query = "SELECT $table_field_id FROM $table_name WHERE $table_field_value = $table_value";
		$result = mysql_query($query);
		$num_rows = mysql_num_rows($result);
		if($num_rows == 0) {
			$insert_query = "INSERT INTO $table_name ($table_field_vale) VALUES ($table_value)";
			mysql_query($query);
			return mysql_insert_id();
		}
		$row = mysql_fetch_row($result);
		return $row[$table_field_id];
	}
	
	// surround a string with quotes
	function quo($str) {
		return "'" . $str . "'";
	}
	
	// insert data into the session table
	function insert_session($simType, $simProject, $simName, $simMajorVersion, $simMinorVersion, $simDevVersion, $simSvnRevision, $simLocaleLanguage, $simLocaleCountry, $simSessionsSince, $simSessionsEver, $simUsageType, $simDistributionTag, $simDev, $simScenario, $hostLocaleLanguage, $hostLocaleCountry, $hostSimplifiedOS) {
		
	}
?>

