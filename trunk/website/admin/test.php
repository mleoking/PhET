<?php
	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/db-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	include_once(SITE_ROOT."admin/cache-utils.php");
	
	foreach (glob('../*/cached-*') as $dir) {
		print "Deleting $dir<br/>";
		
		exec("rm -rf $dir");
		flush();
	}
?>