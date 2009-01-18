
<html><?php
	include("db_util.php");
	
	// temporary code to log tracking messages being sent
	
	$rawfile = fopen('raw-log.txt', 'a');
	fwrite($rawfile, $HTTP_RAW_POST_DATA . "\n");
	fclose($rawfile);
	
	$xml = simplexml_load_string($HTTP_RAW_POST_DATA);
	
	$filename = "tracking-log.txt";
	$file = fopen($filename, 'a');
	$str = date('y/m/d h:i:s A') . ":\n";
	
	$flashFields = array("message_type", "message_version", "user_preference_file_creation_time", "user_total_sessions", "sim_type", "sim_project", "sim_name", "sim_major_version", "sim_minor_version", "sim_dev_version", "sim_svn_revision", "sim_locale_language", "sim_locale_country", "sim_sessions_since", "sim_sessions_ever", "sim_usage_type", "sim_distribution_tag", "sim_dev", "host__flash_os", "host_flash_version", "host_locale_language", "host_flash_time_offset", "host_flash_accessibility", "host_flash_domain");
	
	$query_session = "unused";
	$query_flash_info = "unused";
	$query_java_info = "unused";
	
	if($xml["sim_type"] == "flash") {
		// dump it into tracking-log.txt
		foreach($flashFields as &$field) {
			$str .= "\t" . $field . "=" . urldecode($xml[$field]) . "\n";
		}
		
		
		if($xml["message_version"] == "1") {
			$link = setup_mysql();
			//mysql_real_escape_string
			$query_session = "INSERT INTO sessions (message_version, sim_type, sim_project, sim_name, sim_major_version, sim_minor_version, sim_dev_version, sim_svn_revision, sim_locale_language, sim_locale_country, sim_sessions_since, sim_sessions_ever, sim_usage_type, sim_distribution_tag, sim_dev, sim_scenario, host_locale_language, host_locale_country, host_simplified_os) VALUES (";
			$query_session .= $xml["message_version"] . ", "; // message_version
			$query_session .= "1, "; // sim_type
			$query_session .= "'" . mysql_real_escape_string(urldecode($xml["sim_project"])) . "'" . ", "; // sim_project
			$query_session .= "'" . mysql_real_escape_string(urldecode($xml["sim_name"])) . "'" . ", "; // sim_name
			$query_session .= mysql_real_escape_string(urldecode($xml["sim_major_version"])) . ", "; // sim_major_version
			$query_session .= mysql_real_escape_string(urldecode($xml["sim_minor_version"])) . ", "; // sim_minor_version
			$query_session .= mysql_real_escape_string(urldecode($xml["sim_dev_version"])) . ", "; // sim_dev_version
			$query_session .= mysql_real_escape_string(urldecode($xml["sim_svn_revision"])) . ", "; // sim_svn_revision
			$query_session .= "'" . mysql_real_escape_string(urldecode($xml["sim_locale_language"])) . "'" . ", "; // sim_locale_language
			if(urldecode($xml["sim_locale_country"]) == "none") {
				$query_session .= "NULL, "; // sim_locale_country
			} else {
				$query_session .= "'" . mysql_real_escape_string(urldecode($xml["sim_locale_country"])) . "'" . ", "; // sim_locale_country
			}
			$query_session .= mysql_real_escape_string(urldecode($xml["sim_sessions_since"])) . ", "; // sim_sessions_since
			$query_session .= mysql_real_escape_string(urldecode($xml["sim_sessions_ever"])) . ", "; // sim_sessions_ever
			$query_session .= "'" . mysql_real_escape_string(urldecode($xml["sim_usage_type"])) . "'" . ", "; // sim_usage_type
			if(urldecode($xml["sim_distribution_tag"]) == "none") {
				$query_session .= "NULL, "; // sim_distribution_tag
			} else {
				$query_session .= "'" . mysql_real_escape_string(urldecode($xml["sim_distribution_tag"])) . "'" . ", "; // sim_distribution_tag
			}
			$query_session .= mysql_real_escape_string(urldecode($xml["sim_dev"])) . ", "; // sim_dev
			$query_session .= "NULL, "; // sim_scenario
			$query_session .= "'" . mysql_real_escape_string(urldecode($xml["host_locale_language"])) . "'" . ", "; // host_locale_language
			$query_session .= "NULL, "; // host_locale_country
			$query_session .= "0"; // host_simplified_os
			
			$query_session .= ");";
			
			echo "<p>query_session: " . $query_session . "</p>";
			
			mysql_query($query_session);
			
			echo "<p>errstate" . mysql_error($link) . "</p>";
			
			$session_id = mysql_insert_id();
			
			$query_flash_info = "INSERT INTO flash_info (session_id, host_flash_version_type, host_flash_version_major, host_flash_version_minor, host_flash_version_revision, host_flash_version_build, host_flash_time_offset, host_flash_accessibility, host_flash_domain, host_flash_os) VALUES (";
			$query_flash_info .= $session_id . ", "; // session_id
			
			$version_left = substr(urldecode($xml["host_flash_version"]), 0, stripos(urldecode($xml["host_flash_version"]), " "));
			$version_right = substr(urldecode($xml["host_flash_version"]), stripos(urldecode($xml["host_flash_version"]), " ") + 1);
			$version_numbers = explode(",", $version_right);
			//echo "////" . $version_numbers[2] . "////";
			$query_flash_info .= "'" . mysql_real_escape_string($version_left) . "'" . ", "; // host_flash_version_type
			$query_flash_info .= mysql_real_escape_string($version_numbers[0]) . ", "; // host_flash_version_major
			$query_flash_info .= mysql_real_escape_string($version_numbers[1]) . ", "; // host_flash_version_minor
			$query_flash_info .= mysql_real_escape_string($version_numbers[2]) . ", "; // host_flash_version_revision
			$query_flash_info .= mysql_real_escape_string($version_numbers[3]) . ", "; // host_flash_version_build
			$query_flash_info .= mysql_real_escape_string(urldecode($xml["host_flash_time_offset"])) . ", "; // host_flash_time_offset
			$query_flash_info .= mysql_real_escape_string(urldecode($xml["host_flash_accessibility"])) . ", "; // host_flash_accessibility
			$query_flash_info .= "'" . mysql_real_escape_string(urldecode($xml["host_flash_domain"])) . "'" . ", "; // host_flash_domain
			$query_flash_info .= "'" . mysql_real_escape_string(urldecode($xml["host_flash_os"])) . "'"; // host_flash_os
			
			$query_flash_info .= ");";
			
			echo "<p>query_flash_info: " . $query_flash_info . "</p>";
			
			mysql_query($query_flash_info);
			
			echo "<p>errstate" . mysql_error($link) . "</p>";
			
			
		}
	}
	
	$str .= "\n";
	
	echo "<p>Received Successfully</p>";
	
	fwrite($file, $str);
	fclose($file);
?>
</html>
