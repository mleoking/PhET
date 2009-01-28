<?php
	
	include("db-tracking.php");
	$link = setup_mysql();
	
	set_time_limit(120);
	
	$year = mysql_real_escape_string($_GET['year']);
	$sim = mysql_real_escape_string($_GET['sim']);
	
	mysql_query("SET @year := " . $year . ";");
	
	$result = mysql_query("SELECT TO_DAYS('" . ($year + 1) . "-01-01') - TO_DAYS('" . ($year) . "-01-01');");
	
	$row = mysql_fetch_row($result);
	
	$num_days = $row[0];
	
	if($sim) {
		$result = mysql_query("SELECT MONTH(timestamp), DAY(timestamp), DAYOFYEAR(timestamp), SUM(sim_sessions_since) FROM session, (SELECT id FROM sim_name WHERE sim_name.name = '" . $sim . "') as x WHERE (YEAR(timestamp) = @year AND sim_dev = false AND sim_name = x.id) GROUP BY MONTH(timestamp), DAY(timestamp), DAYOFYEAR(timestamp) ORDER BY MONTH(timestamp), DAY(timestamp), DAYOFYEAR(timestamp)");
	} else {
		$result = mysql_query("SELECT MONTH(timestamp), DAY(timestamp), DAYOFYEAR(timestamp), SUM(sim_sessions_since) FROM session WHERE (YEAR(timestamp) = @year AND sim_dev = false) GROUP BY MONTH(timestamp), DAY(timestamp), DAYOFYEAR(timestamp) ORDER BY MONTH(timestamp), DAY(timestamp), DAYOFYEAR(timestamp)");
	}
	
	$MONTH = 0;
	$DAY = 1;
	$DAYOFYEAR = 2;
	$SUM = 3;
	
	print mysql_error();
	
	$num_rows = mysql_num_rows($result);
	
	$arr = array();
	
	$max_val = 0;
	
	for($i = 0; $i < $num_rows; $i++) {
		$row = mysql_fetch_row($result);
		array_push($arr, $row);
		
		//print "<p>";
		//print_r($row);
		//print "</p>";
		
		if($row[$SUM] > $max_val) {
			$max_val = $row[$SUM];
		}
	}
	
	if($_GET['max']) {
		$max_val = $_GET['max'];
	}
	
	$data = array_fill(1, $num_days, 0);
	
	for($i = 0; $i < sizeof($arr); $i++) {
		$data[$arr[$i][$DAYOFYEAR]] = $arr[$i][$SUM];
	}
	
	
	
	$width = $num_days;
	$height = 200;
	
	$im = imagecreate($width, $height);
	
	$black = imagecolorallocate ($im,0x00,0x00,0x00);
	$gray = imagecolorallocate ($im,0xcc,0xcc,0xcc);
	$lightgray = imagecolorallocate ($im,0xee,0xee,0xee);
	$white = imagecolorallocate ($im,0xff,0xff,0xff);
	
	imagefilledrectangle($im,0,0,$width,$height,$lightgray);
	
	imagestring($im, 1, 0, 0, $max_val, $black);
	
	for($i = 1; $i <= $num_days; $i++) {
		imageline($im, $i, $height, $i, $height - ($data[$i] * $height / $max_val), $black);
	}
	
	header("Content-type: image/png");
	imagepng($im);
	
	
	
	
	
	/*
	$width = $num_rows;
	$height = 200;
	
	$im = imagecreate($width, $height);
	
	$black = imagecolorallocate ($im,0x00,0x00,0x00);
	$gray = imagecolorallocate ($im,0xcc,0xcc,0xcc);
	$white = imagecolorallocate ($im,0xff,0xff,0xff);
	
	imagefilledrectangle($im,0,0,$width,$height,$white);
	
	for($i = 0; $i < $num_rows; $i++) {
		$row = mysql_fetch_row($result);
		//print "<p>";
		//print_r($row);
		//print "</p>";
		imageline($im, $i, $height, $i, $height - $row[3] / 100, $black);
	}
	
	header("Content-type: image/png");
	imagepng($im);
	
	*/
?>