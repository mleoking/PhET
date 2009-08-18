<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/browse-utils.php");

class SimulationChangelogPage extends SitePage {

    const MAX_TITLE_CHARS = 80;

    function find_close_sims($sim_encoding) {
        $close_sims = array();
        $close_sims1 = SimFactory::inst()->getCloseWebEncodings($sim_encoding, 1);
        
        // Search the database, changing the underscores '_' to spaces ' ' and
        // removing small words that match too liberally
        $split_search = array();
        foreach (explode('_', $sim_encoding) as $term) {
            if (!in_array($term, array('and', 'or', 'in', 'as', 'of', 'as', 'the', 'a', 'on'))) {
                $split_search[] = $term;
            }
        }
        $close_sims2 = SimUtils::inst()->searchForSims(implode(' ', $split_search), TRUE);
        
        foreach ($close_sims1 as $sim) {
            $sim_id = $sim['sim_id'];
            if (isset($close_sims[$sim_id])) continue;
            if (!$sim['sim_is_real']) continue;
            $close_sims[$sim_id] = $sim;
        }

        foreach ($close_sims2 as $sim) {
            $sim_id = $sim['sim_id'];
            if (isset($close_sims[$sim_id])) continue;
            if (!$sim['sim_is_real']) continue;
            $close_sims[$sim_id] = $sim;
        }

        $this->close_sims = $close_sims;
        
        return $close_sims;
    }

    function get_sim_not_found_html() {
        $html = '';
        if (is_null($this->sim)) {
            $html .= "<div class=\"error-box\"><p>Specified simulation not found</p></div>";

            if (isset($this->close_sims) && (!empty($this->close_sims))) {
                $html .= "<h2>Perhaps you are looking for:</h2>\n";
                $html .= '<div class="productList" style="display: inline;">';
                foreach ($this->close_sims as $sim_id => $sim_info) {
                    $sim = SimFactory::inst()->getById($sim_id);
                $html .= "<div class=\"productEntry\">\n";

                $link_to_sim = "<a href=\"{$sim->getPageUrl()}\">";

                $sim_thumbnail_link = $sim->getThumbnailUrl();

                $html .= <<<EOT
                        <a href="{$sim->getPageUrl()}">
                            <img src="$sim_thumbnail_link"
                                 width="130"
                                 alt="Screenshot of {$sim->getName()} Simulation"
                                 title="Clear here to view the {$sim->getName()} simulation"
                             />
                        </a>

EOT;

                $html .= "<p>$link_to_sim{$sim->getName()}</a></p>\n";

                // Close product:
                $html .= "</div>\n";
                //                    print "<img src=\"{$sim->getThumbnailUrl()}\" /><strong><a href=\"{$sim->getPageUrl()}\">{$sim->getName()}</a></strong><br/>\n";
                }
                $html .= "</div>\n";
            }
            return $html;
        }
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->sim = null;
        if (!isset($_REQUEST['sim'])) {
            $this->set_title("No Simulation");
            return;
        }

        if (isset($_REQUEST['enable_test_sims']) && 
            $_REQUEST['enable_test_sims']) {
            SimFactory::inst()->enableTestSims();
        }

        // Where did we come frome?
        $this->set_navigation_referer();

        // If we're here, a sim was specified

        if ($_REQUEST['sim'] == 'all') {
            return true;
        }

        $sim_encoding = $_REQUEST['sim'];

        try {
            $this->sim = SimFactory::inst()->getByExactWebEncodedName($sim_encoding);
        }
        catch (PhetSimException $e) {
            $this->close_sims = $this->find_close_sims($sim_encoding);
            return;
        }

        return true;
    }

    private function render_changelog($sim) {
        print <<<EOT
<a href="{$sim->getPageUrl()}" title="Go to the simulation page for {$sim->getName()}"><img style="float: right;" src="{$sim->getThumbnailFilename()}" alt="thumbnail of {$sim->getName()}" /></a>
<h2><a href="{$sim->getPageUrl()}" title="Go to the simulation page for {$sim->getName()}">{$sim->getName()}</a></h2>
                           <a href="{$sim->getChangelogFilename()}">See the raw changelog (changes.txt) for {$sim->getName()}</a>

EOT;

        $web = WebUtils::inst();

        $log = $sim->getChangelog();
        $printed_items = false;

        print "<ul>\n";
        foreach ($log as $log_entry) {
            $comments = $log_entry['comments'];
            if (count($comments) == 0) {
                continue;
            }

            $date_version_info = $log_entry['version'];
            $version = $date_version_info[1];
            if ($version == '') {
                $version = 'N/A';
            }

            $date = $date_version_info[6];
            if ($date == '') {
                $date = 'N/A';
            }

            print <<<EOT
                <li><strong>{$web->toHtml($version)} ({$web->toHtml($date)})</strong><br />
                                          <ul>

EOT;

            foreach($comments as $comment) {
                $style = '';
                $printed_items = true;
                print '<li style="'.$style.'">';
                print WebUtils::inst()->toHtml($comment)."</li>\n";
            }

            print <<<EOT
            </ul></li>

EOT;
            }

            if (!$printed_items) {
                    print '<li>No changes available</li>';
            }

        print "</ul>\n";
                print <<<EOT
<div style="clear: both;"></div>
<hr />

EOT;
    }

    // TODO: separate out the update and render functions more.  No time now.
    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if ($_REQUEST['sim'] == 'all') {

            $sims = SimFactory::inst()->getAllSims(true);
            foreach ($sims as $sim) {
                if (!$sim->isReal()) continue;
                $this->render_changelog($sim);
            }
            return;
        }

        if (is_null($this->sim)) {
            print $this->get_sim_not_found_html();
            return;
        }

        $this->render_changelog($this->sim);


    }

}

$page = new SimulationChangelogPage("Simulation Changelog", NavBar::NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>