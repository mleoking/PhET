<?php
    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/password-protection.php");
    include_once(SITE_ROOT."admin/db.inc");	
    include_once(SITE_ROOT."admin/site-utils.php");

    function print_selection() {
        print <<<EOT
            <h1>Choose Simulation</h1>
            
            <p>
                Please choose the simulation to edit from the list below.
            </p>
            
            <form action="edit-sim.php" method="post">            
                <p>            
                    <select name="sim_id">
EOT;

        $select_simulations_st = "SELECT * FROM `simulation` ORDER BY `sim_name` ASC ";
        $simulation_table      = mysql_query($select_simulations_st);
	
        while ($sim = mysql_fetch_row($simulation_table)) {      
            $sim_id   = $sim[0];
            $simtitle = format_string_for_html($sim[1]);
	
            //print drop down menu
            print "<option value=\"$sim_id\">$simtitle</option>";
        }

        // close drop down menu and form
        print <<<EOT
                    </select>
                    
                <input type="submit" value="edit" />
                </p>
            </form>
EOT;
    }
    
    print_site_page('print_selection', 9);
?>
