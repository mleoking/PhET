<?php
	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/contrib-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	include_once(SITE_ROOT."admin/web-utils.php");
 
	if (isset($_REQUEST['contributor_email'])) {
		$contributor_email = $_REQUEST['contributor_email'];
		
		$contributor = contributor_get_contributor_by_email($contributor_email);
		
		if (!$contributor) {
			$contributor_id = contributor_add_new_contributor($contributor_email, "");

			$contributor = contributor_get_contributor_by_id($contributor_id);
		}
		
		$contributor['contributor_receive_email'] = '1';
		
		contributor_update_contributor($contributor['contributor_id'], $contributor);
	}
	
	$referrer = $_REQUEST['referrer'];
	
	force_redirect($referrer."?subscribed=true");
?>