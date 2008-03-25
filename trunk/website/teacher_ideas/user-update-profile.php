<?php

include_once("../admin/BasePage.php");

class UpdateUserProfile extends BasePage {
    function update() {
        $username = auth_get_username();
        $contributor_id = contributor_get_id_from_contributor_email($username);

        db_update_table('contributor', gather_script_params_into_array('contributor_'), 'contributor_id', $contributor_id);

        $this->meta_refresh("../teacher_ideas/user-edit-profile.php", 3);
    }

    function render_content() {
        print <<<EOT
        <p>Your profile has been successfully updated!</p>

EOT;
    }
}

auth_do_validation();
$page = new UpdateUserProfile(3, '', "Update User Profile");
$page->update();
$page->render();

?>