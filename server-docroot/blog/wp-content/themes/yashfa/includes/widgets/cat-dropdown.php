<?php
function jmCDWidget($args)
{
	extract($args);
	$cat_name = get_option('jm_cat_name');

	$include_cat = get_option('jm_include');
//	if(get_option('jm_child_of')) $child_of = get_option('jm_child_of');
	
	$show_count = get_option('jm_show_count');
//	if(get_option('jm_show_count')) $show_count = get_option('jm_show_count');
echo $before_widget;
?>
	<h3><?php echo $cat_name ?></h3>
	<div class="inn">
	<form action="<?php bloginfo('url'); ?>/" method="get">
<?php
	$select = wp_dropdown_categories('show_option_none=Select category&include='.$include_cat.'&show_count='.$show_count.'&orderby=name&echo=0');
	$select = preg_replace("#<select([^>]*)>#", "<select$1 onchange='return this.form.submit()'>", $select);
	echo $select;
?>
	<noscript><input type="submit" value="View" /></noscript>
	</form>
</div>
</li>
<?php echo $after_widget;
}

function jmCDWidgetAdmin() {
	$cat_name = get_option('jm_cat_name');

	$include_cat = get_option('jm_include');

	$show_count = get_option('jm_show_count');

	// check if anything's been sent
	if (isset($_POST['update_ads'])) {
		$cat_name = strip_tags(stripslashes($_POST['jm_cat_name']));

		$include_cat = strip_tags(stripslashes($_POST['jm_include_cat']));

		$show_count = strip_tags(stripslashes($_POST['jm_show_count']));

		update_option("jm_cat_name",$cat_name);

		update_option("jm_include",$include_cat);
		
		update_option("jm_show_count",$show_count);
	}

	echo '
		<p>
			<label for="jm_cat_name">Category Name?
			<input id="jm_cat_name" name="jm_cat_name" type="text" class="widefat" value="'.$cat_name.'" /></label>
		</p>
		
		<p>
			<label for="jm_include_cat">Main Category? <br /><span style="font-size:10px;">(Put Cat ID separate with comma)</span>
			<input id="jm_include_cat" name="jm_include_cat" type="text" class="widefat" value="'.$include_cat.'" /></label>
		</p>
		<p>
			<label for="jm_show_count">Show Post Count?<br /><span style="font-size:10px;">(Put 1 to Show or 0 to Hidden)
			<input id="jm_show_count" name="jm_show_count" type="text" class="widefat" value="'.$show_count.'" /></label>
		</p>

		<input type="hidden" id="update_ads" name="update_ads" value="1" />';

}

register_sidebar_widget('Category Dropdown', 'jmCDWidget');
register_widget_control('Category Dropdown', 'jmCDWidgetAdmin', 220, 300);

function jmCDWidget2($args)
{
	extract($args);
	$cat_name2 = get_option('jm_cat_name2');

	$include_cat2 = get_option('jm_include2');
	
	$show_count2 = get_option('jm_show_count2');
echo $before_widget;
?>
	<h3><?php echo $cat_name ?></h3>
	<div class="inn">
	<form action="<?php bloginfo('url'); ?>/" method="get">
<?php
	$select = wp_dropdown_categories('show_option_none=Select category&include='.$include_cat2.'&show_count='.$show_count2.'&orderby=name&echo=0');
	$select = preg_replace("#<select([^>]*)>#", "<select$1 onchange='return this.form.submit()'>", $select);
	echo $select;
?>
	<noscript><input type="submit" value="View" /></noscript>
	</form>
</div>
</li>
<?php echo $after_widget;
}

