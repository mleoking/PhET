<?php
	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/sys-utils.php");
	
	define("CACHE_ROOT", "./cached-thumbnails/");
	
	function get_file_for_image($sim_image_url) {
		$file_name = md5($sim_image_url);
		
		return CACHE_ROOT.$file_name;
	}

	function cached_image_out_of_date($sim_image_url) {
		$file = get_file_for_image($sim_image_url);
		
		if (!file_exists($file)) return true;
		
		$last_modified = filemtime($file);
		$now = time();
		
		if ($now - $last_modified > 60 * 60 * 24) return true;
		
		return false;
	}

	$sim_id = $_REQUEST['sim_id'];
	
	$sim = sim_get_sim_by_id($sim_id);
	
	$sim_image_url = sim_get_screenshot($sim);
	
	mkdirs(CACHE_ROOT);

	$file = get_file_for_image($sim_image_url);	

	if (cached_image_out_of_date($sim_image_url)) {
		// Load image -- assume image is in png format (for now):
		$img = imagecreatefrompng($sim_image_url);

		$existing_width  = imagesx($img);
		$existing_height = imagesy($img);

		// Scale to thumbnail width, preserving aspect ratio:
		$new_width  = SIM_THUMBNAIL_WIDTH;
		$new_height = SIM_THUMBNAIL_HEIGHT;//floor($existing_height * ( SIM_THUMBNAIL_WIDTH / $existing_width ));

		$tmp_img = imagecreatetruecolor($new_width, $new_height);

		imagecopyresized($tmp_img, $img, 0, 0, 0, 0, $new_width, $new_height, $existing_width, $existing_height);

		// Output image to cached image location:
		imagejpeg($tmp_img, $file);
	}
	
	send_file_to_browser($file, file_get_contents($file), 'image/jpeg');
?>