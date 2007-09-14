<?php

	include_once("../admin/global.php");
	
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
		
		return file_put_contents($resource_location, $resource_contents);
	}
	
	function cache_get($cache_name, $resource_name) {
		$resource_location = cache_get_file_location($cache_name, $resource_name);
		
		if (!file_exists($resource_location)) return false;
		
		return file_get_contents($resource_location);
	}

?>