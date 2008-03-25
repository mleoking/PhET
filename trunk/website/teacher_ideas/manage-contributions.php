<?php

include_once("../admin/BasePage.php");

class ManageContributions extends BasePage {
    function print_contributions($contributions, $heading) {
        //global $contributor_id, $contributor_is_team_member;
        $username = auth_get_username();
        $contributor = contributor_get_contributor_by_username($username);
        $contributor_id = $contributor["contributor_id"];
        $contributor_is_team_member = $contributor["contributor_is_team_member"];

        if (count($contributions) > 0) {
            print <<<EOT
            <table>
                <tr>
                    <td colspan="3"><h2>$heading</h2></td>
                </tr>

EOT;

            foreach($contributions as $contribution) {
                contribution_print_summary($contribution, $contributor_id, $contributor_is_team_member,
                    SITE_ROOT."teacher_ideas/manage-contributions.php"
                );
            }

            print "            </table>\n";
        }
    }

    function print_manage_contributions() {
        //global $contributor_id, $contributor_is_team_member;
        $username = auth_get_username();
        $contributor = contributor_get_contributor_by_username($username);
        $contributor_id = $contributor["contributor_id"];
        $contributor_is_team_member = $contributor["contributor_is_team_member"];

        if ($contributor_is_team_member) {
        print <<<EOT
            <table>
                <tr style="background-color: #ffbbbb">
                    <td>
                        Red rows indicate <strong>un</strong>approved contributions
                    </td>
                </tr>
            </table>

EOT;
        }

        $contributions = contribution_get_contributions_for_contributor_id($contributor_id);
        $this->print_contributions($contributions, "My Contributions");

        $contributions = contribution_get_coauthored_contributions_for_contributor_id($contributor_id);
        $this->print_contributions($contributions, "Coauthored Contributions");

        $contributions = contribution_get_other_manageable_contributions_for_contributor_id($contributor_id);
        $this->print_contributions($contributions, "Other Contributions");
    }

    function render_content() {
        if (!auth_user_validated()) {
            $intro_text = "<p>You must be logged in to manage the contributions</p>";

            print_login_and_new_account_form("manage-contributions.php", "manage-contributions.php", $this->referrer, $intro_text);
        }
        else {
            $this->print_manage_contributions();
        }
    }
}

auth_do_validation();
$page = new ManageContributions(3, get_referrer(), "Manage Contributions");
$page->update();
$page->render();

?>