<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class NewSimulationPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        db_exec_query("INSERT INTO `simulation` (`sim_name`, `sim_keywords`) VALUES ('".DEFAULT_NEW_SIMULATION_NAME."', 'keyword1, keyword2, keyword3') ");

        $sim_id = mysql_insert_id($connection);

        $this->meta_refresh("edit-sim.php?sim_id=$sim_id", 0);
    }

}

$page = new NewSimulationPage("New Simulations", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>