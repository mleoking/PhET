<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/ordering-util.php");

class OrganizeSimulationsPage extends SitePage {

    function handle_action($action, $simulation_listing_id, $cat_id) {
        $condition = array( 'cat_id' => $cat_id );

        if ($action == 'move_up') {
            order_move_higher('simulation_listing', $simulation_listing_id, $condition);
        }
        else if ($action == 'move_down') {
            order_move_lower('simulation_listing', $simulation_listing_id, $condition);
        }
        else {
            // undefined action, ignore
            return;
        }

        cache_clear_simulations();
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (isset($_REQUEST['action']) && isset($_REQUEST['simulation_listing_id']) && isset($_REQUEST['cat_id'])) {
            $this->handle_action($_REQUEST['action'], $_REQUEST['simulation_listing_id'], $_REQUEST['cat_id']);
        }

        if (isset($_REQUEST['cat_id'])) {
            $cat_id = $_REQUEST['cat_id'];

            $this->add_javascript_header_script("location.href = location.href + '#{$cat_id}'");
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <p>On this page, you may choose the order in which simulations appear for every category.
            Note that simulations appear in this order only in the thumbnail view.</p>

            <p>If the simulations ordering is wonky (can be seen by several sims sharing the same
            "order id" number, <a href="organize-sims.php?auto_order=1">clicking here</a> will reorder them as they appear
            on the screen right now.  This should resolves most sorting problems.</p>

            <p>You may also need to <a href="cache-clear.php?cache=all">clear the cache</a>.</p>

EOT;

        foreach(sim_get_categories() as $category) {
            $cat_id   = $category['cat_id'];
            $cat_name = format_string_for_html($category['cat_name']);

            print <<<EOT
                <h2 id="cat_$cat_id">$cat_name</h2>

                <table>
                    <thead>
                        <tr>
                            <td>Simulation</td> <td>Operations</td> <td>order id</td>
                        </tr>
                    </thead>

                    <tbody>

EOT;

            $auto_order = 1;

            foreach(sim_get_sim_listings_by_cat_id($cat_id) as $sim_listing) {
                $simulation_listing_id = $sim_listing["simulation_listing_id"];
                $simulation_listing_order = $sim_listing["simulation_listing_order"];

                $sim = sim_get_sim_by_id($sim_listing['sim_id']);

                $sim_name = format_string_for_html($sim['sim_name']);

                if (isset($_REQUEST['auto_order'])) {
                    db_exec_query("UPDATE `simulation_listing` SET `simulation_listing_order`='$auto_order' WHERE `simulation_listing_id`='$simulation_listing_id' ");
                    $simulation_listing_order = $auto_order;
                }

                print <<<EOT
                    <tr>
                        <td>$sim_name</td>

                        <td>
                            <a href="organize-sims.php?action=move_up&amp;simulation_listing_id=$simulation_listing_id&amp;cat_id=$cat_id">up</a>
                            <a href="organize-sims.php?action=move_down&amp;simulation_listing_id=$simulation_listing_id&amp;cat_id=$cat_id">down</a>
                        </td>

                        <td>
                            $simulation_listing_order
                        </td>
                    </tr>

EOT;
                $auto_order++;
            }

            print <<<EOT
                    </tbody>
                </table>

EOT;
        }
    }

}

$page = new OrganizeSimulationsPage("Organize Simulations", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>