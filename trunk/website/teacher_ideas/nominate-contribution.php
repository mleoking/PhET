<?php

    include_once("../admin/global.php");

	$custom_title = "Nominate Contribution - Login/Register";

    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/nominate-utils.php");
    include_once(SITE_ROOT."teacher_ideas/referrer.php");
    include_once(SITE_ROOT."teacher_ideas/user-login.php");

	$contribution_id = $_REQUEST['contribution_id'];
	$contribution_nomination_desc = $_REQUEST['contribution_nomination_desc']

	function print_nomination_success() {
		global $contribution_id;
		
		$contribution = contribution_get_contribution_by_id($contribution_id);
		
		eval(get_code_to_create_variables_from_array($contribution));
		
		print <<<EOT
			<p>Thank you for nominating &quot;$contribution_title&quot; as a Gold Star contribution.</p>
			
			<p>If PhET determines the contribution meets Gold Star criteria, the contribution will receive a Gold Star.</p>
EOT;
	}
	
	nominate_contribution($contribution_id, $contribution_nomination_desc);
	
	print_site_page('print_nomination_success', 3, "view-contribution.php?contribution_id=$contribution_id", 2);

?>