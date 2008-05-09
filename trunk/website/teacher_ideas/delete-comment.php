<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class DeleteCommentPage extends SitePage {

    // FIXME: this is duplicated in edit-comment.php, refactor
    function user_can_edit_comment($comment) {
        if ($this->user["contributor_is_team_member"]) {
            // PhET Team members can edit anything
            return true;
        }

        if ($comment["contributor_id"] == $this->user["contributor_id"]) {
            // Contributors can edit their own comments
            return true;
        }

        return false;
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $validation = array("comment_id" => VT_VALID_COMMENT_ID);
        $valid = $this->validate_array($_REQUEST, $validation);
        if (!$valid) {
            return false;
        }

        $comment_id = $_REQUEST["comment_id"];

        $this->deleted = false;

        // Check if there was a submition to change the comment
        if (isset($_REQUEST["submit_delete_comment"])) {
            contribution_delete_comment($comment_id);
            $this->deleted = true;
            cache_clear_teacher_ideas();
            $this->meta_refresh($this->referrer, 3);
        }
        else {
            $this->comment = comment_get_comment_by_id($comment_id);
            $this->comment_contributor = contributor_get_contributor_by_id($this->comment["contributor_id"]);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if ($this->deleted) {
            print <<<EOT
            <h2>Success</h2>
            <p>The comment has been deleted.</p>
            <p>You will be redirected <a href="{$this->referrer}">here</a> in 3 seconds.</p>

EOT;
            return true;
        }

        if ($this->user_can_edit_comment($this->comment)) {
            $comment_contributor = format_string_for_html($this->comment_contributor["contributor_name"]);
            $comment_text = format_string_for_html($this->comment["contribution_comment_text"]);

            print <<<EOT
            <h2>Please Confirm</h2>
            <form method="post" action="delete-comment.php?referrer={$this->referrer}">
                <p>Do you <em>really</em> want to delete this comment?</p>
                <p>
                    Comment by <em>{$comment_contributor}</em>:<br />
                    {$comment_text}
                </p>
                <p>
                    <input type="hidden" name="comment_id" value="{$this->comment["contribution_comment_id"]}" />
                    <input type="submit" name="submit_delete_comment" value="Delete Comment" />
                </p>
            </form>

EOT;
        }
        else {
            print <<<EOT
            <p>You do not have permission to delete this comment</p>

EOT;
        }
    }

}

$page = new DeleteCommentPage("Delete Comment", NAV_TEACHER_IDEAS, get_referrer(), AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>