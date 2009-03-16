<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/referrer.php");

class EditContributionPage extends SitePage {

    function update_contribution($contribution) {
        $contributor = $this->user;

        if (!post_size_ok()) {
            return;
        }

        // Only allow team members to change these fields:
        if ($contributor['contributor_is_team_member'] != 1) {
            unset($contribution['contribution_from_phet']);
            unset($contribution['contribution_is_gold_star']);
            if (isset($_REQUEST['contributor_id'])) {
               $contribution['contributor_id'] = $_REQUEST['contributor_id'];
            }
        }
        else {
            if (isset($_REQUEST['contributor_id'])) {
                $contribution['contributor_id'] = $_REQUEST['contributor_id'];
            }
            if (isset($_REQUEST['new_contributor_id'])) {
                $contribution['contributor_id'] = $_REQUEST['new_contributor_id'];
            }
        }

        if (!isset($contribution['contribution_id']) || $contribution['contribution_id'] == -1) {
            // Updating a contribution that does not exist. First, create it:
            $contribution['contribution_id'] = contribution_add_new_contribution(
                $contribution['contribution_title'],
                $contribution['contributor_id']
            );
        }

        contribution_update_contribution($contribution);

        $contribution_id = $contribution['contribution_id'];

        contribution_delete_all_multiselect_associations('contribution_level',   $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_type',    $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_subject', $contribution_id);

        contribution_unassociate_contribution_with_all_simulations($contribution_id);

        // Establish multiselect associations (level, subject, type):
        $files_to_keep = contribution_establish_multiselect_associations_from_script_params($contribution_id);

        $standards_compliance = generate_encoded_checkbox_string('standards');

        contribution_update_contribution(
            array(
                'contribution_id'                   => $contribution_id,
                'contribution_standards_compliance' => $standards_compliance
            )
        );

        // Automatically approve 'edited' submission:
        contribution_set_approved($contribution_id, true);

        // Handle the temporary table element
        db_delete_row("temporary_partial_contribution_track", array("contribution_id" => $contribution_id));

        // Handle files:

        // First, delete all files we aren't supposed to keep:
        contribution_delete_all_files_not_in_list($contribution_id, $files_to_keep);

        // Second, add all new files:
        $this->file_error = false;
        $this->files_present = contribution_contribution_form_has_files();
        if ($this->files_present) {
            $this->file_error = contribution_add_all_form_files_to_contribution($contribution_id);
        }

        cache_clear_teacher_ideas();
        cache_clear_simulations();

        return $contribution_id;
    }

    function handle_action($action) {
        if ($action == 'update') {
            // First, make sure that the page has the special tag that suggests JavaScript
            // is enabled.  If JavaScript is not enabled, this special hidden input will
            // not be created.  Of course, this is circumventable, but this eliminates
            // most of the non-malicious people.
            if (!$this->javascript_submit_is_valid()) {
                $this->print_javascript_error();
                return false;
            }

            $contribution = gather_script_params_into_array('contribution_');

            $this->final_contribution_id = $this->update_contribution($contribution);

            cache_clear(BROWSE_CACHE);
            cache_clear_teacher_ideas();
            cache_clear_simulations();

            return true;
        }
        else {
            return false;
        }
    }

    function print_file_erors() {
        $errors_html = "";
        foreach ($this->file_error as $filename => $error) {
            $errors_html .= "<li><strong>{$filename}</strong> - $error </li>\n";
        }

        print <<<EOT
            <h2>Update File Errors</h2>

            <p>There were problems with some/all of your file uploads.  Aside from the files listed below, your contribution had been successfully updated.</p>

            <p><strong>Errors:</strong>
            <ul>
            {$errors_html}
            </ul>
            </p>

            <p><a href="edit-contribution.php?contribution_id={$this->final_contribution_id}">Go back to the Edit Contribution page</a></p>

EOT;
    }

