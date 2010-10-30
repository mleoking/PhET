	<div id="sidebar">
		<ul>
			<?php 	/* Widgetized sidebar, if you have the plugin installed. */
					if ( !function_exists('dynamic_sidebar') || !dynamic_sidebar('Sidebar Right') ) : ?>
			<li class="boxr search">
				<?php include (TEMPLATEPATH . '/searchform.php'); ?>
			</li>


<?php if ( is_404() || is_category() || is_day() || is_month() ||
						is_year() || is_search() || is_paged() ) {
			?> <li>

			<?php /* If this is a 404 page */ if (is_404()) { ?>
			<?php /* If this is a category archive */ } elseif (is_category()) { ?>
			<p class="sideinfo"><?php _e('You are currently browsing the archives for the','yashfa');  single_cat_title(''); _e('category.','yashfa');?> </p>

			<?php /* If this is a yearly archive */ } elseif (is_day()) { ?>
			<p class="sideinfo"><?php _e('You are currently browsing the','yashfa')?> <a href="<?php bloginfo('url'); ?>/"><?php echo bloginfo(__('name','yashfa')); ?></a><?php _e('blog archives for the day','yashfa' );the_time('l, F jS, Y'); ?>.</p>

			<?php /* If this is a monthly archive */ } elseif (is_month()) { ?>
			<p class="sideinfo"><?php _e('You are currently browsing the','yashfa')?> <a href="<?php bloginfo('url'); ?>/"><?php echo bloginfo(__('name','yashfa')); ?></a><?php _e('blog archives for','yashfa'); the_time('F, Y'); ?>.</p>

			<?php /* If this is a yearly archive */ } elseif (is_year()) { ?>
			<p class="sideinfo"><?php _e('You are currently browsing the','yashfa')?><a href="<?php bloginfo('url'); ?>/"><?php echo bloginfo(__('name','yashfa')); ?></a><?php _e('blog archives for the year','yashfa') ; the_time('Y'); ?>.</p>

			<?php /* If this is a monthly archive */ } elseif (is_search()) { ?>
			<p class="sideinfo"><?php _e('You have searched the ','yashfa')?> <a href="<?php echo bloginfo('url'); ?>/"><?php echo bloginfo(__('name','yashfa')); ?></a><?php _e('blog archives for ','yashfa')?><strong>'<?php the_search_query(); ?>'</strong><?php _e('. If you are unable to find anything in these search results, you can try one of these links.','yashfa')?></p>

			<?php /* If this is a monthly archive */ } elseif (isset($_GET['paged']) && !empty($_GET['paged'])) { ?>
			<p class="sideinfo"><?php _e('You are currently browsing the ','yashfa')?><a href="<?php echo bloginfo('url'); ?>/"><?php echo bloginfo(__('name','yashfa'),','); ?></a><?php _e('blog archives.','yashfa')?></p>

			<?php } ?>

			</li> <?php }?>

			<li class="boxr clearfix archives"><h3><?php _e('Archives','yashfa')?></h3>
				<ul>
				<?php wp_get_archives('type=monthly&show_post_count=1'); ?>
				</ul>
			</li>


			<li class="boxr clearfix archives">
			<h3><?php _e('Categories','yashfa')?></h3>
			<ul>
			<?php wp_list_categories('show_count=1&title_li=&hierarchical=0'); ?>
			</ul>
			</li>
			
			<?php /* If this is the frontpage */ if ( is_home() || is_page() ) { ?>
				<?php wp_list_bookmarks(); ?>

				<li class="boxr linkcat"><h3>Meta</h3>
				<ul>
					<?php wp_register(); ?>
					<li><?php wp_loginout(); ?></li>
					<li><a href="http://validator.w3.org/check/referer" title="This page validates as XHTML 1.0 Transitional">Valid <abbr title="eXtensible HyperText Markup Language">XHTML</abbr></a></li>
					<li><a href="http://gmpg.org/xfn/"><abbr title="XHTML Friends Network">XFN</abbr></a></li>
					<li><a href="http://wordpress.org/" title="Powered by WordPress, state-of-the-art semantic personal publishing platform.">WordPress</a></li>
					<?php wp_meta(); ?>
				</ul>
				</li>
			<?php } ?>

			<?php endif; ?>
		</ul>
	</div>

