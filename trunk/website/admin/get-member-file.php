<?php

	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/web-utils.php");	

	$self     = get_self_url();
	$file     = urldecode($_REQUEST['file']);
	$download = isset($_REQUEST['download']) && $_REQUEST['download'] == '1';
	
	$custom_title = "Download File";
	$custom_body  = <<<EOT
		<p>Before downloading this file, please tell us a bit about yourself. Providing this information will help PhET retain the support of its financial sponsors.</p>
		<p>Your email will not be shared with anyone. We'll use your email to send you up to four newsletters per year, which describe major updates to the simulations. You may unsubscribe at any time.</p>
		<p>Please login with your existing account information, or create a new account. If you do not wish to help PhET, you may skip the registration process and <a href="$file">download the file directly</a>.</p>
EOT;

	include_once(SITE_ROOT."admin/authentication.php");

	include_once(SITE_ROOT."admin/sys-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");	
	include_once(SITE_ROOT."admin/db-utils.php");		
	
	do_authentication(true);
	
	function print_content() {
		global $file;
		
		$name = basename($file);
		
		print <<<EOT
			<h1>Downloading File</h1>
			
			<p>Your download of the file "$name" will begin shortly.</p>
			
			<p>If you encounter difficulty, you can try <a href="$file">downloading the file directly</a>.</p>
EOT;
	}
	
	if ($download) {
		// Keep track of download statistics:
		db_insert_row(
			'download_statistics', 
			array(
				'download_statistics_file' => $file,
				'contributor_id'           => $contributor_id
			)
		);		
		
		send_file_to_browser($file, null, null, "attachment");
	}
	else {
		print_site_page('print_content', -1, $self."&amp;download=1", 1);
	}
?>