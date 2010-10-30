<?php get_header(); ?>

	<div id="content-wrap">
	<div id="content">
	<div class="gap">

	<?php if (have_posts()) : ?>

		<h2 class="pagetitle"><?php _e('Search Results','yashfa')?></h2>

		<div class="navigation">
			<div class="alignleft"><?php next_posts_link(__('&laquo; Older Entries','yashfa')) ?></div>
			<div class="alignright"><?php previous_posts_link(__('Newer Entries &raquo;','yashfa')) ?></div>
		</div>


		<?php while (have_posts()) : the_post(); ?>

			<div class="post" id="post-<?php the_ID(); ?>">

				<h2><a href="<?php the_permalink() ?>" rel="bookmark" title="Permanent Link to <?php the_title_attribute(); ?>"><?php the_title(); ?></a></h2>

<div class="posties clearfix">
<div class="one">
				
				<div class="entry">
					<?php the_content(__('Read the rest of this entry &rarr;','yashfa')); ?>
				</div>

<div class="tagged">
<?php the_tags(__('Tags: ','yashfa'), ', ', '<br />'); ?>
</div>

</div>
<div class="two">
<div class="dater">
<h3 style="font-size: 150%;"><?php the_time('D d') ?></h3>
<h5><?php the_time('M Y') ?></h5>
</div>
<p class="metadata"><br /><?php _e('by','yashfa'); ?> <?php  the_author() ?> <br /><?php _e('posted in','yashfa'); ?> <?php  the_category(', ') ?> <?php edit_post_link(__('Edit','yashfa'), '', ''); ?><br /><?php comments_popup_link(__('No Comments','yashfa'), __('1 Comment','yashfa'), __( '% Comments','yashfa')); ?></p>
</div>
				</div>

				</div>
		<?php endwhile; ?>

		<div class="navigation">
			<div class="alignleft"><?php next_posts_link(__('&laquo; Older Entries','yashfa')) ?></div>
			<div class="alignright"><?php previous_posts_link(__('Newer Entries &raquo;','yashfa')) ?></div>
		</div>

	<?php else : ?>

		<h2 class="center"><?php _e('No posts found. Try a different search?','yashfa')?></h2>
		<?php include (TEMPLATEPATH . '/searchform.php'); ?>

	<?php endif; ?>

	</div>
	</div>
	</div>	
<?php get_sidebar(); ?>

<?php get_footer(); ?>
