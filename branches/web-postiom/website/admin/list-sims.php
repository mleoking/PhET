<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class SimulationListingPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <p>
            Entries that are <span style="color: grey;">grey</span> represent legacy database data that is no longer used and has not yet been culled out of the database.
            </p>
EOT;
        print "<table class=\"compact\">";

        print "<thead><tr>";
        print "<td></td>";
        print "</tr></thead>";
        print "<tbody>";

        foreach (SimFactory::inst()->getAllSims(true) as $simulation) {
            $sim_id = $simulation->getId();

            print "<tr><td colspan=\"2\"><h3>{$simulation->getName()}</h3>";
            print "<a href=\"delete-sim.php?sim_id={$sim_id}&amp;delete=0\">Delete</a>, ";
            print "<a href=\"edit-sim.php?sim_id={$sim_id}\">Edit</a>";
            print "</td></tr>";

            foreach ($simulation->getUsedDBData() as $key => $value) {
                if ($key == 'sim_name') continue;
                print "<tr><td>{$key}</td><td>{$value}</td></tr>";
            }

            foreach ($simulation->getUnusedDBData() as $key => $value) {
                if ($key == 'sim_name') continue;
                print "<tr><td style=\"color: grey;\">{$key}</td><td style=\"color: grey;\">{$value}</td></tr>";
            }

            print "<tr><td colspan=\"2\">&nbsp;</td></tr>";
        }
        
        print "</tbody>";
        
        print "</table>";
    }

}

$page = new SimulationListingPage("Simulation Listing", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>
