<?php get_header(); ?>

	<div id="content-wrap">
	<div id="content">
	<div class="gap">

		<?php if (have_posts()) : while (have_posts()) : the_post(); ?>
		<div class="post" id="post-<?php the_ID(); ?>">
		<h2><?php the_title(); ?></h2>
			<div class="entry">
				<?php the_content('<p class="serif">'.__('Read the rest of this page &raquo;','yashfa').'</p>'); ?>

				<?php wp_link_pages(array(__('before','yashfa') => '<p><strong>',__('Pages:','yashfa'),'</strong> ',__('after','yashfa') => '</p>','next_or_number' => __('number','yashfa'))); ?>

			</div>
		</div>
		<?php endwhile; endif; ?>
	<?php edit_post_link(__('Edit this entry.','yashfa'), '<p>', '</p>'); ?>

      <?php comments_template('', true); ?>

	</div>
	</div>	</div>
<?php get_sidebar(); ?>

<?php get_footer(); ?>