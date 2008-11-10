
<properties>
<?php
	$filename = "pendulum-lab.properties";
	$file = fopen($filename, 'r');
	$str = fread($file, 1000);
	fclose($file);
	
	$data = explode("\n", $str);
	$data = str_replace("\r", "", $data);
	
	for($idx = 0; $idx < strlen($data); $idx++) {
		$arr = explode("=", $data[$idx]);
		if($arr[0] == "version.major") {
			echo "<versionMajor value=\"" . $arr[1] . "\" />";
		} else if($arr[0] == "version.minor") {
			echo "<versionMinor value=\"" . $arr[1] . "\" />";
		} else if($arr[0] == "version.dev") {
			echo "<dev value=\"" . $arr[1] . "\" />";
		} else if($arr[0] == "version.revision") {
			echo "<revision value=\"" . $arr[1] . "\" />";
		}
	}
?>
</properties>
