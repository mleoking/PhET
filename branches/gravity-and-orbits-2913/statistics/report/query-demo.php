<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	$queries = report_query($_GET);
	$result = null;
	foreach($queries as $query) {
		print "<p>{$query}</p>";
		$result = phet_mysql_query($query);
	}
	display_result($result);
?>