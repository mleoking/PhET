<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class ChooseSimPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        print <<<EOT
            <p>
                Please choose the simulation to edit from the list below.
            </p>

            <form action="edit-sim.php" method="post">
                <p>
                    <select name="sim_id">

EOT;

        $select_simulations_st = "SELECT * FROM `simulation` ORDER BY `sim_name` ASC ";
        $simulation_table      = mysql_query($select_simulations_st, $connection);

        while ($sim = mysql_fetch_row($simulation_table)) {
            $sim_id   = $sim[0];
            $simtitle = format_string_for_html($sim[1]);

            //print drop down menu
            print "<option value=\"$sim_id\">$simtitle</option>";
        }

        // close drop down menu and form
        print <<<EOT
                    </select>

                <input type="submit" value="edit" />
                </p>
            </form>

EOT;
    }

}

$page = new ChooseSimPage("Choose Simulation", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>
