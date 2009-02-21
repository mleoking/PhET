<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class EditOtherProfilePage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return;
        }

        if (!isset($_REQUEST['edit_contributor_id'])) {
            return;
        }

        $this->contributor_to_edit = $_REQUEST['edit_contributor_id'];
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return;
        }

        if (!isset($this->contributor_to_edit)) {
            print <<<EOT
            <h2>No profile specified</h2>

EOT;
            return;
        }

        $editor_contributor_id = $this->user["contributor_id"];
        contributor_print_full_edit_form($editor_contributor_id, $this->contributor_to_edit, "{$this->prefix}admin/update-other-profile.php", null, "<p>You may edit the profile of the user here.</p>");
    }

}

$page = new EditOtherProfilePage("Edit Other Profile", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>