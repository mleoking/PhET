<?php
    include("../report/queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();

    //print_r($_GET); print "<br/>";
	remove_row($_GET["table"], $_GET["id_name"], $_GET["id_val"]);
	if( $_GET["table"] == "session" ) {
	    remove_row("session_java_info", "session_id", $_GET["id_val"]);
	    remove_row("session_flash_info", "session_id", $_GET["id_val"]);
	}
?>