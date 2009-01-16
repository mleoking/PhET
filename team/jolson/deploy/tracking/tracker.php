
<html><?php
	// temporary code to log tracking messages being sent
	
	$rawfile = fopen('raw-log.txt', 'a');
	fwrite($rawfile, $HTTP_RAW_POST_DATA . "\n");
	fclose($rawfile);
	
	$xml = simplexml_load_string($HTTP_RAW_POST_DATA);
	
	$filename = "tracking-log.txt";
	$file = fopen($filename, 'a');
	$str = date('y/m/d h:i:s A') . ":\n";
	
	$flashFields = array("message_type", "message_version", "user_preference_file_creation_time", "user_total_sessions", "sim_type", "sim_project", "sim_name", "sim_major_version", "sim_minor_version", "sim_dev_version", "sim_svn_revision", "sim_locale_language", "sim_locale_country", "sim_sessions_since", "sim_sessions_ever", "sim_usage_type", "sim_distribution_tag", "sim_dev", "host_os", "host_flash", "host_locale_language", "host_time_offset", "host_flash_accessibility", "host_flash_domain");
	
	if($xml["sim_type"] == "flash") {
		foreach($flashFields as &$field) {
			$str .= "\t" . $field . "=" . urldecode($xml[$field]) . "\n";
		}
	}
	
	$str .= "\n";
	
	echo "Received Successfully";
	
	
	
	fwrite($file, $str);
	fclose($file);
?>
</html>
