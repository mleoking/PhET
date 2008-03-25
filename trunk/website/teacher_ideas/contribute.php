<?php

include_once("../admin/global.php");
include_once("../admin/site-utils.php");
include_once("../admin/contrib-utils.php");

include_once("../admin/BasePage.php");

class ContributePage extends BasePage {
    function update() {
        ob_start();
        if (!auth_user_validated()) {
            print_login_and_new_account_form("contribute.php", "contribute.php", $this->referrer);
        }

        contribution_print_full_edit_form(-1, '../teacher_ideas/edit-contribution.php', '../teacher_ideas/edit-contribution.php', 'Submit', $this);
        $this->add_content(ob_get_clean());
        return true;
    }
}

auth_do_validation();
$page = new ContributePage(3, get_referrer(), "Submit an Activity");
$page->update();
$page->render();

?>

