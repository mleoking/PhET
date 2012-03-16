<?php
	// display the raw log
	
	// require authentication
	include("db-auth.php");
	
	// display the file
	$arr = file("parsed-log.txt");
	foreach($arr as $line) {
		print str_replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", htmlentities($line)) . "<br />";
	}
?>