function jmCDWidget2Admin() {
	$cat_name = get_option('jm_cat_name2');

	$include_cat = get_option('jm_include2');

	$show_count = get_option('jm_show_count2');

	// check if anything's been sent
	if (isset($_POST['update_ads'])) {
		$cat_name = strip_tags(stripslashes($_POST['jm_cat_name2']));

		$include_cat = strip_tags(stripslashes($_POST['jm_include2_cat2']));

		$show_count = strip_tags(stripslashes($_POST['jm_show_count2']));

		update_option("jm_cat_name2",$cat_name);

		update_option("jm_include2",$include_cat);
		
		update_option("jm_show_count2",$show_count);
	}

	echo '
		<p>
			<label for="jm_cat_name2">Category Name?
			<input id="jm_cat_name2" name="jm_cat_name2" type="text" class="widefat" value="'.$cat_name2.'" /></label>
		</p>
		
		<p>
			<label for="jm_include2_cat">Main Category? <br /><span style="font-size:10px;">(Put Cat ID separate with comma)</span>
			<input id="jm_include2_cat" name="jm_include2_cat" type="text" class="widefat" value="'.$include_cat2.'" /></label>
		</p>
		<p>
			<label for="jm_show_count">Show Post Count?<br /><span style="font-size:10px;">(Put 1 to Show or 0 to Hidden)
			<input id="jm_show_count" name="jm_show_count2" type="text" class="widefat" value="'.$show_count2.'" /></label>
		</p>

		<input type="hidden" id="update_ads" name="update_ads" value="1" />';

}

register_sidebar_widget('Category Dropdown 2', 'jmCDWidget2');
register_widget_control('Category Dropdown 2', 'jmCDWidget2Admin', 220, 300);

function jmCDWidget3($args)
{
	extract($args);
	$cat_name3 = get_option('jm_cat_name3');

	$include_cat3 = get_option('jm_include3');
//	if(get_option('jm_child_of3')) $child_of = get_option('jm_child_of3');
	
	$show_count = get_option('jm_show_count3');
//	if(get_option('jm_show_count3')) $show_count = get_option('jm_show_count3');
echo $before_widget;
?>
	<h3><?php echo $cat_name3 ?></h3>
	<div class="inn">
	<form action="<?php bloginfo('url'); ?>/" method="get">
<?php
	$select = wp_dropdown_categories('show_option_none=Select category&include='.$include_cat3.'&show_count='.$show_count3.'&orderby=name&echo=0');
	$select = preg_replace("#<select([^>]*)>#", "<select$1 onchange='return this.form.submit()'>", $select);
	echo $select;
?>
	<noscript><input type="submit" value="View" /></noscript>
	</form>
</div>
</li>
<?php echo $after_widget;
}

function jmCDWidget3Admin() {
	$cat_name3 = get_option('jm_cat_name3');

	$include_cat3 = get_option('jm_include3');

	$show_count = get_option('jm_show_count3');

	// check if anything's been sent
	if (isset($_POST['update_ads'])) {
		$cat_name3 = strip_tags(stripslashes($_POST['jm_cat_name3']));

		$include_cat3 = strip_tags(stripslashes($_POST['jm_include3_cat']));

		$show_count = strip_tags(stripslashes($_POST['jm_show_count3']));

		update_option("jm_cat_name3",$cat_name3);

		update_option("jm_include3",$include_cat3);
		
		update_option("jm_show_count3",$show_count);
	}

	echo '
		<p>
			<label for="jm_cat_name3">Category Name?
			<input id="jm_cat_name3" name="jm_cat_name3" type="text" class="widefat" value="'.$cat_name3.'" /></label>
		</p>
		
		<p>
			<label for="jm_include3_cat">Main Category? <br /><span style="font-size:10px;">(Put Cat ID separate with comma)</span>
			<input id="jm_include3_cat" name="jm_include3_cat" type="text" class="widefat" value="'.$include_cat3.'" /></label>
		</p>
		<p>
			<label for="jm_show_count3">Show Post Count?<br /><span style="font-size:10px;">(Put 1 to Show or 0 to Hidden)
			<input id="jm_show_count3" name="jm_show_count3" type="text" class="widefat" value="'.$show_count.'" /></label>
		</p>

		<input type="hidden" id="update_ads" name="update_ads" value="1" />';

}

register_sidebar_widget('Category Dropdown 3', 'jmCDWidget3');
register_widget_control('Category Dropdown 3', 'jmCDWidget3Admin', 220, 300);
?>