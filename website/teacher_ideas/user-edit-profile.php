<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class EditProfilePage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        contributor_print_full_edit_form($this->user["contributor_id"], $this->user["contributor_id"], "user-update-profile.php");
    }

}

$page = new EditProfilePage("Edit Profile", NAV_TEACHER_IDEAS, null, AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>