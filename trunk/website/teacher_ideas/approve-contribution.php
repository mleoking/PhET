<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class ApproveContributionPage extends SitePage {

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
        contribution_set_approved($contribution_id, true);
        cache_clear_teacher_ideas();
        cache_clear_simulations();
        $this->meta_refresh($this->referrer, 2);
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
        <h2>Success</h2>
        <p>The contribution has been marked as approved.</p>

EOT;
    }

}

$page = new ApproveContributionPage("Approve Contribtuion", NAV_TEACHER_IDEAS, get_referrer(SITE_ROOT."teacher_ideas/manage-contributions.php"), AUTHLEVEL_TEAM);
$page->update();
$page->render();

?>