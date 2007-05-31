<?php
    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    
    include_once("../teacher_ideas/referrer.php"); 
    
    function print_content() {
        global $sims, $contribs, $referrer;
        
        print "<div id=\"searchresults\">";
        print "<h1>Simulations</h1>";

        if (count($sims) == 0) {
            print "<p>No simulations were found meeting the specified criteria.</p>";
        }
        else {
            print "<ul>";
            
            foreach($sims as $sim) {
                eval(get_code_to_create_variables_from_array($sim));
            
                print <<<EOT
                    <li><a href="../simulations/sims.php?sim_id=$sim_id">$sim_name</a></li>
EOT;
            }
        
            print "</ul>";
        }
        
        print "<h1>Contributions</h1>";
        
        if (count($contribs) == 0) {
            print "<p>No contributions were found meeting the specified criteria.</p>";
        }
        else {
            print "<ul>";
        
            foreach($contribs as $contrib) {
                eval(get_code_to_create_variables_from_array($contrib));
            
                print <<<EOT
                    <li><a href="../teacher_ideas/view-contribution.php?contribution_id=$contribution_id&amp;referrer=$referrer">$contribution_title</a></li>
EOT;
            }
        
            print "</ul>";        
        }
        
        print "</div>";
    } 

    if (isset($_REQUEST['search_for'])) {
        $search_for = $_REQUEST['search_for'];
        
        $sims     = sim_search_for_sims($search_for);
        $contribs = contribution_search_for_contributions($search_for);

        print_site_page('print_content', -1);        
    }
    else {
        force_redirect($referrer, 0);
    }

?>