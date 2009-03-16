#!/web/chroot/phet/usr/local/php/bin/php
<?php

// Set the current directory to the script directory
chdir(dirname(__FILE__));

// SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/cache-utils.php");

foreach (SimFactory::inst()->getAllSims(TRUE) as $sick_sim) {
    $simulation = array();
    $simulation['sim_id'] = $sick_sim->getId();
    $simulation['sim_sorting_name'] = 
        SimUtils::inst()->generateSortingName(
            $sick_sim->getWrapped()->getName()
            );
  
    db_update_table(
        'simulation',
        $simulation,
        'sim_id',
        $simulation['sim_id']
        );
}

cache_clear_simulations();

?>
