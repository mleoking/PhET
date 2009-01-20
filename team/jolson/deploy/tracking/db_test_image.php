<?php
	include("db_util.php");
	$link = setup_mysql();
	
	set_time_limit(120);
	
	$result = mysql_query("SELECT DISTINCT YEAR(timestamp), MONTH(timestamp), DAY(timestamp), COUNT(id) FROM simulation WHERE DATEDIFF(CURRENT_DATE, timestamp) < 365 GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
	
	$num_rows = mysql_num_rows($result);
	
	
	
	
	
	$width = $num_rows;
	$height = 200;
	
	$im = imagecreate($width, $height);
	
	$black = imagecolorallocate ($im,0x00,0x00,0x00);
	$gray = imagecolorallocate ($im,0xcc,0xcc,0xcc);
	$white = imagecolorallocate ($im,0xff,0xff,0xff);
	
	imagefilledrectangle($im,0,0,$width,$height,$white);
	
	for($i = 0; $i < $num_rows; $i++) {
		$row = mysql_fetch_row($result);
		//echo "<p>";
		//print_r($row);
		//echo "</p>";
		imageline($im, $i, $height, $i, $height - $row[3] / 100, $black);
	}
	
	header("Content-type: image/png");
	imagepng($im);
	
	/*
	$width = 300;
	$height = 200;
	$im = imagecreate($width,$height);
	$gray = imagecolorallocate ($im,0xcc,0xcc,0xcc);
	$black = imagecolorallocate ($im,0x00,0x00,0x00);
	$gray_dark = imagecolorallocate ($im,0x7f,0x7f,0x7f);
	$white = imagecolorallocate ($im,0xff,0xff,0xff);
	imagefilledrectangle($im,0,0,$width,$height,$white);
	imageline($im, 0, 0, 100, 100, $black);
	imageline($im, 0, 0, 0, 100, $black);
	imageline($im, 0, 0, 100, 0, $black);
	header ("Content-type: image/png");
	imagepng($im);
	*/
?>