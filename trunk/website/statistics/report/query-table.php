<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	// used to extract fieldnames and display each table
	function display_result($result) {
		$num_rows = mysql_num_rows($result);
		print "<table border=1>\n";
		print "<tr>\n";
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
	
	display_result(report_result($_GET));
?>