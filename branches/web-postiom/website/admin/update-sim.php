<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class UpdateSimulationPage extends SitePage {

    function handle_teachers_guide($sim_id) {
        //
        // Teacher's guide

        // Check the radio button value
        if (!isset($_REQUEST["radio_teachers_guide_action"])) {
            return false;
        }

        if ($_REQUEST["radio_teachers_guide_action"] == 'no_change') {
            // Nothing to do
            return true;
        }
        else if ($_REQUEST["radio_teachers_guide_action"] == 'remove') {
            // Remove the teacher's guide
            $sim = SimFactory::inst()->getById($sim_id);
            $sim->removeTeachersGuide();
            return true;
        }
        else if ($_REQUEST["radio_teachers_guide_action"] != 'upload') {
            // If here, the request should be to upload.  If not, something is whacked
            return false;
        }

        // See if there is a file uploaded
        $file_key = "sim_teachers_guide_file_upload";
        if (isset($_FILES[$file_key])) {
            $file_user_name = $_FILES[$file_key]["name"];
            $file_tmp_name = $_FILES[$file_key]["tmp_name"];
            $file_size = $_FILES[$file_key]["size"];
            $file_error = $_FILES[$file_key]["error"];
            if (($file_size > 0) &&
                (!empty($file_tmp_name)) &&
                ($file_error == UPLOAD_ERR_OK)) {
                // All good, do file stuff

                // If there is a file, drop any associations with it
                $sim = SimFactory::inst()->getById($sim_id);
                $sim->removeTeachersGuide();

                $file_content = file_get_contents($file_tmp_name);
                $sim->setTeachersGuide($file_user_name, $file_content, $file_size);
            }
        }
    }

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

        $simulation = array();

        // Input names for strings requiring list processing
        $lists = array(
            'sim_keywords', 'sim_main_topics', 'sim_sample_goals', 
            'sim_design_team', 'sim_libraries', 'sim_thanks_to'
            );

        // Update every field that was passed in as a _REQUEST parameter and
        // which starts with 'sim_':
        foreach($_REQUEST as $key => $raw_value) {
            if (preg_match('/sim_.*/', $key) != 1) continue;

            if (in_array($key, $lists)) {
                $trimmed_data = trim($raw_value, '*, ');
                $stripped_data = preg_replace("/\r\n/", '', $trimmed_data);
                if (strstr($stripped_data, '*')) {
                    $data = preg_split('/ *\\* */', $stripped_data, -1, PREG_SPLIT_NO_EMPTY);
                }
                else {
                    $data = preg_split('/ *, */', $stripped_data, -1, PREG_SPLIT_NO_EMPTY);
                }
                $value = join('*', $data);
            }
            else {
                $value = trim($raw_value);
            }

            $simulation[$key] = $value;
        }

        $this->handle_teachers_guide($simulation["sim_id"]);

        $this->sim_name = $simulation["sim_name"];
        $sim_id = $simulation["sim_id"];

        // The sorting name should not be prefixed by such words as 'the', 'an', 'a':
        $simulation['sim_sorting_name'] = SimUtils::inst()->generateSortingName($this->sim_name);

        db_update_table(
            'simulation',
            $simulation,
            'sim_id',
            $simulation['sim_id']
            );

        // Now we have to update the categories manually:
        $category_rows = mysql_query("SELECT * FROM `category` ", $connection);

        while ($category_row = mysql_fetch_assoc($category_rows)) {
            $cat_id   = $category_row['cat_id'];
            $cat_name = $category_row['cat_name'];

            $key_to_check_for = "checkbox_cat_id_$cat_id";

            $is_in_category        = CategoryUtils::inst()->isSimInCategory($sim_id, $cat_id);
            $should_be_in_category = isset($_REQUEST["$key_to_check_for"]);

            if ($is_in_category && !$should_be_in_category) {
                db_exec_query("DELETE FROM `simulation_listing` WHERE `cat_id`='$cat_id' AND `sim_id`='$sim_id' ");
            }
            else if (!$is_in_category && $should_be_in_category) {
                db_exec_query("INSERT INTO `simulation_listing` (`sim_id`, `cat_id`) VALUES('$sim_id', '$cat_id')");
            }
        }

        // Cleanup junk, if any:
        db_exec_query('DELETE FROM `simulation` WHERE `sim_name`=\''.DEFAULT_NEW_SIMULATION_NAME.'\' ');

        // Clear the sim cache
        cache_clear_simulations();
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        // FIXME: $this->sim_name needs to be HTML formatted
        $encoded_sim_name = WebUtils::inst()->encodeString($this->sim_name);
        print <<<EOT
            <h2>Update Successful</h2>

            <p>The simulation "{$this->sim_name}" was successfully updated.</p>

            <p><a href="../simulations/sims.php?sim={$encoded_sim_name}">Go to the simulation page</a>

EOT;
    }

}

$page = new UpdateSimulationPage("Update Simulation", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>