#!/web/chroot/phet/usr/local/php/bin/php
<?php

// Set the current directory to the script directory
chdir(dirname(__FILE__));

// SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/sim-utils.php");
require_once("include/cache-utils.php");

$sims = sim_get_all_sims();

foreach ($sims as $sim) {
    print "sim: {$sim['sim_name']}\n";
    sim_auto_calc_sim_size($sim['sim_id']);
}

cache_clear_simulations();

?>