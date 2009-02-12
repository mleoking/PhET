<?php
	// fake version script
        $sim_name = $_GET['sim'];
	
	$fake_installer_timestamp = 1233710554;
	
	if($sim_name == "geometric-optics") {
		// not in installer
		$fake_sim_timestamp = 1234710554;
	} else {
		// should be in installer
		$fake_sim_timestamp = 1232710554;
	}
	
	print <<<VI
<?xml version="1.0"?>
<sim-version-info
project="{$sim_name}"
sim="{$sim_name}"
version="1.00.00"
revision="29000"
timestamp="{$fake_sim_timestamp}"
installer_timestamp="{$fake_installer_timestamp}" />
VI;
	
?>