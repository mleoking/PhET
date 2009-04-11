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

        // Get the request version.  Currently we're at version 1,
        // refactor this if we change versions.
        if ((!isset($_REQUEST['request_version'])) ||
            ($_REQUEST['request_version'] != 1)) {
            $this->error = "Invalid request version number";
            return false;
        }

        // Make sure the query strings give the info we need
        if (!isset($_REQUEST['project']) || !isset($_REQUEST['sim'])) {
            $this->error = "Make sure that query terms 'project' and 'sim' are properly specificed.'";
            return false;
        }

        // Get the sim
        try {
            SimFactory::inst()->enableTestSims();
            $this->sim = 
                SimFactory::inst()->getByProjectAndSimName(
                    $_REQUEST['project'],
                    $_REQUEST['sim'],
                    FALSE);
            $this->header_redirect($this->sim->getPageUrl());
        }
        catch (PhetSimException $e) {
            $this->error = "Cannot find a simulation matching project='{$_REQUEST['project']}' sim='{$_REQUEST['sim']}'";
            return false;
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
        else {
            // One sim found, but if we're here the redirect failed
            print "<h2>Simulation Redirect</h2>\n";
            print "<p>Automatic redirection failed, please go to <a href=\"{$this->sim->getPageUrl()}\">{$this->sim->getName()}</a></p>\n";
        }
    }
}

$page = new RedirectSimulationPage("Redirect Simulation", NavBar::NAV_NOT_SPECIFIED, null, SitePage::AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>