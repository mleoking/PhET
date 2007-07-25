<?php

	include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/web-utils.php");

	include_once("user-login.php");
	
	force_redirect($_REQUEST['url'], 0);
?>