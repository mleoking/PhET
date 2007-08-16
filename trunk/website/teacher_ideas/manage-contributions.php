<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."teacher_ideas/user-login.php");

	function print_contributions($contributions, $heading) {
		global $contributor_id, $contributor_is_team_member;
		
		if (count($contributions) > 0) {
			print "<h2>$heading</h2>";
        
        	print "<ul>";
        
	        foreach($contributions as $contribution) {
	            contribution_print_summary($contribution, $contributor_id, $contributor_is_team_member,
	                SITE_ROOT."teacher_ideas/manage-contributions.php"
	            );
	        }
        
	        print "</ul>";
		}
	}
    
    function print_manage_contributions() {
        global $contributor_id, $contributor_is_team_member;
        
        print "<h1>Manage Contributions</h1>";

		$contributions = contribution_get_contributions_for_contributor_id($contributor_id);		
		print_contributions($contributions, "My Contributions");

		$contributions = contribution_get_coauthored_contributions_for_contributor_id($contributor_id);
		print_contributions($contributions, "Coauthored Contributions");
		
		$contributions = contribution_get_other_manageable_contributions_for_contributor_id($contributor_id);
		print_contributions($contributions, "Other Contributions");
    }
    
    print_site_page('print_manage_contributions', 3);

?>