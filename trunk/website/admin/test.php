<?php

	include_once("sim-utils.php");
	
	foreach(sim_get_all_sims() as $simulation) {
		sim_auto_calc_sim_size($simulation['sim_id']);
	}
    
?>