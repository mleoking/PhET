<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");       

    function print_content() {
        global $contribution_id, $referrer;
        
        $contribution = contribution_get_contribution_by_id($contribution_id);
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        print "<h2>$contribution_title - $contribution_type</h2>";
        print "<p><em>$contribution_keywords</em></p>";
        print "<p>$contribution_desc</p>";
        print "<br/>";
        
        print "<p><a href=\"$referrer\">back</a></p>";
    }
    
    $contribution_id = $_REQUEST['contribution_id'];
    
    if (isset($_REQUEST['referrer'])) {
        $referrer = $_REQUEST['referrer'];
    }
    else {
        $referrer = SITE_ROOT.'teacher_ideas/manage-contributions.php';
    }
    
    print_site_page('print_content', 3);

?>