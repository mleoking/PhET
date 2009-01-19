<html>
<head><title>Tracking Data</title></head>
<body>
<h1>Raw Database Values</h1>
<?php
	
	// script to display the raw values in the database
	// not fun if you view this with 4 million entries or so (over 8 million tuples)
	
	include("db_util.php");
	$link = setup_mysql();
	
	// used to extract fieldnames and display each table
	function display_table($tablename) {
		$result = mysql_query("SELECT * FROM $tablename") or die("SELECT ERROR");
		$num_rows = mysql_num_rows($result);
		print "<h3>$tablename</h3>\n";
		print "<table border=1>\n";
		$fields_num = mysql_num_fields($result);
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			echo "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		echo "</tr>\n";
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
	
	display_table("user");
	display_table("sim_project");
	display_table("sim_name");
	display_table("deployment");
	display_table("distribution_tag");
	display_table("simplified_os");
	display_table("session");
	display_table("flash_version_type");
	display_table("flash_domain");
	display_table("flash_os");
	display_table("flash_info");
	display_table("java_os_name");
	display_table("java_os_version");
	display_table("java_os_arch");
	display_table("java_vendor");
	display_table("java_webstart_version");
	display_table("java_timezone");
	display_table("java_info");
	
?>
<h1>Joined Data (View Tables)</h1>
<?php
	display_table("simulation");
	display_table("flash_simulation");
	display_table("java_simulation");
	
	
	mysql_close($link);
	
?>
</body>
</html>
