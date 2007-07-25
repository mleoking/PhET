<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/web-utils.php");
    
    cookie_var_clear("username");
    cookie_var_clear("password_hash");

	if (isset($_REQUEST['url'])) {
		$url = $_REQUEST['url'];
	}
	else {
		$url = SITE_ROOT.'teacher_ideas/user-edit-profile.php';
	}
    
    force_redirect($url, 0);
?>