<?php
	include("db-tracking.php");
	$link = setup_mysql();
	
	set_time_limit(60 * 20);
	
	function display_desc($desc) {
		print "\n<h2>" . $desc . "</h2>\n";
	}
	
	function display_query($query) {
		$show_results = true;
		$explain = true;
		
		$start_time = microtime(true);
		$result = mysql_query($query) or die("SELECT ERROR: " . mysql_error());
		$end_time = microtime(true);
		$num_rows = mysql_num_rows($result);
		print "<h6>{$query}</h6>\n";
		print "Running time: " . ($end_time - $start_time) . " seconds\n";
		if($explain) {
			display_explain($query);
		}
		if($show_results) {
			print "<h6>data:</h6>\n";
			print "<table border=1>\n";
		} else {
			print "<table border=1 style='display: none;'>\n";
		}
		$fields_num = mysql_num_fields($result);
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			print "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		print "</tr>\n";
		while($get_info = mysql_fetch_row($result)) {
			print "<tr>\n";
			foreach($get_info as $field) {
				print "\t<td><font face=arial size=2/>{$field}</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	function display_explain($query) {
		$result = mysql_query("EXPLAIN " . $query) or die("EXPLAIN ERROR");
		$num_rows = mysql_num_rows($result);
		print "<h6>explanation:</h6>\n";
		print "<table border=1>\n";
		$fields_num = mysql_num_fields($result);
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			print "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		print "</tr>\n";
		while($get_info = mysql_fetch_row($result)) {
			print "<tr>\n";
			foreach($get_info as $field) {
				print "\t<td><font face=arial size=2/>{$field}</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	$total_start_time = microtime(true);
	
	
	
	
	$total_end_time = microtime(true);
	
	print "<br/><h1>TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds</h1>\n";
	
?>