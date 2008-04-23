<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

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
        cache_clear(BROWSE_CACHE);
        $this->meta_refresh($this->referrer, 2);
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

$page = new UnapproveContributionPage("Unapprove Contribtuion", NAV_TEACHER_IDEAS, get_referrer("../teacher_ideas/manage-contributions.php"), SP_AUTHLEVEL_TEAM);
$page->update();
$page->render();

?>