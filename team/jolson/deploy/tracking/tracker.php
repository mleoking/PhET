
<html><?php
	// temporary code to log tracking messages being sent
	
	$rawfile = fopen('raw-log.txt', 'a');
	fwrite($rawfile, $HTTP_RAW_POST_DATA . "\n");
	fclose($rawfile);
	
	$xml = simplexml_load_string($HTTP_RAW_POST_DATA);
	
	$filename = "tracking-log.txt";
	$file = fopen($filename, 'a');
	$str = date('y/m/d h:i:s A') . ":\n";
	
	$flashFields = array("type", "user-id", "session-id", "flash-version", "project", "sim", "sim-type", "sim-version", "sim-revision", "sim-locale", "dev", "os", "locale-default", "flash-audio", "flash-accessibility", "flash-manufacturer", "flash-playertype", "screen-x", "screen-y", "user-timezone-offset", "flash-domain", "timestamp");
	
	if($xml["type"] == "session-ended") {
		$str .= "\t" . "type" . "=" . urldecode($xml["type"]) . "\n";
		$str .= "\t" . "session-id" . "=" . urldecode($xml["session-id"]) . "\n";
		$str .= "\t" . "timestamp" . "=" . urldecode($xml["timestamp"]) . "\n";
	} else {
		if($xml["sim-type"] == "flash") {
			foreach($flashFields as &$field) {
				$str .= "\t" . $field . "=" . urldecode($xml[$field]) . "\n";
			}
		}
	}
	
	$str .= "\n";
	
	echo "Received Successfully";
	
	
	
	fwrite($file, $str);
	fclose($file);
?>
</html>
