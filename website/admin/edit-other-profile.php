<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class EditOtherProfilePage extends SitePage {
    function update() {
        $user_authenticated = parent::update();
        if (!$user_authenticated) {
            return;
        }

        if (!isset($_REQUEST['edit_contributor_id'])) {
            return;
        }

        $this->contributor_to_edit = $_REQUEST['edit_contributor_id'];
    }

    function render_content() {
        $user_authenticated = parent::render_content();
        if (!$user_authenticated) {
            return;
        }

        if (!isset($this->contributor_to_edit)) {
            print <<<EOT
            <h2>No profile specified</h2>

EOT;
            return;
        }

        $editor_contributor_id = contributor_get_id_from_contributor_username(auth_get_username());
        contributor_print_full_edit_form($editor_contributor_id, $this->contributor_to_edit, "../admin/update-other-profile.php", null, "<p>You may edit the profile of the user here.</p>");
    }
}

$page = new EditOtherProfilePage("Edit Other Profile", NAV_ADMIN, null, SP_AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>