<?php

include_once("../admin/global.php");

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class NominateContributionPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (!auth_user_validated()) {
            return;
        }

        if (!isset($_REQUEST['contribution_id']) ||
            !isset($_REQUEST['contribution_nomination_desc'])) {
            return false;
        }

        $this->contribution_id = $_REQUEST['contribution_id'];
        $contribution_nomination_desc = $_REQUEST['contribution_nomination_desc'];

        $this->success = nominate_contribution($this->contribution_id, $contribution_nomination_desc);
    }

    function print_nomination_success() {
        $contribution = contribution_get_contribution_by_id($this->contribution_id);
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        print <<<EOT
            <p>Thank you for nominating &quot;{$contribution_title}&quot; for a Gold Star.</p>

            <p>If PhET determines the contribution meets Gold Star criteria, the contribution will receive a Gold Star.</p>

EOT;
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

$page = new NominateContributionPage("Nominate Contribution", NAV_TEACHER_IDEAS, null, SP_AUTHLEVEL_USER);
$page->update();
$page->render();

?>