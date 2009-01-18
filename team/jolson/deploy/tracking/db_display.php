<html>
<head><title>Tracking Data</title></head>
<body>
<h1>Raw Database Values</h1>
<?php
	include("db_util.php");
	$link = setup_mysql();
	
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
	
	display_table("users");
	display_table("sim_projects");
	display_table("sim_names");
	display_table("usage_types");
	display_table("distribution_tags");
	display_table("scenarios");
	display_table("simplified_os_names");
	display_table("sessions");
	display_table("flash_domains");
	display_table("flash_os_names");
	display_table("flash_info");
	display_table("java_os_names");
	display_table("java_os_versions");
	display_table("java_os_architectures");
	display_table("java_vendors");
	display_table("java_webstart_versions");
	display_table("java_timezones");
	display_table("java_info");
	
?>
<h1>Joined Data</h1>
<?php
	display_table("simulations");
	display_table("flash_simulations");
	display_table("java_simulations");
	
	
	mysql_close($link);
	
?>
</body>
</html>
