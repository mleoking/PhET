<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class AddComment extends SitePage {
    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $contributor = contributor_get_contributor_by_username(auth_get_username());
        $contributor_id = $contributor["contributor_id"];
        if (isset($_REQUEST['contribution_id']) && isset($contributor_id) && isset($_REQUEST['contribution_comment_text'])) {
            if (!isset($_REQUEST['contribution_id']) ||
                !isset($_REQUEST['contribution_comment_text'])) {
                return false;
            }

            $contribution_id = $_REQUEST['contribution_id'];
            $contribution_comment_text = $_REQUEST['contribution_comment_text'];

            $this->id = contribution_add_comment($contribution_id, $contributor_id, $contribution_comment_text);

            cache_clear_teacher_ideas();

            $this->meta_refresh($this->referrer, 0);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }
        if (!isset($this->id)) {
            print "<p>There was an error.  Please go back and try again.</p>\n";
        }
        else {
            BasePage::render_redirect();
        }
    }

}

$page = new AddComment("Add Comment", NAV_TEACHER_IDEAS, get_referrer(), SP_AUTHLEVEL_USER);
$page->update();
$page->render();

?>