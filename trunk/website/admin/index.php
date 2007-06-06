<?php
    include_once("../admin/password-protection.php");

    function print_control_panel() {       
        print <<<EOT
            <h1>PhET Administration Control Panel</h1>

            <ul>
                <li><a href="new-sim.php">Add Simulation</a></li>

                <li><a href="choose-sim.php">Edit Existing Simulation</a></li>

                <li><a href="list-sims.php">List Simulations</a></li>

                <li><a href="organize-cats.php">Organize Categories</a></li>
        
                <li><a href="manage-contributors.php">Manage Contributors</a></li>
                
                <li><a href="compose-newsletter.php">Compose Newsletter</a></li>
            </ul>
EOT;
    }
    
    print_site_page('print_control_panel', 9);
?>