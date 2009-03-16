<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class RedirectSimulationPage extends SitePage {

    private $error = '';

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->simulation = null;

        // Get the request version.  Currently we're at version 1, refactor this if we
        // change versions.
        if ((!isset($_REQUEST['request_version'])) || 
            ($_REQUEST['request_version'] != 1)) {
            $this->error = "Invalid request version number";
            return false;
        }

        // Try dirname and flavorname
        if (!isset($_REQUEST['project']) || !isset($_REQUEST['sim'])) {
            $this->error = "Make sure that query terms 'dirname' and 'flavorname' are properly specificed.'";
            return false;
        }

        // Get the sims that match the specificed dirname and flavorname
        $cond = array('sim_dirname' => $_REQUEST['project'], 'sim_flavorname' => $_REQUEST['sim']);
        $this->sims = db_get_rows_by_condition('simulation', $cond, false, false);
        if (count($this->sims) == 0) {
            $this->error = "Cannot find a simulation matching project='{$_REQUEST['project']}' sim='{$_REQUEST['sim']}'";
            return false;
        }
        else if (count($this->sims) == 1) {
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

        if (!empty($this->error)) {
            // No sims variable found, problem with the parameters
            print "<h2>Error</h2>\n";
            print "<p>{$this->error}</p>";
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