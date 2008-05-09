<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class UpadteOtherProfilePage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Work on password first
        // check on passwords matching,
        //   encrypt password in the $_REQUEST var, and
        //   use it if it is not changing
        $this->password_failure = false;
        $pass1 = (isset($_REQUEST["new_contributor_password"])) ? trim($_REQUEST["new_contributor_password"]) : "";
        $pass2 = (isset($_REQUEST["new_contributor_password2"])) ? trim($_REQUEST["new_contributor_password2"]) : "";

        if ((strlen($pass1) > 0) || (strlen($pass2) > 0)) {
            if ($pass1 == $pass2) {
                $pass1 = encrypt_password($_REQUEST["new_contributor_password"]);
            }
            else {
                $this->password_failure = true;
                return false;
            }
        }

        if (strlen($pass1) > 0) {
            $_REQUEST["contributor_password"] = $pass1;
        }
        else {
            if (isset($_REQUEST["contributor_password"])) {
                unset($_REQUEST["contributor_password"]);
            }

        }

        if (isset($_REQUEST["new_contributor_password"])) {
            unset($_REQUEST["new_contributor_password"]);
        }

        if (isset($_REQUEST["new_contributor_password2"])) {
            unset($_REQUEST["new_contributor_password2"]);
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

$page = new UpadteOtherProfilePage("Update Profile", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>