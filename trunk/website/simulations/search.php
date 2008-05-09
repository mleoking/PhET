<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/research-utils.php");

class SearchPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->search_for = "";
        $this->sims = array();
        $this->contribs = array();
        $this->researches = array();

        if (isset($_REQUEST['search_for'])) {
            $this->search_for = trim($_REQUEST['search_for']);
            if (strlen($this->search_for) == 0) {
                return;
            }

            $this->sims       = sim_search_for_sims($this->search_for);
            $this->contribs   = contribution_search_for_contributions($this->search_for);
            $this->researches = research_search_for($this->search_for);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $number_results = count($this->sims) + count($this->contribs) + count($this->researches);
        if (!strcmp("", $this->search_for)) {
            print "<p>No search term specified</p>\n";
            return;
        }
        else {
            print "<p>{$number_results} search results matching <strong>{$this->search_for}</strong></p>\n";
        }
        print "<div id=\"searchresults\">";
        print "<h2>Simulations</h2>";

        if ((!isset($this->sims)) || (count($this->sims) == 0)) {
            print "<p>No simulations were found meeting the specified criteria.</p>";
        }
        else {
            print "<ul>";

            foreach($this->sims as $sim) {
                $sim_id = $sim['sim_id'];
                $sim_name = format_string_for_html($sim['sim_name']);

                $sim_url = sim_get_url_to_sim_page($sim_id);

                print <<<EOT
                    <li><a href="$sim_url">$sim_name</a></li

EOT;
            }

            print "</ul>";
        }

        print "<h2>Contributions</h2>";

        if ((!isset($this->contribs) || count($this->contribs) == 0)) {
            print "<p>No contributions were found meeting the specified criteria.</p>";
        }
        else {
            print "<ul>";

            foreach($this->contribs as $contrib) {
                $contribution_id = $contrib['contribution_id'];
                $contribution_title = format_string_for_html($contrib['contribution_title']);
                print <<<EOT
                    <li><a href="{$this->prefix}teacher_ideas/view-contribution.php?contribution_id=$contribution_id&amp;referrer={$this->referrer}">$contribution_title</a></li>

EOT;
            }

            print "</ul>";
        }

        print "<h2>Research</h2>";

        if ((!isset($this->researches)) || (count($this->researches) == 0)) {
            print "<p>No research items were found meeting the specified criteria.</p>";
        }
        else {
            print "<ul>";

            foreach($this->researches as $research) {
                print "<li>";

                research_print($research['research_id']);

                print "</li>";
            }

            print "</ul>";
        }

        print <<<EOT
            </div>

            <p><a href="{$this->referrer}">back</a></p>

EOT;
    }

}

$page = new SearchPage("Search results", NAV_NOT_SPECIFIED, get_referrer(), AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>