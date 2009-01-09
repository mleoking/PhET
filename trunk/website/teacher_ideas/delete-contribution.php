<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class DeleteContributionPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $contributor_id = $this->user["contributor_id"];

        $this->meta_refresh($this->referrer, 2);
        $this->delete_success = false;
        if (isset($_REQUEST['contribution_id'])) {
            $contribution_id = $_REQUEST['contribution_id'];

            if (contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {
                contribution_delete_contribution($contribution_id);
                $this->delete_success = true;
                cache_clear_teacher_ideas();
                cache_clear_simulations();
            }
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if ($this->delete_success) {
            print <<<EOT
        <p>The contribution has been successfully deleted.</p>

EOT;
        }
        else {
            print <<<EOT
        <p>The contribution could not be deleted because it does not exist or you do not have permission to delete it.</p>

EOT;
        }
    }

}

$page = new DeleteContributionPage("Delete Contribution", NAV_TEACHER_IDEAS, get_referrer(SITE_ROOT."teacher_ideas/manage-contributions.php"), AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>