<?php
	include_once("../admin/global.php");
	
	include_once(SITE_ROOT."admin/password-protection.php");
	include_once(SITE_ROOT."admin/contrib-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");

	function print_edit_profile_form() {
	    global $edit_contributor_id;
    
	    print "<h1>Edit Profile</h1>";
    
	    contributor_print_full_edit_form($edit_contributor_id, "../admin/update-other-profile.php", null, "<p>You may edit the profile of the user here.</p>");
	}
	
	$edit_contributor_id = $_REQUEST['edit_contributor_id'];

	print_site_page('print_edit_profile_form', 9);
?>