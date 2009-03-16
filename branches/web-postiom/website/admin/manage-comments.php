<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class ManageCommentsPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $info_valid = $this->validate_array($_REQUEST, array("delete_comment_id" => VT_VALID_COMMENT_ID));
        if ($info_valid) {
            // An action has been specified, do it, then get to the rest
            $comment_id = $_REQUEST["delete_comment_id"];
            contribution_delete_comment($comment_id);
            $this->action_result_html = "The specified comment was deleted";
            $this->delete_success = true;
        }
        else if (isset($_REQUEST["delete_comment_id"])) {
            $this->action_result_html = "The specified comment id is invalid";
            $this->delete_success = false;
        }

        // force the info to be valid, it is optional
        // TODO: better way to handle this
        $this->valid_info = true;

        // Get all the comments
        $raw_comment_data = comment_get_all_comments();
        $this->comments_array_html = array();
        foreach ($raw_comment_data as $row) {
            $comment_info_html = array();
            $comment_info_html["comment"] = format_string_for_html($row["contribution_comment_text"]);
            $comment_info_html["contribution_title"] = "<a href=\"".SITE_ROOT."teacher_ideas/view-contribution.php?contribution_id={$row["contribution_id"]}\">".format_string_for_html($row["contribution_title"])."</a>";
            $comment_info_html["comment_author"] = format_string_for_html($row["contributor_name"]);
            $comment_info_html["comment_updated"] = format_string_for_html($row["contribution_comment_updated"]);
            $comment_info_html["controls"] =
                "<a href=\"".SITE_ROOT."teacher_ideas/edit-comment.php?comment_id={$row["contribution_comment_id"]}\">edit</a><br />".
                "<a href=\"".SITE_ROOT."teacher_ideas/delete-comment.php?comment_id={$row["contribution_comment_id"]}\">delete</a><br />".
                "<a href=\"".SITE_ROOT."admin/manage-comments.php?delete_comment_id={$row["contribution_comment_id"]}\">delete&nbsp;now</a>";

            $this->comments_array_html[] = $comment_info_html;
        }

        // Now sort by updated time
        function lambdacmp($time1, $time2) {
            $t1 = strtotime($time1["comment_updated"]);//"%Y-%m-%d %H:%M:%S";
            $t2 = strtotime($time2["comment_updated"]);
            if ($t1 == $t2)
                return 0;
            return ($t1 > $t2) ? -1 : 1;
            
        }
        usort($this->comments_array_html, "lambdacmp");
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (isset($this->action_result_html)) {
            $box_stlye = ($this->delete_success) ? "result_box_success" : "result_box_failure";
            print <<<EOT
            <div class="{$box_stlye}">
            <p class="result_box_heading">Action result:</p><br />
            <p>{$this->action_result_html}</p>
            </div>

EOT;
        }

        print <<<EOT
        <table>
        <thead>
            <tr>
                <th>Comment</th>
                <th>Author</th>
                <th>Time Modified</th>
                <th>Title of <br/>Associated Contribution</th>
                <th>Controls</th>
            </tr>
        </thead>
        <tbody>

EOT;

        foreach ($this->comments_array_html as $comment_info) {
            print <<<EOT
            <tr>
                <td>{$comment_info["comment"]}</td>
                <td>{$comment_info["comment_author"]}</td>
                <td>{$comment_info["comment_updated"]}</td>
                <td>{$comment_info["contribution_title"]}</td>
                <td>{$comment_info["controls"]}</td>
            </tr>

EOT;
        }
        print <<<EOT
        </tbody>
        </table>

EOT;
    }

}

$page = new ManageCommentsPage("Manage Comments", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>