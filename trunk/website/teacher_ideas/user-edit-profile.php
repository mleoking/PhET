<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class EditProfile extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        contributor_print_full_edit_form($this->user["contributor_id"], $this->user["contributor_id"], "user-update-profile.php");
    }

}

$page = new EditProfile("Edit Profile", NAV_TEACHER_IDEAS, get_referrer(), SP_AUTHLEVEL_USER);
$page->update();
$page->render();

?>