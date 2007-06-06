<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."teacher_ideas/user-login.php");
    
    function print_manage_contributions() {
        global $contributor_id, $contributor_is_team_member;
        
        print "<h1>Manage Contributions</h1>";
        
        $contributions = contribution_get_manageable_contributions_for_contributor_id($contributor_id);
        
        print "<ul>";
        
        foreach($contributions as $contribution) {
            contribution_print_summary($contribution, $contributor_id, $contributor_is_team_member,
                SITE_ROOT."teacher_ideas/manage-contributions.php"
            );
        }
        
        print "</ul>";
    }
    
    print_site_page('print_manage_contributions', 3);

?>