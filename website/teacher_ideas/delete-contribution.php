<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class DeleteContributionPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $username = auth_get_username();
        $contributor_id = contributor_get_id_from_contributor_username($username);

        $this->meta_refresh($this->referrer, 2);
        $this->delete_success = false;
        if (isset($_REQUEST['contribution_id'])) {
            $contribution_id = $_REQUEST['contribution_id'];

            if (contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {
                contribution_delete_contribution($contribution_id);
                $this->delete_success = true;
                cache_clear_teacher_ideas();
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

$page = new DeleteContributionPage("Delete Contribution", NAV_TEACHER_IDEAS, get_referrer("../teacher_ideas/manage-contributions.php"), SP_AUTHLEVEL_USER);
$page->update();
$page->render();

?>