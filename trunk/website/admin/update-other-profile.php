<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class UpadteOtherProfilePage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Assumes success
        db_update_table('contributor', gather_script_params_into_array('contributor_'), 'contributor_id', $_REQUEST['contributor_id']);
        $this->meta_refresh(SITE_ROOT."admin/manage-contributors.php", 2);
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }
        
        print("<p>The user's profile has been successfully updated!</p>");
    }
}

$page = new UpadteOtherProfilePage("Update Profile", NAV_ADMIN, null, SP_AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>