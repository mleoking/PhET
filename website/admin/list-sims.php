<?
    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/password-protection.php");
	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");	

    function print_sims_list() {
        print "<h1>Simulation Listing</h1>";

        print "<table class=\"compact\">";

        print "<thead><tr>";
        print "<td></td>";
        print "</tr></thead>";
        print "<tbody>";

        foreach (sim_get_all_sims() as $simulation) {
            print "<tr><td><h3>".$simulation['sim_name']."</h3>";
            print "<a href=\"delete-sim.php?sim_id=$sim_id&amp;delete=0\">Delete</a>, ";
            print "<a href=\"edit-sim.php?sim_id=$sim_id\">Edit</a>";
            print "</td></tr>";
        
            foreach($simulation as $key => $value) {
                if ($key != 'sim_name') {
                    print "<tr><td>$key</td><td>$value</td></tr>";
                }
            }
        
            $sim_id = $simulation['sim_id'];
        
;
            print "<tr><td>&nbsp;</td></tr>";
        }
        
        if (!$first_time) {
            print "</tbody>";
        }
        
        print "</table>";
    }
    
    print_site_page('print_sims_list', 9);
?>
