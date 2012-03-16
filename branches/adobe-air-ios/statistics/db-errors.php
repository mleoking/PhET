<?php
	
	// script to display the raw values in the database
	// not fun if you view this with 4 million entries or so (over 8 million rows)

    // require authentication to display
	include("db-auth.php");

	// necessary functions for database interaction
	include("db-stats.php");

	// connect to the database
	$link = setup_mysql();
	
	// used to extract fieldnames and display each table
	function display_table($tablename) {

	    // query the database
		$result = mysql_query("SELECT * FROM {$tablename} ORDER BY id DESC") or die("SELECT ERROR");

		// number of rows
		$num_rows = mysql_num_rows($result);
		print "<table border=1>\n";

		// number of fields (columns)
		$fields_num = mysql_num_fields($result);

		// print out a row composed of just field names (smaller font)
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			print "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		print "</tr>\n";

		// print out each row of the table
		while($get_info = mysql_fetch_row($result)) {
			print "<tr>\n";
			foreach($get_info as $field) {
				$str = htmlentities($field);
				print "\t<td><font face=arial size=2/>{$str}</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	print <<<EOT
<html>
<head><title>Errors in statistics database</title></head>
<body>
EOT;
	
	display_table("message_error");
	
	mysql_close($link);
	
	print "</body></html>";
?>