<?php
/*
Template Name: Links
*/
?>

<?php get_header(); ?>

	<div id="content-wrap">
	<div id="content">
	<div class="gap">

<h2><?php _e('Links:','yashfa')?></h2>
<ul>
<?php wp_list_bookmarks(); ?>
</ul>

</div>
</div>
</div>

<?php get_footer(); ?>
