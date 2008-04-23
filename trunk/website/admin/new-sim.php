<?php

    include_once("../admin/global.php");
include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class NewSimulation extends SitePage {
    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        db_exec_query("INSERT INTO `simulation` (`sim_name`, `sim_keywords`) VALUES ('New Simulation', 'keyword1, keyword2, keyword3') ");

        $sim_id = mysql_insert_id();

        $this->meta_refresh("edit-sim.php?sim_id=$sim_id", 0);
    }
}

$page = new NewSimulation("New Simulations", NAV_ADMIN, null, SP_AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>