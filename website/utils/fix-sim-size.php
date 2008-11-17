<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."admin/sim-utils.php");
include_once(SITE_ROOT."admin/cache-utils.php");

$sims = sim_get_all_sims();

foreach ($sims as $sim) {
    sim_auto_calc_sim_size($sim['sim_id']);
}

cache_clear_simulations();

?>