    function print_success() {
        $return_to = (!empty($_REQUEST['return_to'])) ? $_REQUEST['return_to'] : SITE_ROOT.'teacher_ideas/browse.php';

        print <<<EOT
            <h2>Update Success</h2>

            <p><strong>Thank you! The contribution has been successfully updated.</strong></p>

            <p><a href="{$return_to}">continue</a></p>

EOT;
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        ob_start();

        if (!post_size_ok()) {
            $post_max_size = ini_get('post_max_size');
            print <<<EOT
            <p><strong>Error:</strong> size of file(s) exceeds limit of <strong>{$post_max_size}</strong></p>
            <p>Please try again.</p>

EOT;
        }
        else if (!isset($_REQUEST['contribution_id'])) {
            print <<<EOT
            <p>You must specify a contribution to edit.  Try going to the <a href="manage-contributions.php">Manage Contributions</a> page, and select a activity to edit.</p>

EOT;
        }
        else if ((contribution_get_contribution_by_id($_REQUEST['contribution_id']) === false) &&
                ($_REQUEST['contribution_id'] != -1)){
            print <<<EOT
            <p>Invalid contribution id specified.  Try going to the <a href="manage-contributions.php">Manage Contributions</a> page, and select a activity to edit.</p>

EOT;
        }
        else {
            if ($this->authentication_level < AUTHLEVEL_USER) {
                $this->add_javascript_header_script("disable_not_always_enabled_form_elements();");
            }

            $contribution_id = $_REQUEST['contribution_id'];
            $contributor = $this->user;

            if (contribution_can_contributor_manage_contribution($contributor["contributor_id"], $contribution_id)) {
                if (isset($_REQUEST['action'])) {
                    $success = $this->handle_action($_REQUEST['action']);

                    if (!empty($this->file_error)) {
                        $this->print_file_erors();
                    }
                    else if ($success) {
                        $return_to = (!empty($_REQUEST['return_to'])) ? $_REQUEST['return_to'] : SITE_ROOT.'teacher_ideas/browse.php';
                        $this->meta_refresh($return_to, 3);
                        $this->print_success();
                    }
                    else {
                        if ($this->authenticate_get_level() >= AUTHLEVEL_TEAM) {
                            print_contribution_admin_control_panel($contribution_id, $this->prefix, $this->referrer);
                        }
                        $return_to = (!empty($_REQUEST['return_to'])) ? $_REQUEST['return_to'] : $this->referrer;
                        contribution_print_full_edit_form($contribution_id, "{$this->prefix}teacher_ideas/edit-contribution.php", $return_to, "Update", $this);
                    }
                }
                else {
                    if ($this->authenticate_get_level() >= AUTHLEVEL_TEAM) {
                        print_contribution_admin_control_panel($contribution_id, $this->prefix, $this->referrer);
                    }
                    $return_to = (!empty($_REQUEST['return_to'])) ? $_REQUEST['return_to'] : $this->referrer;
                    contribution_print_full_edit_form($contribution_id, "{$this->prefix}teacher_ideas/edit-contribution.php", $return_to, "Update", $this);
                }
            }
            else {
                print <<<EOT
            <p>You do not have permission to edit this activity.  Try going to the <a href="manage-contributions.php">Manage Contributions</a> page, and select a different activity to edit.</p>

EOT;
            }
        }

        $this->add_content(ob_get_clean());
        return true;
    }

    function render_content() {
        // If this contribution is partial, add extra instructions
        if (isset($_REQUEST['contribution_id'])) {
            $partial_rows = db_get_rows_by_condition('temporary_partial_contribution_track', array('contribution_id' => $_REQUEST['contribution_id']));
            if (is_array($partial_rows) && (count($partial_rows) > 0)) {
                $this->set_login_header_message("<strong>You must <em>login</em> or <em>create an account</em> to finish submitting this contribution</strong>");
            }
        }

        $result = parent::render_content();
        if (!$result) {
            return BasePage::render_content();
        }
    }

}

$page = new EditContributionPage("Edit an Activity", NAV_TEACHER_IDEAS, get_referrer(SITE_ROOT.'teacher_ideas/manage-contributions.php'), AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>