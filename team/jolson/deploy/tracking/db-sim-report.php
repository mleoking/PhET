<?php
	include("db-tracking.php");
	$link = setup_mysql();
	
	set_time_limit(60 * 20);
	
	function display_desc($desc) {
		print "\n<h4>" . $desc . "</h4>\n";
	}
	
	function display_query($query) {
		$show_results = true;
		
		$start_time = microtime(true);
		$result = mysql_query($query) or die("SELECT ERROR: " . mysql_error());
		$end_time = microtime(true);
		$num_rows = mysql_num_rows($result);
		//print "<h6>$query</h6>\n";
		if($show_results) {
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
				print "\t<td><font face=arial size=2/>$field</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	$total_start_time = microtime(true);
	
	$simName = mysql_real_escape_string($_SERVER['QUERY_STRING']);
	
	print "<h1>Report for " . $simName . "</h1>";
	
	mysql_query("SELECT (@sid := sim_name.id) FROM sim_name WHERE sim_name.name = " . quo($simName) . ";");
	
	display_desc("total number of messages");
	display_query("SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid)");
	
	display_desc("total number of messages (non-dev)");
	display_query("SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_desc("total number of sim runs (non-dev)");
	display_query("SELECT SUM(session.sim_sessions_since) AS total_sessions FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_desc("number of times sim have been run by week");
	display_query("SELECT YEARWEEK(timestamp), SUM(session.sim_sessions_since) FROM session WHERE (session.sim_name = @sid) GROUP BY YEARWEEK(timestamp);");
	
	$total_end_time = microtime(true);
	
	print "<br/><h6>TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds</h6>\n";
	
?>