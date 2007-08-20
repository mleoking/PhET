<?php
	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/authentication.php");
	include_once(SITE_ROOT."admin/contrib-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	include_once(SITE_ROOT."admin/web-utils.php");
	
	do_authentication(false);
 
	function subscribe_user() {
		if (isset($_REQUEST['contributor_email'])) {
			$contributor_email = $_REQUEST['contributor_email'];
		
			$contributor = contributor_get_contributor_by_email($contributor_email);
		
			if (!$contributor) {
				$contributor_id = contributor_add_new_contributor($contributor_email, "");

				$contributor = contributor_get_contributor_by_id($contributor_id);
			
				// Fill in organization, name, desc, if present:
				if (isset($_REQUEST['contributor_organization'])) {
					$contributor['contributor_organization'] = $_REQUEST['contributor_organization'];
				}
				if (isset($_REQUEST['contributor_name'])) {
					$contributor['contributor_name'] = $_REQUEST['contributor_name'];
				}
				if (isset($_REQUEST['contributor_desc'])) {
					$contributor['contributor_desc'] = $_REQUEST['contributor_desc'];
				}
			}
		
			$contributor['contributor_receive_email'] = '1';
		
			contributor_update_contributor($contributor['contributor_id'], $contributor);
		}
	}
	
	function print_content() {
		eval(get_code_to_create_variables_from_array(gather_globals_into_array("contributor_")));
		
		if (isset($_REQUEST['contributor_email'])) {
			$subscribing = true;
		}
		else {
			$subscribing = false;
		}
		
		if (isset($contributor_receive_email) && $contributor_receive_email == 1) {
			print <<<EOT
				<h1>Subscribe to PhET</h1>
				
				<p>
					$contributor_name: you are already subscribed to the PhET newsletter. To unsubscribe, <a href="../teacher_ideas/user-edit-profile.php">edit your profile</a> and uncheck the box marked 'Receive PhET Newsletter'.
				</p>
EOT;
		}
		else if ($subscribing) {
			subscribe_user();
			
			print <<<EOT
				<h1>Subscription Successful!</h1>
				
				<p>
					Thank you, $contributor_name, for subscribing to the PhET newsletter! 
				</p>
				
				<p>
					<a href="../index.php">Home</a>
				</p>
EOT;
		}
		else {
			print <<<EOT
				<h1>Subscribe to PhET</h1>
				
				<p>
					The PhET newsletter contains information on major updates to the simulations, and is issued <strong>four times per year</strong>.
					You may unsubscribe at any time.
				</p>
EOT;
			print_new_account_form("subscribe-newsletter.php", "Subscribe", false);
		}
	}
	
	print_site_page('print_content', 3);
?>