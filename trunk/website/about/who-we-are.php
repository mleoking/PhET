<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class WhoWeArePage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $is_team_member = isset($GLOBALS['contributor_is_team_member']) ? $GLOBALS['contributor_is_team_member'] : 0;

        print <<<EOT
        <img src="../images/contact-page.jpg" class="imageOne" alt="Image of the PhET Team" width="578"/>

        <div>
            <div class="caption">
                <span style="font-weight: bold; text-decoration: underline; font-style: italic;">Back Row:</span> Mindy Gratny, Chris Malley, Chris Keller, John De Goes, Sam Reid, Carl Wieman
            </div>

            <div class="caption">
                <span style="font-weight: bold; text-decoration: underline; font-style: italic;">Front Row:</span> Kathy Perkins, Alex Adams, Wendy Adams, Angie Jardine, Mike Dubson, Noah Finkelstein
            </div>

            <p>&nbsp;</p>

            <h4>The PhET Team</h4>

            <ul id="people">

EOT;

            $team_members = contributor_get_team_members();

            foreach($team_members as $team_member) {
                $contributor_name = format_for_html($team_member["contributor_name"]);
                $contributor_title = format_for_html($team_member["contributor_title"]);
                $contributor_primary_phone = format_for_html($team_member["contributor_primary_phone"]);
                $contributor_secondary_phone = format_for_html($team_member["contributor_secondary_phone"]);
                $contributor_office = format_for_html($team_member["contributor_office"]);

                print "<li>$contributor_name - $contributor_title";

                if ($is_team_member) {
                    print <<<EOT
                        <br/>
                        <table class="form">
                            <tr>
                                <td>
                                    primary phone:
                                </td>

                                <td>
                                    $contributor_primary_phone
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    secondary phone:
                                </td>

                                <td>
                                    $contributor_secondary_phone
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    office:
                                </td>

                                <td>
                                    $contributor_office
                                </td>
                            </tr>
                        </table>
                        <br/>

EOT;
                        }

                        print "</li>";
                    }
                print <<<EOT
            </ul>
        </div>

EOT;
    }
}

$page = new WhoWeArePage("Who We Are", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>