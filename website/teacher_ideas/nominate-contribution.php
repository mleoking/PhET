<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");
require_once("teacher_ideas/referrer.php");

class NominateContributionPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (!isset($_REQUEST['contribution_id']) ||
            !isset($_REQUEST['contribution_nomination_desc'])) {
            return false;
        }

        $this->contribution_id = $_REQUEST['contribution_id'];
        $contribution_nomination_desc = $_REQUEST['contribution_nomination_desc'];

        $this->success = nominate_contribution($this->contribution_id,
                                               $contribution_nomination_desc,
                                               $this->user["contributor_id"]);

        if ($this->success) {
            $this->meta_refresh($this->referrer, 5);
        }
    }

    function print_nomination_success() {
        $contribution = contribution_get_contribution_by_id($this->contribution_id);

        $contribution_title = format_string_for_html($contribution["contribution_title"]);

        print <<<EOT
            <p>Thank you for nominating &quot;{$contribution_title}&quot; for a Gold Star.</p>

            <p>If PhET determines the contribution meets Gold Star criteria, the contribution will receive a Gold Star.</p>

EOT;

        $this->render_redirect();
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (isset($this->success) && $this->success) {
            $this->print_nomination_success();
        }
        else {
            print "<p>There was an error, please go back and try again.</p>\n";
        }
    }

}

$page = new NominateContributionPage("Nominate Contribution", NAV_TEACHER_IDEAS, get_referrer(contribution_url_to_view_from_uri()), AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>