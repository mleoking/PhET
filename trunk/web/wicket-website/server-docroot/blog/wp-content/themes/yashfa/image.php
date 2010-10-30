<?php get_header(); ?>

	<div id="content-wrap">
	<div id="content">
	<div class="gap">

  <?php if (have_posts()) : while (have_posts()) : the_post(); ?>

		<div class="post" id="post-<?php the_ID(); ?>">
			<h2><a href="<?php echo get_permalink($post->post_parent); ?>" rev="attachment"><?php echo get_the_title($post->post_parent); ?></a> &raquo; <?php the_title(); ?></h2>
			<div class="entry">
				<p class="attachment"><a href="<?php echo wp_get_attachment_url($post->ID); ?>"><?php echo wp_get_attachment_image( $post->ID, __('medium','yashfa')); ?></a></p>
                <div class="caption"><?php if ( !empty($post->post_excerpt) ) the_excerpt(); // this is the "caption" ?></div>

				<?php the_content('<p class="serif">',__('Read the rest of this entry &raquo;','yashfa'),'</p>'); ?>

				<div class="navigation">
					<div class="alignleft"><?php previous_image_link() ?></div>
					<div class="alignright"><?php next_image_link() ?></div>
				</div>
				<br class="clear" />

				<p class="postmetadata alt">
					<small>
						<?php _e('This entry was posted on ','yashfa');?><?php the_time('l, F jS, Y') ?> at <?php the_time() ?>
						<?php _e('and is filed under ','yashfa');?><?php the_category(', ') ?>.
						<?php the_taxonomies(); 
						__('You can follow any responses to this entry through the?','yashfa'); post_comments_feed_link('RSS 2.0');, _e('feed.','yashfa'); ?>

						<?php if (('open' == $post-> comment_status) && ('open' == $post->ping_status)) {
							// Both Comments and Pings are open ?>
							<?php _e('You can ','yashfa');?><a href="#respond"><?php _e('leave a response','yashfa');?></a><?php _e(', or ','yashfa');?><a href="<?php trackback_url(); ?>" rel="trackback"><?php _e('trackback','yashfa');?></a><?php _e('from your own site.','yashfa');?>

						<?php } elseif (!('open' == $post-> comment_status) && ('open' == $post->ping_status)) {
							// Only Pings are Open ?>
							<?php _e('Responses are currently closed, but you can ','yashfa');<a href="<?php trackback_url(); ?> " rel="trackback"><?php _e('trackback','yashfa');?></a><?php _e('from your own site.','yashfa');?>

						<?php } elseif (('open' == $post-> comment_status) && !('open' == $post->ping_status)) {
							// Comments are open, Pings are not 
							_e('You can skip to the end and leave a response. Pinging is currently not allowed.','yashfa');?>

						<?php } elseif (!('open' == $post-> comment_status) && !('open' == $post->ping_status)) {
							// Neither Comments, nor Pings are open 
							_e('Both comments and pings are currently closed.','yashfa');?>

						<?php } edit_post_link(__('Edit this entry.','yashfa'),'',''); ?>

					</small>
				</p>

			</div>

		</div>

      <?php comments_template('', true); ?>

	<?php endwhile; else: ?>

		<p><?php _e('Sorry, no attachments matched your criteria.','yashfa');?></p>

<?php endif; ?>

	</div>
	</div>
	</div>
<?php get_footer(); ?>
