<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/referrer.php");

class ContributePage extends SitePage {

    function update() {
        // Intentional not returning if update returns false, we
        // want to display the login/new account panel AND the
        // contribution page fields, even though they will be
        // inactivated.  Look but don't touch.
        parent::update();

        if ($this->authentication_level < AUTHLEVEL_USER) {
            $this->add_javascript_header_script("disable_not_always_enabled_form_elements();");
        }

        ob_start();
        // This needs to be here for now, because the setup makes calls to add things to the header
        contribution_print_full_edit_form(-1, "{$this->prefix}teacher_ideas/edit-contribution.php", $this->referrer, 'Submit', $this);
        $this->add_content(ob_get_clean());
        return true;
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            BasePage::render_content();
            return $result;
        }
    }

}

$page = new ContributePage("Submit an Activity", NAV_TEACHER_IDEAS, get_referrer(SITE_ROOT.'teacher_ideas/manage-contributions.php'), AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>

