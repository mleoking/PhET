<?php

include_once("../admin/BasePage.php");

class EditProfile extends BasePage {

    function render_content() {
        if (!auth_user_validated()) {
            $intro_text = "<p>You must be logged in to edit your profile</p>";

            print_login_and_new_account_form("manage-contributions.php", "manage-contributions.php", $this->referrer, $intro_text);
        }
        else {
            $username = auth_get_username();
            $contributor = contributor_get_contributor_by_username($username);
            $contributor_id = $contributor["contributor_id"];

            contributor_print_full_edit_form($contributor_id, "user-update-profile.php");
        }
    }

}

auth_do_validation();
$page = new EditProfile(3, get_referrer(), "Edit Profile");
$page->update();
$page->render();

?>