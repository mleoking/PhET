<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/web-utils.php");

class SimSubmitContributionPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Check if the file is just too big
        if (post_size_ok()) {
            return;
        }

        if (!isset($_REQUEST['contribution_title'])) {
            return;
        }

        if (!$this->javascript_submit_is_valid()) {
            return false;
        }

        $sim_id             = $_REQUEST['sim_id'];
        $contribution_title = $_REQUEST['contribution_title'];

        if (isset($_FILES['contribution_file_url'])) {
            $file = $_FILES['contribution_file_url'];
        }
        else if (isset($_FILES['MF__F_0_0'])) {
            $file = $_FILES['MF__F_0_0'];
        }
        else {
            $file = null;
        }

        if (!is_null($file)) {
            $name     = $file['name'];
            $type     = $file['type'];
            $tmp_name = $file['tmp_name'];
            $size     = $file['size'];
            $error    = $file['error'] != 0;
        }
        else {
            $name     = null;
            $type     = null;
            $tmp_name = null;
            $size     = null;
            $error    = null;
        }

        if ($contribution_title == '') {
            $contribution_title = remove_file_extension(basename($name));
        }

        if (!isset($contributor_id)) {
            // The user isn't logged in yet. We'll add the contribution and change
            // the owner later:
            $contributor_id = -1;
        }

        $contribution_id = contribution_add_new_contribution($contribution_title, $contributor_id, $tmp_name, $name);

        // Set it as unapproved initially, until user edits it:
        contribution_set_approved($contribution_id, false);

        // Handle files:
        for ($i = 1; true; $i++) {
            $file_key = "MF__F_0_$i";

            if (!isset($_FILES[$file_key])) {
                break;
            }
            else {
                $file = $_FILES[$file_key];

                $name     = $file['name'];
                $type     = $file['type'];
                $tmp_name = $file['tmp_name'];
                $size     = $file['size'];
                $error    = $file['error'] != 0;

                if (!$error){
                    contribution_add_new_file_to_contribution($contribution_id, $tmp_name, $name);
                }
                else {
                    // Some error occurred during file upload
                }
            }
        }

        // Associate contribution with simulation:
        if (is_numeric($sim_id)) {
            contribution_associate_contribution_with_simulation($contribution_id, $sim_id);
        }

        // Establish multiselect associations (level, subject, type):
        contribution_establish_multiselect_associations_from_script_params($contribution_id);

        $sim_url = sim_get_url_to_sim_page($sim_id);

        // Add it to the temporary table
        $row_data = array("contribution_id" => $contribution_id, "sessionid" => session_id());
        db_insert_row("temporary_partial_contribution_track", $row_data);

        $sims_page    = "\"$sim_url\"";
        $this->edit_contrib = "{$this->prefix}teacher_ideas/edit-contribution.php?contribution_id=$contribution_id&amp;sim_id=$sim_id&amp;referrer=$sims_page";

        // Immediately redirect to contribution editing page:
        $this->meta_refresh("{$this->edit_contrib}", 0);
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $post_max_size = ini_get('post_max_size');
        if (!post_size_ok()) {
            print "<p><strong>Error:</strong> size of file(s) exceeds limit of <strong>{$post_max_size}</strong></p>\n";
            return;
        }

        if (!isset($_REQUEST['contribution_title'])) {
            print "<p>No contribution title specified.  Go back and try again.</p>\n";
            return;
        }

        if (!$this->javascript_submit_is_valid()) {
            $this->print_javascript_error();
            return;
        }

        BasePage::render_redirect();
    }

}

$page = new SimSubmitContributionPage("Submit Contribution from Simulation Page", NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>