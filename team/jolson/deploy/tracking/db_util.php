
<?php
	function setup_mysql() {
		$link = mysql_connect("localhost", "www-data", "d3#r3m0nt$") or die(mysql_error());
		mysql_select_db("phet_stats_test_1") or die(mysql_error());
		return $link;
	}
?>

