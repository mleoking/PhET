<?php

include_once("../admin/BasePage.php");

class DeleteContributionPage extends BasePage {

    function update() {
        $username = auth_get_username();
        $contributor_id = contributor_get_id_from_contributor_email($username);

        $this->meta_refresh($this->referrer, 2);
        $this->delete_success = false;
        if (isset($_REQUEST['contribution_id'])) {
            $contribution_id = $_REQUEST['contribution_id'];

            if (contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {
                contribution_delete_contribution($contribution_id);
                $this->delete_success = true;
            }
        }
    }

    function render_content() {
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

auth_do_validation();
$page = new DeleteContributionPage(3, get_referrer("../teacher_ideas/manage-contributions.php"), "Delete Contribution");
$page->update();
$page->render();

?>