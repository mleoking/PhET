<?php
    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/research-utils.php");

    include_once("../teacher_ideas/referrer.php");

    function print_content() {
        global $sims, $contribs, $researches, $referrer;

        print "<div id=\"searchresults\">";
        print "<h1>Simulations</h1>";

        if (count($sims) == 0) {
            print "<p>No simulations were found meeting the specified criteria.</p>";
        }
        else {
            print "<ul>";

            foreach($sims as $sim) {
                $sim_id = $sim['sim_id'];
                $sim_name = format_string_for_html($sim['sim_name']);

                $sim_url = sim_get_url_to_sim_page($sim_id);

                print <<<EOT
                    <li><a href="$sim_url">$sim_name</a></li>
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
                $contribution_id = $contrib['contribution_id'];
                $contribution_title = format_string_for_html($contrib['contribution_title']);
                print <<<EOT
                    <li><a href="../teacher_ideas/view-contribution.php?contribution_id=$contribution_id&amp;referrer=$referrer">$contribution_title</a></li>
EOT;
            }

            print "</ul>";
        }

        print "<h1>Research</h1>";

        if (count($researches) == 0) {
            print "<p>No research items were found meeting the specified criteria.</p>";
        }
        else {
            print "<ul>";

            foreach($researches as $research) {
                print "<li>";

                research_print($research['research_id']);

                print "</li>";
            }

            print "</ul>";
        }

        print <<<EOT
            </div>

            <p><a href="$referrer">back</a></p>
EOT;
    }

    if (isset($_REQUEST['search_for'])) {
        $search_for = $_REQUEST['search_for'];

        $sims       = sim_search_for_sims($search_for);
        $contribs   = contribution_search_for_contributions($search_for);
        $researches = research_search_for($search_for);

        print_site_page('print_content', -1);
    }
    else {
        force_redirect($referrer, 0);
    }

?>