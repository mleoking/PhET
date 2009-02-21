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

        print "<table class=\"compact\">";

        print "<thead><tr>";
        print "<td></td>";
        print "</tr></thead>";
        print "<tbody>";

        foreach (sim_get_all_sims() as $simulation) {
            $sim_id = $simulation['sim_id'];

            print "<tr><td><h3>".format_string_for_html($simulation['sim_name'])."</h3>";
            print "<a href=\"delete-sim.php?sim_id=$sim_id&amp;delete=0\">Delete</a>, ";
            print "<a href=\"edit-sim.php?sim_id=$sim_id\">Edit</a>";
            print "</td></tr>";

            foreach($simulation as $key => $value) {
                // FIXME: change this to == and a continue
                if ($key != 'sim_name') {
                    $formatted_key = format_string_for_html($key);
                    $formatted_value = format_string_for_html($value);
                    print "<tr><td>$formatted_key</td><td>$formatted_value</td></tr>";
                }
            }

            print "<tr><td>&nbsp;</td></tr>";
        }
        
        print "</tbody>";
        
        print "</table>";
    }

}

$page = new SimulationListingPage("Simulation Listing", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>
