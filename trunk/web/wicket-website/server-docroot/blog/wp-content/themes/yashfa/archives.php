<?php
/*
Template Name: Archives
*/
?>

<?php get_header(); ?>

<div id="content-wrap" class="archive">
	<div id="content">
	<div class="gap">

      <div class="post">

<div class="posties clearfix">
<div class="one">      
        <h3 class="pagetitle2"><?php _e('Last 25 Posts','yashfa') ?>
        </h3>
        <ul class="recent2">
          <?php

$temp = $wp_query;
$wp_query= null;
$wp_query = new WP_Query();

$wp_query->query('showposts=25'.'&paged='.$paged);
?>
          <?php while ($wp_query->have_posts()) : $wp_query->the_post(); ?>
          <li><a href="<?php the_permalink() ?>" rel="bookmark">
            <?php the_title(); ?>
            </a>
            <?php the_time('d m'); ?>
          </li>
          <?php endwhile; ?>
        </ul>
        <?php
$sett = '&offset='.$totale;
if($postoff ==$sett): ?>
        <div class="navigation">
          <div class="alignleft">
            <?php previous_posts_link(__('&laquo; Previous','yashfa')) ?>
          </div>
          <div class="alignright">
            <?php next_posts_link(__('More &raquo;','yashfa')) ?>
          </div>
        </div>
        <?php endif; ?>
        <?php $wp_query = null; $wp_query = $temp;?>
        <br class="clear" />
        <br class="clear" />
        <h3 class="pagetitle2"><?php _e('Archives by Month:','yashfa') ?>
        </h3>
        <ul>
          <?php wp_get_archives('type=monthly'); ?>
        </ul>
        <br class="clear" />
        <h3 class="pagetitle2"><?php _e('Archives by Subject:','yashfa') ?>
        </h3>
        <ul>
          <?php wp_list_categories('show_count=1&title_li=<h4>',__('Categories','yashfa'),'</h4>');?>
        </ul>
        <h4><?php _e('By Tags','yashfa');?></h4>
          <?php wp_tag_cloud(); ?>
</div>
<div class="two">
<h2><?php _e('Archives','yashfa')?></h2>
</div>
</div>
      </div>
</div>
</div>
</div>
<?php get_sidebar(); ?>

<?php get_footer(); ?>
