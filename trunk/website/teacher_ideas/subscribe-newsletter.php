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
			
				// Fill in organization & name, if present:
				if (isset($_REQUEST['contributor_organization'])) {
					$contributor['contributor_organization'] = $_REQUEST['contributor_organization'];
				}
				if (isset($_REQUEST['contributor_name'])) {
					$contributor['contributor_name'] = $_REQUEST['contributor_name'];
				}
			}
		
			$contributor['contributor_receive_email'] = '1';
		
			contributor_update_contributor($contributor['contributor_id'], $contributor);
		}
	}
	
	function print_content() {
		if (isset($GLOBALS['contributor_email'])) {
			$contributor_email = $GLOBALS['contributor_email'];
			$contributor_name  = $GLOBALS['contributor_name'];
			$contributor_organization = $GLOBALS['contributor_organization'];
			$contributor_receive_email = $GLOBALS['contributor_receive_email'];
		}
		else {
			$contributor_email = '';
			$contributor_name  = '';
			$contributor_organization = '';
			$contributor_receive_email = 0;
		}
		
		if (isset($_REQUEST['contributor_email'])) {
			$subscribing = true;
			
			$name = $_REQUEST['contributor_name'];
		}
		else {
			$subscribing = false;
		}
		
		if ($subscribing) {
			subscribe_user();
			
			print <<<EOT
				<h1>Subscription Successful!</h1>
				
				<p>
					Thank you, $name, for subscribing to the PhET newsletter! 
				</p>
				
				<p>
					<a href="../index.php">Home</a>
				</p>
EOT;
		}
		else if ($contributor_receive_email) {
			print <<<EOT
				<h1>Subscribe to PhET</h1>
				
				<p>
					$contributor_name: you are already subscribed to the PhET newsletter. To unsubscribe, <a href="../teacher_ideas/user-edit-profile.php">edit your profile</a> and uncheck the box marked 'Receive PhET Newsletter'.
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
				
				<form method="post" action="subscribe-newsletter.php">
					<fieldset>
						<table>
							<tr>
								<td>email:</td>		<td><input id="contributor_email_uid" type="text" size="20" name="contributor_email" value="$contributor_email" onkeyup="javascript:on_email_change_guess_data();"/></td>
							</tr>
							
							<tr>
								<td>name:</td>		<td><input id="contributor_name_uid" type="text" size="20" name="contributor_name"  value="$contributor_name"/></td>
							</tr>
							
							<tr>
								<td>organization:</td> <td><input id="contributor_organization_uid" type="text" size="20" name="contributor_organization"  value="$contributor_organization"/></td>
							</tr>
							
							<tr>
								<td colspan="2"><input type="submit" name="submit" value="Subscribe" /></td>
							</tr>
						</table>
					</fieldset>
				</form>
EOT;
		}
	}
	
	print_site_page('print_content', 3);
?>