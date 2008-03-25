<?php
    include_once("../admin/password-protection.php");

    function print_control_panel() {
        print <<<EOT
            <h1>PhET Administration Control Panel</h1>

            <h3>Simulations</h3>
            <ul>
                <li><a href="new-sim.php">Add Simulation</a></li>

                <li><a href="choose-sim.php">Edit Existing Simulation</a></li>

                <li><a href="list-sims.php">List Simulations</a></li>

                <li><a href="organize-cats.php">Organize Categories</a></li>

                <li><a href="organize-sims.php">Organize Simulations</a></li>
            </ul>

            <h3>Contributions</h3>
            <ul>
                <li><a href="manage-contributors.php">Manage Contributors</a></li>
            </ul>

            <h3>Database</h3>
            <ul>
                <li><a href="db-check-integrity.php">Check database integrity</a></li>

                <li><a href="manage-db.php">Manage Database</a></li>
            </ul>

            <h3>Misc</h3>
            <ul>
                <li><a href="compose-newsletter.php">Compose Newsletter</a></li>

                <li><a href="view-statistics.php">View Statistics</a></li>

                <li><a href="manage-db.php">Manage Database</a></li>
            </ul>

            <h3>Web page caching</h3>
            <ul>
                <li><a href="cache-clear-sims.php">Clear the simulation cache</a></li>
                <li><a href="cache-clear-activitiesdb.php">Clear the activities cache</a></li>
                <li><a href="cache-clear-base.php">Clear the base directory cache</a></li>
                <li><a href="cache-clear-all.php">Clear all the caches</a></li>
            </ul>

EOT;
    }

    print_site_page('print_control_panel', 9);
?>