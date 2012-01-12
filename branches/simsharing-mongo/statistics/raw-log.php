<?php
	// display the raw log
	
	// require authentication
	include("db-auth.php");
	
	// display the file
	$arr = file("raw-log.txt");
	foreach($arr as $line) {
		print htmlentities($line) . "<br />";
	}
?>