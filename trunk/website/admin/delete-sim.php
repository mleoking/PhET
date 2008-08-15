<?

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class DeleteSimPage extends SitePage {

    function deletesim() {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $sim_id      = $_REQUEST['sim_id'];
        // delete from SIMULATIONS TABLE
        $sql        = "DELETE FROM `simulation` WHERE `sim_id`='$sim_id' ";
        $sql_result = mysql_query($sql, $connection);

        // delete from CATEGORIES TABLE
        $sql        = "DELETE FROM `simulation_listing` WHERE `sim_id`='$sim_id' ";
        $sql_result = mysql_query($sql, $connection);

        cache_clear_simulations();

        print "<p>Successfully deleted the simulation from the database. <a href=\"list-sims.php\">Click here to go back.</a></p>";
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (!isset($_REQUEST['sim_id'])) {
            print "<strong><em>No simulation specified</em></strong>\n";
            return;
        }

        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $sim_id = $_REQUEST['sim_id'];
        $delete = isset($_REQUEST['delete']) ? $_REQUEST['delete'] : 0;

        if ($delete == '1') {
            $this->deletesim();
        }
        else {
            // first select what SIMULATION to delete
            $sql        = "SELECT * FROM `simulation` WHERE `sim_id`='$sim_id' ";
            $sql_result = mysql_query($sql, $connection);

            while ($row = mysql_fetch_assoc($sql_result)) {
                $sim_name = $row['sim_name'];

                print "<p><b>Are you sure you want to delete the simulation \"$sim_name\"?</b></p>";

                print "<p><a href=delete-sim.php?sim_id=$sim_id&delete=1>Yes</a> | <a href=list-sims.php>NO</a></p>";
            }
        }
    }

}

$page = new DeleteSimPage("Delete Simulation", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>
