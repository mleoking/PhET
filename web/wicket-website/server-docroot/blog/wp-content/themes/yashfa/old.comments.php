<?php // Do not delete these lines
	if (!empty($_SERVER['SCRIPT_FILENAME']) && 'comments.php' == basename($_SERVER['SCRIPT_FILENAME']))
		die (__('Please do not load this page directly. Thanks!','yashfa'));

	if (!empty($post->post_password)) { // if there's a password
		if ($_COOKIE['wp-postpass_' . COOKIEHASH] != $post->post_password) {  // and it doesn't match the cookie
			?>

			<p class="nocomments"><?php _e('This post is password protected. Enter the password to view comments.','yashfa')?></p>

			<?php
			return;
		}
	}

	/* This variable is for alternating comment background */
	$oddcomment = 'odd" ';
?>

<!-- You can start editing here. -->

<?php if ($comments) : ?>
<?php 

	/* Count the totals */
	$numPingBacks = 0;
	$numComments  = 0;

	/* Loop through comments to count these totals */
	foreach ($comments as $comment) {
		if (get_comment_type() != "comment") { $numPingBacks++; }
		else { $numComments++; }
	}

?>
<?php 

	/* This is a loop for printing comments */
	if ($numComments != 0) : ?>

	<h3 id="comments"><?php echo $numComments; _e('Responses','yashfa')?></h3>

	<ol class="commentlist">

	<?php foreach ($comments as $comment) : ?>
	<?php if (get_comment_type()=="comment") : ?>

		<li class="<?php if ( $comment->comment_author_email == get_the_author_email() ) echo 'mycomment'; else echo $oddcomment; ?>" id="comment-<?php comment_ID() ?>">
			<?php echo get_avatar( $comment, 32 ); ?>
			<div class="byby">
			<strong class="by"><?php comment_author() ?> <?php if(get_comment_author_url()): ?><span></span>&rarr;  <a href="<?php comment_author_url(); ?>"><?php comment_author_url(); ?></a></span><?php endif; ?></strong></div>
			<?php if ($comment->comment_approved == '0') : ?>
			<em><?php _e('Your comment is awaiting moderation.','yashfa')?></em>
			<?php endif; ?>
			<br />

			<?php comment_text() ?>

			<small class="commentmetadata"><a href="#comment-<?php comment_ID() ?>" <?php _e(' title','yashfa')?>=""><?php comment_date('F jS, Y'); _e(' at ','yashfa'); comment_time() ?></a> <?php edit_comment_link(__('edit','yashfa'),'&nbsp;&nbsp;',''); ?></small>

		</li>

	<?php /* Changes every other comment to a different class */
if ('alt' == $oddcomment) $oddcomment = 'odd';
else $oddcomment = 'alt';
?>

	<?php endif; endforeach; /* end for each comment */ ?>

	</ol>
	<?php endif; ?>
	
	<?php

	/* This is a loop for printing trackbacks if there are any */
	if ($numPingBacks != 0) : ?>
<h3 id="trackbacks"><?php _e($numPingBacks); ?> Trackback(s)</h3>
	<ol class="tblist">

<?php foreach ($comments as $comment) : ?>
<?php if (get_comment_type()!="comment") : ?>

	<li id="comment-<?php comment_ID() ?>">
		<?php comment_date('M j, Y') ?>: <?php comment_author_link() ?>
		<?php if ($comment->comment_approved == '0') : ?>
		<em><?php _e('Your comment is awaiting moderation.','yashfa')?></em>
		<?php endif; ?>
	</li>
	
	<?php if('odd'==$thiscomment) { $thiscomment = 'even'; } else { $thiscomment = 'odd'; } ?>
	
<?php endif; endforeach; ?>

	</ol>

<?php endif; ?>
	
 <?php else : // this is displayed if there are no comments so far ?>

	<?php if ('open' == $post->comment_status) : ?>
		<!-- If comments are open, but there are no comments. -->

	 <?php else : // comments are closed ?>
		<!-- If comments are closed. -->
		<p class="nocomments"><?php _e('Comments are closed.','yashfa')?></p>

	<?php endif; ?>
<?php endif; ?>


<?php if ('open' == $post->comment_status) : ?>
<div class="comform">
<h3 id="respond"><?php _e('Leave a Reply','yashfa')?></h3>

<?php if ( get_option('comment_registration') && !$user_ID ) : ?>
<p><?php _e('You must be ','yashfa')?><a href="<?php echo get_option('siteurl'); ?>/wp-login.php?redirect_to=<?php echo urlencode(get_permalink()); ?>"><?php _e('logged in','yashfa')?></a><?php _e(' to post a comment.','yashfa')?></p>
<?php else : ?>

<form action="<?php echo get_option('siteurl'); ?>/wp-comments-post.php" method="post" id="commentform">

<?php if ( $user_ID ) : ?>

<p><?php _e('Logged in as ','yashfa');<a href="<?php echo get_option('siteurl'); ?>/wp-admin/profile.php"><?php echo $user_identity; ?></a>. <a href="<?php echo get_option('siteurl'); ?>/wp-login.php?action=logout" title="Log out of this account">Log out &raquo;</a></p>

<?php else : ?>

<p><input type="text" name="author" id="author" value="<?php echo $comment_author; ?>" size="22" tabindex="1" <?php if ($req) echo "aria-required='true'"; ?> />
<label for="author"><small>Name <?php if ($req) echo "(required)"; ?></small></label></p>

<p><input type="text" name="email" id="email" value="<?php echo $comment_author_email; ?>" size="22" tabindex="2" <?php if ($req) echo "aria-required='true'"; ?> />
<label for="email"><small>Mail (will not be published) <?php if ($req) echo "(required)"; ?></small></label></p>

<p><input type="text" name="url" id="url" value="<?php echo $comment_author_url; ?>" size="22" tabindex="3" />
<label for="url"><small>Website</small></label></p>

<?php endif; ?>

<!--<p><small><strong>XHTML:</strong> You can use these tags: <code><?php echo allowed_tags(); ?></code></small></p>-->

<p><textarea name="comment" id="comment" cols="100%" rows="10" tabindex="4"></textarea></p>

<p><input name="submit" type="submit" id="submit" tabindex="5" value="Submit Comment" />
<input type="hidden" name="comment_post_ID" value="<?php echo $id; ?>" />
</p>
<?php do_action('comment_form', $post->ID); ?>

</form>

<?php endif; // If registration required and not logged in ?>
</div>
<?php endif; // if you delete this the sky will fall on your head ?>
