<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class ManageContributorsPage extends SitePage {

    function do_update() {
        $contributor_id = $_REQUEST['contributor_id'];

        contributor_update_contributor_from_script_parameters($contributor_id);
    }

    function do_new() {
        $contributor_id = contributor_add_new_blank_contributor();

        $_REQUEST['contributor_id']       = "$contributor_id";
        $_REQUEST['contributor_password'] = web_create_random_password();

        $this->do_update();
    }

    function do_delete() {
        $contributor_id = $_REQUEST['contributor_id'];

        contributor_delete_contributor($contributor_id);
    }

    function handle_action() {
        if (isset($_REQUEST["action"])) {
            $action = $_REQUEST["action"];
            if ($action == "update") {
                $this->do_update();
            }
            else if ($action == "new") {
                $this->do_new();
            }
            else if ($action == "delete") {
                $this->do_delete();
            }
        }
    }

    function print_contributors() {
        $contributors = contributor_get_all_contributors();

        print <<<EOT
        <p>With this form, you can add, delete, and update the status of PhET contributors, including
        PhET team members.</p>

        <div class="compact">
            <table>
                <thead>
                    <tr>
                        <td>Name</td>   <td>Email</td>  <td>Team Member</td>    <td>Action</td>
                    </tr>
                </thead>

                <tbody>
                    <tr>

EOT;

        foreach($contributors as $contributor) {
            $contributor_id             = format_for_html($contributor['contributor_id']);
            $contributor_name           = format_for_html($contributor['contributor_name']);
            $contributor_email          = format_for_html($contributor['contributor_email']);
            $contributor_is_team_member = format_for_html($contributor['contributor_is_team_member']);

            $contributor_is_team_member_html = $contributor_is_team_member == '1' ? 'Y' : 'N';

            $checked_status = $contributor_is_team_member == '1' ? "checked=\"checked\"" : "";

            print <<<EOT
                        <tr>
                            <td>
                                <a href="edit-other-profile.php?edit_contributor_id=$contributor_id">$contributor_name</a>
                            </td>

                            <td>
                                <a href="mailto:$contributor_email?Subject=Your%20Account%20With%20PhET">$contributor_email</a>
                            </td>

                            <td>
                                $contributor_is_team_member_html
                            </td>

                            <td>
                                <a href="manage-contributors.php?contributor_id=$contributor_id&amp;action=delete">Delete</a>
                                <a href="edit-other-profile.php?edit_contributor_id=$contributor_id">Edit</a>
                            </td>
                        </tr>

EOT;
        }


        print <<<EOT2
                    </tr>

                    <tr>
                        <form action="manage-contributors.php" method="post">
                            <input type="hidden" name="action" value="new" />

                            <td><input type="text" name="contributor_name" /></td>
                            <td><input type="text" name="contributor_email" /></td>
                            <td><input type="checkbox" name="contributor_is_team_member" value="1" /></td>

                            <td><input type="submit" value="Add" /></td>
                        </form>
                    </tr>
                </tbody>
            </table>
        </div>

EOT2;
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->handle_action();
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $this->print_contributors();
    }

}

$page = new ManageContributorsPage("Manage Contributors", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>