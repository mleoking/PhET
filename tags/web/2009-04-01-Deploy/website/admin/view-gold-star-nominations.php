<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class ViewGoldStarNominationsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $stats = get_nomination_statistics();
        $descs = get_nomination_descriptions();

        if (count($stats) > 0) {
            print "<ul>";

            foreach ($stats as $contribution_id => $count) {
                $contribution = contribution_get_contribution_by_id($contribution_id);

                $title      = format_string_for_html($contribution['contribution_title']);
                $title_html = "<a href=\"{$this->prefix}teacher_ideas/edit-contribution.php?contribution_id=$contribution_id\">$title</a>";

                $desc  = $descs[$contribution_id];

                print "<li>$title_html - $count Gold Star Nominations $desc</li>";
            }

            print "</ul>";
        }
        else {
            print "<p>No contributions have been nominated as Gold Star contributions.</p>";
        }
    }

}

$page = new ViewGoldStarNominationsPage("View Gold Star Nominations", NavBar::NAV_ADMIN, null, SitePage::AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>