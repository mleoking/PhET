<?php get_header(); ?>

	<div id="content-wrap">
	<div id="content">
	<div class="gap">

		<?php if (have_posts()) : ?>

 	  <?php $post = $posts[0]; // Hack. Set $post so that the_date() works. ?>
 	  <?php /* If this is a category archive */ if (is_category()) { ?>
		<h2 class="pagetitle"><?php _e('Archive for the &#8216;', 'yashfa' ); ?><?php single_cat_title(); ?>&#8217;<?php _e('Category','yashfa')?></h2>
 	  <?php /* If this is a tag archive */ } elseif( is_tag() ) { ?>
		<h2 class="pagetitle"><?php _e('Posts Tagged &#8216;', 'yashfa'); ?><?php single_tag_title(); ?>&#8217;</h2>
 	  <?php /* If this is a daily archive */ } elseif (is_day()) { ?>
		<h2 class="pagetitle"><?php _e('Archive for ','yashfa'); ?><?php the_time('F jS, Y'); ?></h2>
 	  <?php /* If this is a monthly archive */ } elseif (is_month()) { ?>
		<h2 class="pagetitle"><?php _e('Archive for ','yashfa');?><?php the_time('F, Y'); ?></h2>
 	  <?php /* If this is a yearly archive */ } elseif (is_year()) { ?>
		<h2 class="pagetitle"><?php _e('Archive for ','yashfa'); ?><?php the_time('Y'); ?></h2>
	  <?php /* If this is an author archive */ } elseif (is_author()) { ?>
		<h2 class="pagetitle"><?php _e('Author Archive','yashfa')?></h2>
 	  <?php /* If this is a paged archive */ } elseif (isset($_GET['paged']) && !empty($_GET['paged'])) { ?>
		<h2 class="pagetitle"><?php _e('Blog Archives','yashfa'); ?></h2>
 	  <?php } ?>


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

		<h2 class="center"><?php _e('Not Found','yashfa')?></h2>
		<?php include (TEMPLATEPATH . '/searchform.php'); ?>

	<?php endif; ?>

	</div>
	</div>	</div>
<?php get_sidebar(); ?>

<?php get_footer(); ?>
