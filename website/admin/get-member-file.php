<?php

	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/web-utils.php");	
	include_once(SITE_ROOT."admin/sys-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");	
	include_once(SITE_ROOT."admin/authentication.php");
	
	do_authentication(true);
	
	$self     = get_self_url();
	$file     = $_REQUEST['file'];
	$download = isset($_REQUEST['download']) && $_REQUEST['download'] == '1';
	
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
		send_file_to_browser($file, null, null, "attachment");
	}
	else {
		print_site_page('print_content', -1, $self."&amp;download=1", 1);
	}
?>