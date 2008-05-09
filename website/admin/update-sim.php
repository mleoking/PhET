<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class UpdateSimulationPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $simulation = array();

        // Update every field that was passed in as a _REQUEST parameter and
        // which starts with 'sim_':
        foreach($_REQUEST as $key => $value) {
            if (preg_match('/sim_.*/', $key) == 1) {
                if (preg_match('/^sim_.+_url$/', $key) == 1) {
                    // Maybe the user uploaded something instead of specifying a url:
                    $value = process_url_upload_control($key, $value);
                }
                else {
                    // Get rid of escape characters:
                    $value = str_replace('\\', '', $value);
                }

                $simulation[$key] = $value;
            }
        }

        $this->sim_name = $simulation["sim_name"];
        $sim_id = $simulation["sim_id"];

        // The sorting name should not be prefixed by such words as 'the', 'an', 'a':
        $simulation['sim_sorting_name'] = get_sorting_name($this->sim_name);

        sim_update_sim($simulation);

        // Update size of simulation:
        sim_auto_calc_sim_size($simulation['sim_id']);

        // Now we have to update the categories manually:
        $category_rows = mysql_query("SELECT * FROM `category` ");

        while ($category_row = mysql_fetch_assoc($category_rows)) {
            $cat_id   = $category_row['cat_id'];
            $cat_name = $category_row['cat_name'];

            $key_to_check_for = "checkbox_cat_id_$cat_id";

            $is_in_category        = sim_is_in_category($sim_id, $cat_id);
            $should_be_in_category = isset($_REQUEST["$key_to_check_for"]);

            if ($is_in_category && !$should_be_in_category) {
                db_exec_query("DELETE FROM `simulation_listing` WHERE `cat_id`='$cat_id' AND `sim_id`='$sim_id' ");
            }
            else if (!$is_in_category && $should_be_in_category) {
                db_exec_query("INSERT INTO `simulation_listing` (`sim_id`, `cat_id`) VALUES('$sim_id', '$cat_id')");
            }
        }

        // Cleanup junk, if any:
        db_exec_query('DELETE FROM `simulation` WHERE `sim_name`=\'New Simulation\' ');

        // Clear the sim cache
        cache_clear_simulations();

        // TODO: check for success!
        $this->meta_refresh("edit-sim.php?sim_id=$sim_id", 2);
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <h2>Update Successful</h2>

            <p>The simulation "{$this->sim_name}" was successfully updated.</p>

EOT;
    }

}

$page = new UpdateSimulationPage("Update Simulation", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>