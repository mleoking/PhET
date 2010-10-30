<?php
/*------------------------------------------------------------------------
# WordPress Yashfa Theme - February 2009
# ------------------------------------------------------------------------
# @license - WordPress Yashfa Theme is available under the terms of the GPL 
# Author: Nurudin Jauhari
# Websites:  http://wpgpl.com
-------------------------------------------------------------------------*/
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" <?php language_attributes(); ?>>

<head profile="http://gmpg.org/xfn/11">
<meta http-equiv="Content-Type" content="<?php bloginfo('html_type'); ?>; charset=<?php bloginfo('charset'); ?>" />

<title><?php bloginfo('name'); ?> <?php if ( is_single() ) { ?> &raquo; Blog Archive <?php } ?> <?php wp_title(); ?></title>


<link rel="alternate" type="application/rss+xml" title="<?php bloginfo('name'); ?> RSS Feed" href="<?php bloginfo('rss2_url'); ?>" />
<link rel="pingback" href="<?php bloginfo('pingback_url'); ?>" />
<!-- <link rel="shortcut icon" href="<?php bloginfo('template_directory'); ?>/favicon.ico" /> -->
<link rel="stylesheet" href="<?php bloginfo('stylesheet_url'); ?>" type="text/css" title="Yashfa Themes" media="screen" />

<script language="javascript" type="text/javascript" src="<?php bloginfo('template_directory'); ?>/js/jquery-1.2.6.min.js"></script>
<script language="javascript" type="text/javascript" src="<?php bloginfo('template_directory'); ?>/js/jquery.equalheights.js"></script>

<script type="text/javascript">
var $jx = jQuery.noConflict();

$jx(document).ready(function() {
    $jx(".inner").equalHeights();
});

</script>


<?php if ( is_singular() ) wp_enqueue_script( 'comment-reply' ); ?>
<?php wp_head(); ?>
<link rel="stylesheet" href="<?php bloginfo('template_directory'); ?>/css-navi.css" type="text/css" />

<!--[if IE]>
<link href="<?php bloginfo('template_directory'); ?>/css-ie.css" media="all" rel="stylesheet" type="text/css" />
<![endif]-->


</head>
<body>
<div style="background: url(/images/header_BG.gif) left top repeat-x; height: 127px; margin-bottom: 10px;">
    <div style="height: 127px; margin: 0 auto; width: 765px; position: relative;">
        <div style="position: absolute; width: 242px; height: 127px; left: 0; top: 0;">
            <a href="/">
                <img src="/images/phet-logo.gif" alt="PhET Logo" title="Go to the PhET home page"
                     style="border: 0; background-color: #18388c;"/>
            </a>
        </div>

        <div style="position: absolute; width: 400px; height: 127px; right: 0; top: 0;">
            <img src="/images/logo-title.jpg"
                 alt="Physics Education Technology - University of Colorado, Boulder"
                 title="Physics Education Technology - University of Colorado, Boulder"/>
        </div>
    </div>
</div>
<div id="navr" style="display: none;">
<div id="navr2">
  <ul class="menu clearfix">
    <li <?php if(is_home()){echo 'class="current_page_item"';}?>><a href="<?php bloginfo('siteurl'); ?>/" title="Home">Home</a></li>
    <?php wp_list_pages('sort_column=menu_order&title_li='); ?>
	<?php wp_register('<li class="admintab">','</li>'); ?>
  </ul>
<div class="feeder">
<a rel="alternate" type="application/rss+xml" title="<?php bloginfo('name'); ?> RSS Feed" href="<?php bloginfo('rss2_url'); ?>" />RSS</a>			</div>
</div>
</div>

<div id="page">


<div id="header" style="display: none;">
		<h1><a href="<?php echo get_option('home'); ?>/"><?php bloginfo('name'); ?></a></h1>
		<div class="description"><?php bloginfo('description'); ?></div>
</div>
<hr />

<div id="thebg">
