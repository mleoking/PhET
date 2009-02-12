<?php

// Administrative control panel page

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class AdminControlPanelPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <h3>Simulations</h3>
            <ul>
                <li><a href="new-sim.php">Add Simulation</a></li>

                <li><a href="choose-sim.php">Edit Existing Simulation</a></li>

                <li><a href="list-sims.php">List Simulations</a></li>

                <li><a href="organize-cats.php">Organize Categories</a></li>

                <li><a href="organize-sims.php">Organize Simulations</a></li>
            </ul>

            <h3>Contributions</h3>
            <ul>
                <li><a href="manage-contributors.php">Manage Contributors</a></li>
                <li><a href="manage-comments.php">Manage Comments</a></li>
                <li><a href="view-gold-star-nominations.php">View Gold Star Nominations</a></li>
            </ul>

            <h3>Database</h3>
            <ul>
                <li><a href="db-check-integrity.php">Check database integrity (processor intensive, only use if you need it)</a></li>
                <li><a href="manage-db.php">Manage Database</a></li>
            </ul>

            <h3>Web page caching</h3>
            <ul>
                <li><a href="cache-clear.php?cache=sims">Clear the simulation cache</a></li>
                <li><a href="cache-clear.php?cache=teacher_ideas">Clear the activities cache</a></li>
                <li><a href="cache-clear.php?cache=admin">Clear the admin directory cache</a></li>
                <li><a href="cache-clear.php?cache=all">Clear all the caches</a></li>
            </ul>

EOT;
    }

}

$page = new AdminControlPanelPage("PhET Administration Control Panel", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>