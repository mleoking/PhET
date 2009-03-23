<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/referrer.php");

class ViewContributionPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (!isset($_REQUEST['contribution_id'])) {
            print "<p>No contribution id specified.</p>\n";
            return;
        }

        $contribution_id = $_REQUEST['contribution_id'];

        $contribution = contribution_get_contribution_by_id($contribution_id);
        if (!$contribution) {
            print "<p>Invalid contribution id specified.</p>\n";
            return;
        }

        $level_names   = contribution_get_level_names_for_contribution($contribution_id);
        $subject_names = contribution_get_subject_names_for_contribution($contribution_id);
        $type_names    = contribution_get_type_names_for_contribution($contribution_id);

        $contribution = format_for_html($contribution);
        // Removing unsafe function 'get_code_to_create_variables_from_array',
        // just doing the equivalent by hand
        //eval(get_code_to_create_variables_from_array($contribution));
        $contribution_id = $contribution["contribution_id"];
        $contribution_title = $contribution["contribution_title"];
        $contribution_authors = $contribution["contribution_authors"];
        $contribution_keywords = $contribution["contribution_keywords"];
        $contribution_approved = $contribution["contribution_approved"];
        $contribution_desc = nl2br(trim($contribution["contribution_desc"]));
        $contribution_duration = $contribution["contribution_duration"];
        $contribution_answers_included = $contribution["contribution_answers_included"];
        $contribution_contact_email = $contribution["contribution_contact_email"];
        $contribution_authors_organization = $contribution["contribution_authors_organization"];
        $contribution_date_created = $contribution["contribution_date_created"];
        $contribution_date_updated = $contribution["contribution_date_updated"];
        $contribution_nomination_count = $contribution["contribution_nomination_count"];
        $contribution_flagged_count = $contribution["contribution_flagged_count"];
        $contribution_standards_compliance = $contribution["contribution_standards_compliance"];
        $contribution_from_phet = $contribution["contribution_from_phet"];
        $contribution_is_gold_star = $contribution["contribution_is_gold_star"];
        $contributor_id = $contribution["contributor_id"];

        // Perform cleanup for some fields:
        $contribution_keywords = convert_comma_list_into_linked_keyword_list($contribution_keywords);

        $contribution_date_created = db_simplify_sql_timestamp($contribution_date_created);
        $contribution_date_updated = db_simplify_sql_timestamp($contribution_date_updated);

        $contribution_answers_included = $contribution_answers_included == 1 ? "Yes" : "No";

        $type_list    = convert_array_to_comma_list($type_names);
        $subject_list = convert_array_to_comma_list($subject_names);
        $level_list   = convert_array_to_comma_list($level_names);

        $files_html = contribution_get_files_listing_html($contribution_id);

        $download_script = SITE_ROOT."admin/download-archive.php?contribution_id=$contribution_id";

        if ($contribution_duration == '') {
            $contribution_duration = 0;
        }

        if ($contribution_duration == 0) {
            $contribution_duration_html = "NA";
        }
        else {
            $contribution_duration_html = "$contribution_duration minutes";
        }

        $comments = contribution_get_comments($contribution_id);

        $comment_count = count($comments);

        $comments_html = '';

        if ($comment_count == 0) {
            $comments_html = "<em>There are no comments for this activity yet</em>";
        }
        else {
            foreach($comments as $comment) {
                $comments_html .= '<p class="comment">&quot;<em>';
                $comments_html .= format_string_for_html($comment['contribution_comment_text']);
                $comments_html .= '</em>&quot; - '.format_string_for_html($comment['contributor_name']);
                if (($this->user["contributor_id"] == $comment["contributor_id"]) ||
                    ($this->user["contributor_is_team_member"])) {
                    $comments_html .= " (<a href=\"edit-comment.php?comment_id=".$comment["contribution_comment_id"]."\">edit</a>,";
                    $comments_html .= " <a href=\"delete-comment.php?comment_id=".$comment["contribution_comment_id"]."\">delete</a>)";
                }
                $comments_html .= '</p>';
            }
        }

        $contribution_simulations = contribution_get_simulation_listings_as_list($contribution_id);

        $gold_star_html = contribution_get_gold_star_html_for_contribution($contribution);

        if (count(contribution_get_contribution_file_infos($contribution_id)) > 0) {
            $download_zip_html = <<<EOT
        <p>Or you may <a href="$download_script">download</a> all files as a compressed archive (<a href="http://en.wikipedia.org/wiki/ZIP_(file_format)">ZIP</a>).</p>

EOT;
        }
        else {
            $download_zip_html = "";
        }

        if ($this->authenticate_get_level() >= SitePage::AUTHLEVEL_TEAM) {
            print_contribution_admin_control_panel($contribution_id, $this->prefix, $this->referrer);
        }

        print <<<EOT
        <h2>$contribution_title</h2>

        $gold_star_html

        <div id="contributionview">
            <h3>Download Files</h3>

            $files_html

            $download_zip_html

            <h3>Submission Information</h3>

            <div class="field">
                <span class="label_content">$contribution_authors &nbsp;</span>

                <span class="label">authors</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_contact_email &nbsp;</span>

                <span class="label">contact email</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_authors_organization &nbsp;</span>

                <span class="label">school/organization</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_date_created</span>

                <span class="label">submitted</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_date_updated</span>

                <span class="label">updated</span>
            </div>

            <h3>Contribution Description</h3>

            <div class="field">
                <span class="label_content">$contribution_title &nbsp;</span>

                <span class="label">title</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_simulations &nbsp;</span>

                <span class="label">simulations</span>
            </div>

            <div class="field">
                <span class="label_content" id="keywords">$contribution_keywords</span>

                <span class="label">keywords</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_desc &nbsp;</span>

                <span class="label">description</span>
            </div>

            <div class="field">
                <span class="label_content">$level_list &nbsp;</span>

                <span class="label">level</span>
            </div>

            <div class="field">
                <span class="label_content">$type_list &nbsp;</span>

                <span class="label">type</span>
            </div>

            <div class="field">
                <span class="label_content">$subject_list &nbsp;</span>

                <span class="label">subject</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_duration_html</span>

                <span class="label">duration</span>
            </div>

            <div class="field">
                <span class="label_content">$contribution_answers_included</span>

                <span class="label">answers included</span>
            </div>

            <div class="field">
                <span class="label">standards compliance</span>
            </div>

            <div class="field">

