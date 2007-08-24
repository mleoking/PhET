<?php
	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/db-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	
	function print_content() {
		print <<<EOT
			<h1>Form Validation Testing</h1>
			
			<form>
				<table class="form">
					<tr>
						<td>email</td>    <td><input type="text" size="20" name="contributor_email" value='' id="contributor_email_uid" title="You must enter a valid email address"/></td>
					</tr>
					
					<tr>
						<td>name</td>    <td><input type="text" size="20" name="contributor_name" id="contributor_name_uid" value='' title="You must enter your full name" /></td>
					</tr>

					<tr>
						<td>password</td>    <td><input type="text" size="20" name="contributor_password" id="contributor_password_uid" value='' title="You must enter your password" /></td>
					</tr>

					<tr>
						<td>organization</td>    <td><input type="text" size="20" name="contributor_organization" id="contributor_organization_uid" value='' title="You must enter your organization or company" /></td>
					</tr>
					
					<tr>
						<td colspan="2">
							<input type="submit" name="submit" value="Enter" />
						</td>
					</tr>
				</table>
			</form>
EOT;
	}
	
    print_site_page('print_content', -1);
?>