<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/referrer.php");

class UnapproveContributionPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Validate the variables we want
        $validation = array("contribution_id" => VT_VALID_CONTRIBUTION_ID);
        $valid = $this->validate_array($_REQUEST, $validation);
        if (!$valid) {
            return false;
        }

        $contribution_id = $_REQUEST["contribution_id"];
        contribution_set_approved($contribution_id, false);
        cache_clear_teacher_ideas();
        cache_clear_simulations();
        $this->meta_refresh($this->referrer, 3);
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
        <h2>Success</h2>
        <p>The contribution has been marked as unapproved.</p>

EOT;
    }

}

$page = new UnapproveContributionPage("Unapprove Contribtuion", NAV_TEACHER_IDEAS, get_referrer(contribution_url_to_view_from_uri()), AUTHLEVEL_TEAM);
$page->update();
$page->render();

?>