<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	$result = report_result($_GET);
	
	$str = "";
	
	$num_rows = mysql_num_rows($result);
	$fields_num = mysql_num_fields($result);
	for($i=0; $i<$fields_num; $i++) {
		$field = mysql_fetch_field($result);
		if($i != 0) { $str .= ","; }
		$str .= $field->name;
	}
	$str .= "\n";
	while($get_info = mysql_fetch_row($result)) {
		$count = 0;
		foreach($get_info as $field) {
			$count++;
			if($count != 1) { $str .= ","; }
			$str .=  "\"{$field}\"";
		}
		$str .= "\n";
	}
	
	$file_size = strlen($str);
	
	header("Content-Type: text/csv");
	header("Content-Disposition: inline; filename=\"data.csv\"");
	//header("Content-Description: ".trim(htmlentities($name)));
	header("Content-Length: $file_size");
	header("Content-Transfer-Encoding: binary");
	header("Connection: close");
	
	print $str;
	
	flush();
?>