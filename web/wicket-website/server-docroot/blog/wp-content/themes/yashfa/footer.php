
<hr />
</div>
</div>

<div id="more-footer">
<div id="more-more-footer">
<div class="inner-one inner">
<div class="grap">
<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Extra Bottom Left') ) : ?>
<h3><?php _e('Recent Comments'); ?></h3>
<?php
global $wpdb;
$sql = "SELECT DISTINCT ID, post_title, post_password, comment_ID,
comment_post_ID, comment_author, comment_date_gmt, comment_approved,
comment_type,comment_author_url,
SUBSTRING(comment_content,1,30) AS com_excerpt
FROM $wpdb->comments
LEFT OUTER JOIN $wpdb->posts ON ($wpdb->comments.comment_post_ID =
$wpdb->posts.ID)
WHERE comment_approved = '1' AND comment_type = '' AND
post_password = ''
ORDER BY comment_date_gmt DESC
LIMIT 10";
$comments = $wpdb->get_results($sql);
$output = $pre_HTML;
$output .= "\n<ul>";
foreach ($comments as $comment) {
$output .= "\n<li>".strip_tags($comment->comment_author)
.":" . "<a href=\"" . get_permalink($comment->ID) .
"#comment-" . $comment->comment_ID . "\" title=\"on " .
$comment->post_title . "\">" . strip_tags($comment->com_excerpt)
."</a></li>";
}
$output .= "\n</ul>";
$output .= $post_HTML;
echo $output;?>
<?php endif; ?>
</div>
</div>

<div class="inner-two inner">
<div class="grap">
<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Extra Bottom Center') ) : ?>
<h3><?php _e('Tags Cloud','yashfa')?></h3>
          <?php wp_tag_cloud(); ?>
<?php endif; ?>
</div>
</div>

<div class="inner-three inner">
<div class="grap">
<?php if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Extra Bottom Right') ) : ?>
<?php J_ShowRecentPosts(); ?>
<?php endif; ?>
</div>
</div>

</div>
</div>
<br class="clear" />
<div id="extra-footer">

<div id="footer">
	<p>
		<?php bloginfo(__('name','yashfa')); _e('is proudly powered by','yashfa');?>
		<a href="http://wordpress.org/">WordPress</a>
		<br /><a href="<?php bloginfo('rss2_url'); ?>"><?php _e('Entries (RSS)','yashfa');?></a>
		<?php _e('and','yashfa')?> <a href="<?php bloginfo('comments_rss2_url'); ?>"><?php _e('Comments (RSS)','yashfa');?></a>.
		<!-- <?php echo get_num_queries(); ?> queries. <?php timer_stop(1); ?> seconds. -->
	</p>
	<p class="right"><a href="http://wpgpl.com/themes/yashfa" title="Free WordPress Theme: Yashfa">Yashfa</a> ver. 1.7 <?php _e('created by','yashfa')?> <a href="http://wpgpl.com" title="WordPress GPL Themes & Plugins">WP GPL</a></p>
</div>
</div>

		<?php wp_footer(); ?>
</body>
</html>
