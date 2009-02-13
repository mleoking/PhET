<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	$_GET['query'] = "empty";
	
	
	print "<select name='sim_project' onchange='javascript:specify_name()'>";
	print "<option value='all'>all</option>";
	print "<optgroup label='Java'>";
	
	$arr = array(
		'query' => 'empty',
		'group' => 'sim_project',
		'order' => 'sim_project',
		'sim_type' => 'java'
	);
	$result = report_result($arr);
	display_options($result);
	
	print "</optgroup>";
	print "<optgroup label='Flash'>";
	
	$arr['sim_type'] = 'flash';
	$result = report_result($arr);
	display_options($result);
	
	print "</optgroup>";
	print "</select>";
?>