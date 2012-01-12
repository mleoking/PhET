<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	$_GET['query'] = "empty";
	
	$result = report_result($_GET);
	
	
	print "<select name='{$_GET['select_name']}'>";
	
	if(!$_GET['hide_all']) {
		print "<option value='all'>all</option>";
	}
	
	$num_rows = mysql_num_rows($result);
	while($get_info = mysql_fetch_row($result)) {
		foreach($get_info as $field) {
			if($field !== null) {
				echo "<option value='{$field}'>{$field}</option>";
			}
		}
	}
	
	print "</select>";
?>