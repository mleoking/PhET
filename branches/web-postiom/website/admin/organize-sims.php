<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/ordering-util.php");

class OrganizeSimulationsPage extends SitePage {

    function handle_action($action, $simulation_listing_id, $cat_id) {
        $condition = array('cat_id' => $cat_id);

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

        if (isset($_REQUEST['auto_order'])) {
            CategoryUtils::inst()->fixSimListingOrder();
        }

        print <<<EOT
            <p>On this page, you may choose the order in which simulations appear for every category.
            Note that simulations appear in this order only in the thumbnail view.</p>

            <p><strong>Sim <em>order id</em> wonky?  <a href="organize-sims.php?auto_order=1">Clicking here</a></strong>
            will fix it.  "Wonky" is when several sims sharing the same
            "order id" number, and using that link will number them according to the order you see here.</p>

            <p>You may also need to <a href="cache-clear.php?cache=all">clear the cache</a>.</p>

EOT;

        foreach(CategoryUtils::inst()->getAllCategories() as $category) {
            $cat_id   = $category['cat_id'];
            $cat_name = WebUtils::inst()->toHtml($category['cat_name']);

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

            $listing_order = CategoryUtils::inst()->getSimListingOrder($cat_id);
            foreach($listing_order as $sim_listing) {
                $simulation_listing_id = $sim_listing["simulation_listing_id"];
                $simulation_listing_order = $sim_listing["simulation_listing_order"];

                try {
                    $sim = SimFactory::inst()->getById($sim_listing['sim_id']);
                }
                catch (PhetSimException $e) {
                    // TODO: log an error, remove the invalid listing
                    //print "<tr><td>Invalid id #{$sim_listing['sim_id']}</td><td>no action</td><td>{$simulation_listing_order}</td></tr>";
                    continue;
                }

                $sim_name = $sim->getName();

                print <<<EOT
                    <tr>
                        <td>$sim_name</td>

                        <td>
                            <a href="organize-sims.php?action=move_up&amp;simulation_listing_id={$simulation_listing_id}&amp;cat_id={$cat_id}">up</a>
                            <a href="organize-sims.php?action=move_down&amp;simulation_listing_id={$simulation_listing_id}&amp;cat_id={$cat_id}">down</a>
                        </td>

                        <td>
                            $simulation_listing_order
                        </td>
                    </tr>

EOT;
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