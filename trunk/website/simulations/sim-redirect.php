<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class RedirectSimulationPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->simulation = null;
        // Try dirname and flavorname
        if (!isset($_REQUEST['project']) || !isset($_REQUEST['sim'])) {
            return false;
        }

        // Get the sims that match the specificed dirname and flavorname
        $cond = array('sim_dirname' => $_REQUEST['project'], 'sim_flavorname' => $_REQUEST['sim']);
        $this->sims = db_get_rows_by_condition('simulation', $cond, false, false);
        if (count($this->sims) == 1) {
            // If there is one match, do the redirect
            $sim_url = sim_get_url_to_sim_page_by_sim_name($this->sims[0]['sim_name']);
            $this->header_redirect($sim_url);
        }

        return true;
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (!isset($this->sims)) {
            // No sims variable found, problem with the parameters
            print "<h2>Error</h2>\n";
            print "<p>An error occurred, make sure that query terms 'dirname' and 'flavorname' are properly specificed.</p>";
        }
        else if (count($this->sims) == 0) {
            // No sims found
            print "<h2>Error: No match</h2>\n";
            print "<p>An error occurred, make sure that query terms 'dirname' and 'flavorname' are properly specificed.</p>";
        }
        else if (count($this->sims) == 1) {
            // One sim found, but if we're here the redirect failed
            print "<h2>Simulation Redirect</h2>\n";
            $sim_url = sim_get_url_to_sim_page_by_sim_name($this->sims[0]['sim_name']);
            print "<p>Automatic redirection failed, please go to <a href=\"{$sim_url}\">{$this->sims[0]['sim_name']}</a></p>\n";
        }
        else if (count($this->sims) > 1) {
            // Multiple sims found, give options
            print "<h2>Ambiguous redirect</h2>\n";
            print "<p>The parameters specified are not unique, choose from the following matches:</p>\n";
            print "<ul>\n";
            foreach ($this->sims as $sim) {
                $sim_url = sim_get_url_to_sim_page($sim['sim_id']);
                print "<li><a href=\"{$sim_url}\">{$sim['sim_name']}</a></li>";
            }
            print "</ul>\n";
        }
    }
}

$page = new RedirectSimulationPage("Redirect Simulation", NAV_NOT_SPECIFIED, null, AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>