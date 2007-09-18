<?php

	if (!defined('SITE_ROOT')) {
		include_once("../admin/global.php");
	}
	
	include_once(SITE_ROOT."admin/sys-utils.php");
	
	function cache_get_location($cache_name) {
		return "./cached-$cache_name";
	}
	
	function cache_get_file_location($cache_name, $resource_name) {
		return cache_get_location($cache_name)."/$resource_name";
	}
	
	function cache_clear($cache_name) {
		exec('rm -rf '.cache_get_location($cache_name));
	}
	
	function cache_put($cache_name, $resource_name, $resource_contents) {
		$cache_dir = cache_get_location($cache_name);
		
		if (!file_exists($cache_dir)) {
			mkdir($cache_dir);
		
			exec('chmod 775 '.$cache_dir);
		}
		
		$resource_location = cache_get_file_location($cache_name, $resource_name);
		
		$return_value = flock_put_contents($resource_location, $resource_contents);
		
		exec('chmod 775 '.$resource_location);
		
		return $return_value;
	}
	
	function cache_get($cache_name, $resource_name, $expiration_hours = false) {
		$resource_location = cache_get_file_location($cache_name, $resource_name);
		
		if (!file_exists($resource_location)) return false;
		
		if (is_numeric($expiration_hours)) {
			$time = filemtime($resource_location);
		
			$diff = time() - $time;
		
			// Refresh the cache every 24 hours:
			if ($diff > $expiration_hours * 60 * 60) {			
				return false;
			}
		}
		
		return flock_get_contents($resource_location);
	}

?>