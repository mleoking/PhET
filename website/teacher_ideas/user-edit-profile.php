<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class EditProfile extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $username = auth_get_username();
        $contributor = contributor_get_contributor_by_username($username);
        $contributor_id = $contributor["contributor_id"];

        $editor_contributor_id = contributor_get_id_from_contributor_username(auth_get_username());
        contributor_print_full_edit_form($editor_contributor_id, $contributor_id, "user-update-profile.php");
    }

}

$page = new EditProfile("Edit Profile", NAV_TEACHER_IDEAS, get_referrer(), SP_AUTHLEVEL_USER);
$page->update();
$page->render();

?>