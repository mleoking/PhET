<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/password-protection.php");
    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/ordering-util.php");

    function print_sims() {
        if (isset($_REQUEST['cat_id'])) {
            $cat_id = $_REQUEST['cat_id'];

            print <<<EOT
                <script type="text/javascript">
                    /*<![CDATA[*/
                    $(document).ready(
                        function() {
                            location.href = location.href + '#$cat_id';
                        }
                    );
                    /*]]>*/
                </script>
EOT;
        }

        print <<<EOT

            <h1>Organize Simulations</h1>

            <p>On this page, you may choose the order in which simulations appear for every category.
            Note that simulations appear in this order only in the thumbnail view.</p>
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
                eval(get_code_to_create_variables_from_array($sim_listing));

                $sim = sim_get_sim_by_id($sim_listing['sim_id']);

                $sim_name = format_string_for_html($sim['sim_name']);

                if (isset($_REQUEST['auto_order'])) {
                    db_exec_query("UPDATE `simulation_listing` SET `simulation_listing_order`='$auto_order' WHERE `simulation_listing_id`='$simulation_listing_id' ");
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

    function handle_action($action, $simulation_listing_id, $cat_id) {
        $condition = array( 'cat_id' => $cat_id );

        if ($action == 'move_up') {
            order_move_higher('simulation_listing', $simulation_listing_id, $condition);
        }
        else if ($action == 'move_down') {
            order_move_lower('simulation_listing', $simulation_listing_id, $condition);
        }
    }

    if (isset($_REQUEST['action']) && isset($_REQUEST['simulation_listing_id']) && isset($_REQUEST['cat_id'])) {
        handle_action($_REQUEST['action'], $_REQUEST['simulation_listing_id'], $_REQUEST['cat_id']);
    }

    print_site_page('print_sims', 9);

?>