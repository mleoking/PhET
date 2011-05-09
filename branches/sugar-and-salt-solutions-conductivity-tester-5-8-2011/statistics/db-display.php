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
		$result = mysql_query("SELECT * FROM {$tablename}") or die("SELECT ERROR");

		// number of rows
		$num_rows = mysql_num_rows($result);
		print "<h3>{$tablename}</h3>\n";
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
				print "\t<td><font face=arial size=2/>{$field}</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	print <<<EOT
<html>
<head><title>Statistics</title></head>
<body>
<h1>Raw Database Tables</h1>
EOT;
	
	display_table("user");
	display_table("sim_type");
	display_table("sim_project");
	display_table("sim_name");
	display_table("deployment");
	display_table("distribution_tag");
	display_table("simplified_os");
	display_table("session");
	display_table("flash_version_type");
	display_table("flash_domain");
	display_table("flash_os");
	display_table("session_flash_info");
	display_table("java_os_name");
	display_table("java_os_version");
	display_table("java_os_arch");
	display_table("java_vendor");
	display_table("java_webstart_version");
	display_table("java_timezone");
	display_table("session_java_info");
	display_table("message_error");
	
	mysql_close($link);
	
	print "</body></html>";
?>