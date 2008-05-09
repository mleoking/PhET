<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class AddCommentPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $contributor_id = $this->user["contributor_id"];
        if (isset($_REQUEST['contribution_id']) && isset($contributor_id) && isset($_REQUEST['contribution_comment_text'])) {
            if (!isset($_REQUEST['contribution_id']) ||
                !isset($_REQUEST['contribution_comment_text'])) {
                return false;
            }

            $contribution_id = $_REQUEST['contribution_id'];
            $contribution_comment_text = $_REQUEST['contribution_comment_text'];

            $this->id = contribution_add_comment($contribution_id, $contributor_id, $contribution_comment_text);

            cache_clear_teacher_ideas();

            $this->meta_refresh($this->referrer, 3);
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
            print <<<EOT
            <h2>Success</h2>
            <p>You have successfully updated the comment.</p>
            <p>You will be redirected <a href="{$this->referrer}">here</a> in 3 seconds.</p>

EOT;
            return true;
        }
    }

}

$page = new AddCommentPage("Add Comment", NAV_TEACHER_IDEAS, get_referrer(), AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>