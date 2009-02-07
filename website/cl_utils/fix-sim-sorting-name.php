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
  $new_sim_info = array();
  $new_sim_info['sim_id'] = $sim['sim_id'];
  $new_sim_info['sim_sorting_name'] = get_sorting_name($sim['sim_name']);
  sim_update_sim($new_sim_info);
}

cache_clear_simulations();

?>