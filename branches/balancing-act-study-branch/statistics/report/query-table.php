<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	display_result(report_result($_GET));
?>