EOT;

        contribution_print_standards_compliance($contribution_standards_compliance, true);

        $php_self = $_SERVER['PHP_SELF'];

        if (!isset($GLOBALS['contributor_name'])) {
            $contributor_name = '';
        }
        else {
            $contributor_name = $GLOBALS['contributor_name'];
        }

        $html_referrer = format_for_html($this->referrer);

        print <<<EOT
            </div>

            <h3>Nominations for Gold Star</h3>

            <p><em>
                Gold Star contributions are high quality inquiry-based activities that follow the
                <a href="contribution-guidelines.php">PhET design guidelines</a>
                (<a href="contribution-guidelines.pdf">PDF</a>) and that teachers
                find useful.
            </em></p>
            <p><a href="javascript:void;" onclick="$(this).parent().next().toggle(300); return false;">Nominate this contribution as a Gold Star Activity</a></p>

            <div id="nominate-contribution" style="display: none;">
                <form method="get" action="{$this->prefix}teacher_ideas/nominate-contribution.php">
                    <div>
                        <input type="hidden" name="contribution_id" value="$contribution_id" />
                    </div>

                    <table class="form">
                        <tr>
                            <td>reason for nomination</td>    <td><textarea name="contribution_nomination_desc" rows="5" cols="50"></textarea></td>
                        </tr>

                        <tr>
                            <td colspan="2">
                                <input type="submit" name="submit" value="Nominate" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>

            <div>
            <h3>Comments</h3>
            <p>What do you think about this activity?
                How did you use it or change it for your class?
                Professionally constructive comments welcome.
                (<a href="#" onclick="$(this).parent().next().toggle(300);return false;">add comment</a>)
            </p>

            <div id="hidden_comment_box" style="display: none">
            <form method="get" action="add-comment.php" onsubmit="javascript:return false;">
                    <p>
                        <input type="hidden" name="contribution_id" value="{$contribution_id}" />
                    </p>


                    <div id="required_login_info_uid">

                    </div>

                    <div class="field">
                        <span class="label_content">
                            <textarea name="contribution_comment_text" cols="40" rows="5" ></textarea>
                        </span>

                        <span class="label">your comment</span>
                    </div>

                    <div class="field">
                        <span class="label_content">
                            <input type="button" onclick="javascript:this.form.submit();" value="Add Comment" name="add" />
                        </span>

                        <span class="label">&nbsp;</span>
                    </div>

                </form>
            </div>

            <div class="comments">
                $comments_html
            </div>

            <hr/>

            </div>

        </div>

        <p><a href="{$html_referrer}">back</a></p>

EOT;
    }

}

$page = new ViewContributionPage("View Contributions", NavBar::NAV_TEACHER_IDEAS, get_referrer(SITE_ROOT."teacher_ideas/manage-contributions.php"), SitePage::AUTHLEVEL_NONE);
$page->update();
$page->render();

?>