<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class ManageContributionsPage extends SitePage {
    function print_contributions($contributions, $heading) {
        $contributor = $this->user;
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
        $contributor = $this->user;
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

        $num_contributions = 0;

        $contributions = contribution_get_contributions_for_contributor_id($contributor_id);
        $num_contributions += count($contributions);
        $this->print_contributions($contributions, "My Contributions");

        $contributions = contribution_get_coauthored_contributions_for_contributor_id($contributor_id);
        $num_contributions += count($contributions);
        $this->print_contributions($contributions, "Coauthored Contributions");

        $contributions = contribution_get_other_manageable_contributions_for_contributor_id($contributor_id);
        $num_contributions += count($contributions);
        $this->print_contributions($contributions, "Other Contributions");

        if ($num_contributions == 0) {
            print "<p><strong><em>You don't have any contributions to manage.</em></strong></p>\n";
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $this->print_manage_contributions();

        return true;
    }

}

$page = new ManageContributionsPage("Manage Contributions", NAV_TEACHER_IDEAS, null, AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>