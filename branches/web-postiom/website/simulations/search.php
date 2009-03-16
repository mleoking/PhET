<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");
require_once("include/research-utils.php");
require_once("teacher_ideas/referrer.php");

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

            $this->sims       = SimUtils::inst()->searchForSims($this->search_for);
            $this->contribs   = contribution_search_for_contributions($this->search_for);
            $this->researches = research_search_for($this->search_for);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $html_referrer = WebUtils::inst()->toHtml($this->referrer);

        $number_results = count($this->sims) + count($this->contribs) + count($this->researches);
        if (!strcmp("", $this->search_for)) {
            print "<p>No search term specified</p>\n";
            return;
        }
        else {
            print "<p>{$number_results} search results matching <strong>{$this->search_for}</strong></p>\n";
        }
        print "<div id=\"searchresults\">\n";
        print "<h2>Simulations</h2>\n";

        if ((!isset($this->sims)) || (count($this->sims) == 0)) {
            print "<p>No simulations were found meeting the specified criteria.</p>\n";
        }
        else {
            print "<ul>\n";

            foreach($this->sims as $sim) {
                $simm = SimFactory::inst()->getById($sim['sim_id']);

                print <<<EOT
                    <li><a href="{$simm->getPageUrl()}">{$simm->getName()}</a></li>

EOT;
            }

            print "</ul>";
        }

        print "<h2>Contributions</h2>\n";

        if ((!isset($this->contribs) || count($this->contribs) == 0)) {
            print "<p>No contributions were found meeting the specified criteria.</p>\n";
        }
        else {
            print "<ul>\n";

            foreach($this->contribs as $contrib) {
                $contribution_id = $contrib['contribution_id'];
                $contribution_title = WebUtils::inst()->toHtml($contrib['contribution_title']);
                print <<<EOT
                    <li><a href="{$this->prefix}teacher_ideas/view-contribution.php?contribution_id=$contribution_id">$contribution_title</a></li>

EOT;
            }

            print "</ul>\n";
        }

        print "<h2>Research</h2>\n";

        if ((!isset($this->researches)) || (count($this->researches) == 0)) {
            print "<p>No research items were found meeting the specified criteria.</p>\n";
        }
        else {
            print "<ul>\n";

            foreach($this->researches as $research) {
                print "<li>";

                research_print($research['research_id']);

                print "</li>\n";
            }

            print "</ul>\n";
        }

        print <<<EOT
            </div>

            <p><a href="{$html_referrer}">back</a></p>

EOT;
    }

}

$page = new SearchPage("Search results", NAV_NOT_SPECIFIED, get_referrer(), AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>