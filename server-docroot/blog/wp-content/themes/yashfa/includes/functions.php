<?php
/**
 * count for Trackback, pingback, comment, pings
 *
 * use it:
 * fb_comment_type_count('ping');
 * fb_comment_type_count('comment');
 */
if ( !function_exists('fb_comment_type_count') ) {
	function fb_get_comment_type_count($type='all', $post_id = 0) {
		global $cjd_comment_count_cache, $id, $post;
 
		if ( !$post_id )
			$post_id = $post->ID;
		if ( !$post_id )
			return;
 
		if ( !isset($cjd_comment_count_cache[$post_id]) ) {
			$p = get_post($post_id);
			$p = array($p);
			update_comment_type_cache($p);
		}
 
		if ( $type == 'pingback' || $type == 'trackback' || $type == 'comment' )
			return $cjd_comment_count_cache[$post_id][$type];
		elseif ( $type == 'pings' )
			return $cjd_comment_count_cache[$post_id]['pingback'] + $cjd_comment_count_cache[$post_id]['trackback'];
		else
			return array_sum((array) $cjd_comment_count_cache[$post_id]);
	}
 
	// comment, trackback, pingback, pings
	function fb_comment_type_count($type = 'all', $post_id = 0) {
		echo fb_get_comment_type_count($type, $post_id);
	}
}

 function update_comment_type_cache(&$queried_posts) {
global $cjd_comment_count_cache, $wpdb;

if ( !$queried_posts )
return $queried_posts;

foreach ( (array) $queried_posts as $post )
if ( !isset($cjd_comment_count_cache[$post->ID]) )
$post_id_list[] = $post->ID;

if ( $post_id_list ) {
$post_id_list = implode(',', $post_id_list);

foreach ( array('','pingback', 'trackback') as $type ) {
$counts = $wpdb->get_results("SELECT ID, COUNT( comment_ID ) AS ccount
FROM $wpdb->posts
LEFT JOIN $wpdb->comments ON ( comment_post_ID = ID AND comment_approved = '1' AND comment_type='$type' )
WHERE post_status = 'publish' AND ID IN ($post_id_list)
GROUP BY ID");

if ( $counts ) {
if ( '' == $type )
$type = 'comment';
foreach ( $counts as $count )
$cjd_comment_count_cache[$count->ID][$type] = $count->ccount;
}
}
}
return $queried_posts;
}
?>