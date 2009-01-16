<html>
<head><title>Raw Tracking Data</title></head>
<body>
<h1>Raw Database Values</h1>
<?php
	include("db_util.php");
	$link = setup_mysql();
	
	$result = mysql_query("SELECT * FROM sessions") or die("SELECT ERROR");
	$num_rows = mysql_num_rows($result);
	print "<h3>sessions</h3>\n";
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
	
	$result = mysql_query("SELECT * FROM flash_info") or die("SELECT ERROR");
	$num_rows = mysql_num_rows($result);
	print "<h3>flash_info</h3>\n";
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
	
	$result = mysql_query("SELECT * FROM java_info") or die("SELECT ERROR");
	$num_rows = mysql_num_rows($result);
	print "<h3>java_info</h3>\n";
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
	
	
	
	mysql_close($link);
	
?>
</body>
</html>