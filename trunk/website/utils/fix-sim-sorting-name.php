<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."admin/sim-utils.php");
include_once(SITE_ROOT."admin/cache-utils.php");

$sims = sim_get_all_sims();

foreach ($sims as $sim) {
  $new_sim_info = array();
  $new_sim_info['sim_id'] = $sim['sim_id'];
  $new_sim_info['sim_sorting_name'] = get_sorting_name($sim['sim_name']);
  sim_update_sim($new_sim_info);
}

cache_clear_simulations();

?>