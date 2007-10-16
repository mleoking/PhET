<?php

	if (!defined('SITE_ROOT')) {
		include_once("../admin/global.php");
	}
	
	include_once(SITE_ROOT."admin/sys-utils.php");

	define("WEBPAGES_CACHE", 			"webpages");
	define("HOURS_TO_CACHE_WEBPAGES", 	1);
	
	$g_disable_all_caching = false;//true;
	
	function create_proper_ownership($file) {
		exec('chmod 775 '.$file);
		
		if (is_dir($file)) {
			exec('chgrp --recursive phet '.$file);
		}
		else {
			exec('chgrp phet '.$file);
		}
	}
	
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
			create_proper_ownership($cache_dir);
		}
		
		$resource_location = cache_get_file_location($cache_name, $resource_name);
		
		$return_value = flock_put_contents($resource_location, $resource_contents);
		
		create_proper_ownership($resource_location);
		
		return $return_value;
	}
	
	function cache_get($cache_name, $resource_name, $expiration_hours = false) {
		global $g_disable_all_caching;
		
		if ($g_disable_all_caching) return false;
		
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
	
	function cache_auto_get_page_name() {
		$hash_contents = $_SERVER['REQUEST_URI'];
		
		foreach ($_SESSION as $key => $value) {
			$hash_contents .= "$key=>$value";
		}
		
		return md5($hash_contents).'.html';
	}
	
	/**
	 * Starts caching the current webpage. Must be called before any content printed.
	 */
	function cache_auto_start() {		
		$page_name = cache_auto_get_page_name();
		
		$cached_page = cache_get(WEBPAGES_CACHE, $page_name, HOURS_TO_CACHE_WEBPAGES);
		
		if ($cached_page) {
			print $cached_page;
			
			exit;
		}
		else {
			ob_start();
		}
	}
	
	/**
	 * Ends caching the current webpage. Must be called after all content printed.
	 */
	function cache_auto_end() {
		$page_name = cache_auto_get_page_name();
		
		$page_contents = ob_get_contents();
		
		$page_contents = preg_replace('/^ +/',       '',   $page_contents);		
		$page_contents = preg_replace('/[ \t]{2,}/', ' ',  $page_contents);

		cache_put(WEBPAGES_CACHE, $page_name, $page_contents);
			
		ob_end_flush();
	}

?>