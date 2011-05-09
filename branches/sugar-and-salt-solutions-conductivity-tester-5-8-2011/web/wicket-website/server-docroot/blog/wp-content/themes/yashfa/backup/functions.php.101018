<?php //update_option( 'sidebars_widgets', $null ); ?>
<?php
$totale = get_option('posts_per_page');

function J_ShowRecentPosts() {?>
<div class="last">
<h3><span><?php _e('Latest Post','yashfa');?></span> <span class="infopost"><?php previous_posts_link(__('Previous //','yashfa')) ?> <?php next_posts_link(__('More','yashfa')) ?></span></h3>
<ul class="recent">
<?php
if(is_home()):
$postoff = $totale;
else:
$postoff = '';
endif;

$temp = $wp_query;
$wp_query= null;
$wp_query = new WP_Query();

$wp_query->query('showposts=10'.'&paged='.$paged.$postoff);
?>
<?php while ($wp_query->have_posts()) : $wp_query->the_post(); ?>
	<li><a href="<?php the_permalink() ?>" rel="bookmark"><?php the_title(); ?></a> <?php the_time('d m'); ?></li>
<?php endwhile; ?>
</ul>
<?php if($postoff == $totale): ?>
  
</div>
<?php endif; ?>
<?php $wp_query = null; $wp_query = $temp;?>
<br class="clear" />
</div>

<?php }	
// WIDEGT OPTIONS
if ( function_exists('register_sidebar') ) {
	register_sidebar(array(
		'name'=>'Sidebar Right',
        'before_widget' => '<li id="%1$s" class="boxr widget %2$s">',
        'after_widget' => '</li>',
        'before_title' => '<h3 class="widgettitle"><span>',
        'after_title' => '</span></h3>'
	));
	register_sidebar(array(
		'name'=>'Extra Bottom Left',
        'before_widget' => '<div id="%1$s" class="widget %2$s">',
        'after_widget' => '</div>',
        'before_title' => '<h3 class="widgettitle"><span>',
        'after_title' => '</span></h3>'
	));
	register_sidebar(array(
		'name'=>'Extra Bottom Center',
        'before_widget' => '<div id="%1$s" class="widget %2$s">',
        'after_widget' => '</div>',
        'before_title' => '<h3 class="widgettitle"><span>',
        'after_title' => '<span></h3>'
	));
	register_sidebar(array(
		'name'=>'Extra Bottom Right',
        'before_widget' => '<div id="%1$s" class="widget %2$s">',
        'after_widget' => '</div>',
        'before_title' => '<h3 class="widgettitle"><span>',
        'after_title' => '</span></h3>'
	));

}

define('HEADER_IMAGE', '%s/images/header.jpg'); // %s is theme dir uri
define('HEADER_IMAGE_WIDTH', 950);
define('HEADER_IMAGE_HEIGHT', 170);
define('HEADER_TEXTCOLOR', 'FFF');

function admin_header_style() { ?>
<style type="text/css">
#headimg, #header {
	background: #333 url(<?php header_image();?>) 0 0 no-repeat;
	height: <?php echo HEADER_IMAGE_HEIGHT;?>px;
	width: <?php echo HEADER_IMAGE_WIDTH;?>px;
	position: relative;
	z-index: 499;
}
<?php if ( 'blank' == get_header_textcolor() ) { ?>  
#header h1 a, #headimg h1 {
 	display: none;
}
#header .description {
	display: none;
}
<?php
} else {
?>  #header h1 a, #headimg h1 a {
	color: #fff;
	position: absolute;
	left: -5px;
	top: -14px;
	font-size: 60px;
	letter-spacing: -10px;
	text-transform: uppercase;
}
#header .description, #headimg #desc {
	position: absolute;
	top: 50px;
	left: 0;
	border-left: 30px solid #ED3300;
	padding-left: 5px;
	color: #ccc;
	font-size: 30px;
}
<?php
}
?>
</style>
<?php }

function header_style() { ?>
<style type="text/css">
#header {
 background: #333 url(<?php header_image();
?>) 0 0 no-repeat;
 height: <?php echo HEADER_IMAGE_HEIGHT;
?>px;
 width: <?php echo HEADER_IMAGE_WIDTH;
?>px;
	position: relative;
}
 <?php if ( 'blank' == get_header_textcolor() ) {
?> #header h1 a, #header .description {
 display: none;
}
<?php
}
?>
</style>
<?php }
if ( function_exists('add_custom_image_header') ) {
  add_custom_image_header('header_style', 'admin_header_style');
} 

/* WordPress 2.7 and Later on */
add_filter('comments_template', 'legacy_comments');
function legacy_comments($file) {
	if(!function_exists('wp_list_comments')) 	$file = TEMPLATEPATH . '/old.comments.php';
	return $file;
}

function list_pings($comment, $args, $depth) {
       $GLOBALS['comment'] = $comment;
?>
<li id="comment-<?php comment_ID(); ?>">
  <?php comment_author_link(); ?>
  <span>
  <?php comment_date('d m y'); ?>
  </span>
  <?php } 

function list_comment($comment, $args, $depth) {
   $GLOBALS['comment'] = $comment; ?>
<li <?php comment_class(); ?> id="comment-<?php comment_ID() ?>">
  <div id="div-comment-<?php comment_ID() ?>" class="thechild">
    <div class="cleft">
      <?php echo get_avatar($comment, 60); ?>
      
      </div>
    <div class="cright"> 
    <div class="comment-author vcard by"> <?php printf('<span class="fn">%s</span>', get_comment_author_link()) ?> <a href="<?php echo htmlspecialchars( get_comment_link( $comment->comment_ID ) ) ?>">#</a> </div>
      <div class="comment-meta commentmetadata"><?php printf('%1$s %2$s', get_comment_date('m.d.Y'),  get_comment_time('H:i')) ?>
        <?php edit_comment_link(__('(e)','yashfa'),'  ','') ?>
      </div>
	<span class="numero"><?php global $cmntCnt; ?><?php echo $cmntCnt+1; ?><?php $cmntCnt = $cmntCnt + 1; ?>
</span>
    
      <?php if ($comment->comment_approved == '0') : ?>
      <em>
      <?php _e('Your comment is awaiting moderation.') ?>
      </em> <br />
      <?php endif; ?>
      <div class="texe"><?php comment_text() ?></div>
      <div class="reply">
        <?php comment_reply_link(array_merge( $args, array('add_below' => 'div-comment', 'depth' => $depth, 'max_depth' => $args['max_depth']))) ?>
      </div>
    </div>
<div class="clear"></div>
  </div>
<div class="clear"></div>

</li>
<?php
        }
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