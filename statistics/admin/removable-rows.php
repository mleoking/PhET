<?php
    include("../report/queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();

	$queries = report_query($_GET);
	$result = null;
	foreach($queries as $query) {
		print "<p>{$query}</p>";
		$result = phet_mysql_query($query);
	}
	//display_result($result);

	$id = "id";

	switch( $_GET["remove_table"] ) {
	    case "session_java_info":
	    case "session_flash_info":
	        $id = "session_id";
    }

	display_removables($result, $_GET["remove_table"], $id);
?>