<?php // Do not delete these lines
	if (!empty($_SERVER['SCRIPT_FILENAME']) && 'comments.php' == basename($_SERVER['SCRIPT_FILENAME']))
		die (__('Please do not load this page directly. Thanks!','yashfa'));

	if ( post_password_required() ) { ?>

<p class="nocomments"><?php _e('This post is password protected. Enter the password to view comments.','yashfa') ; ?></p>
<?php
		return;
}
?>
<!-- You can start editing here. -->
<?php if ( have_comments() ) : ?>
<?php if ( ! empty($comments_by_type[__('comment','yashfa')]) ) : ?>
<h3 id="comments">
  <?php fb_comment_type_count(__('comment','yashfa')); ?>
  Comments <a href="#respond"><?php _e('Add Yours &darr;','yashfa');?></a></h3>
<p class="cinfo"><?php _e('The upper is the most recent comment','yashfa');?></p>
<ol class="commentlist">
<?php $cmntCnt = 1;?>
  <?php wp_list_comments('type=comment&callback=list_comment'); ?>
</ol>
<div class="navigation">
  <div class="alignleft">
    <?php previous_comments_link() ?>
  </div>
  <div class="alignright">
    <?php next_comments_link() ?>
  </div>
</div>
<br class="clear" />
<?php endif; ?>
<?php if ( ! empty($comments_by_type[__('pings','yashfa')]) ) : ?>
<h3 id="pings"><?php fb_comment_type_count(__('pingback','yashfa')); _e('Trackbacks/Pingbacks' );?>
</h3>
<ol class="trackbacklist">
  <?php wp_list_comments('type=pings&callback=list_pings'); ?>
</ol>
<?php endif; ?>
<br class="clear" />
<?php else : // this is displayed if there are no comments so far ?>
<?php if ('open' == $post->comment_status) : ?>
<!-- If comments are open, but there are no comments. -->
<?php else : // comments are closed ?>
<!-- If comments are closed. -->
<p class="nocomments"><?php _e('Comments are closed.','yashfa');?></p>
<?php endif; ?>
<?php endif; ?>
<?php if ('open' == $post->comment_status) : ?>
<div id="respond" class="comform">
  <h3>
    <?php comment_form_title(__('Your Comment','yashfa'), __('Your Comment to %s','yashfa') ); ?>
  </h3>
  <div class="cancel-comment-reply"> <small>
    <?php cancel_comment_reply_link(); ?>
    </small> </div>
  <?php if ( get_option('comment_registration') && !$user_ID ) : ?>
  <p><?php _e('You must be','yashfa');?> <a href="<?php echo get_option(__('siteurl','yashfa')); ?>/wp-login.php?redirect_to=<?php echo urlencode(get_permalink()); ?>"><?php _e('logged in','yashfa');?></a><?php _e('to post a comment.','yashfa');?></p>
  <?php else : ?>
  <form action="<?php echo get_option(__('siteurl','yashfa')); ?>/wp-comments-post.php" method="post" id="commentform">
    <div class="oneform">
      <?php if ( $user_ID ) : ?>
      <p><?php _e('Logged in as','yashfa');?> <a href="<?php echo get_option(__('siteurl','yashfa')); ?>/wp-admin/profile.php"><?php echo $user_identity; ?></a>. <a href="<?php echo wp_logout_url(get_permalink()); ?>" title="<?php _e('Log out of this account','yashfa');?>"><?php _e('Log out &raquo;','yashfa');?></a></p>
      <?php else : ?>
      <p>
        <label for="author"><small><?php _e('Name','yashfa');?></label>
        <input type="text" name="author" id="author" value="<?php echo $comment_author; ?>" size="22" tabindex="1" <?php if ($req) echo "aria-required='true'"; ?> />
      </p>
      <p>
        <label for="email"><small><?php _e ('Mail','yashfa')?></small></label>
        <input type="text" name="email" id="email" value="<?php echo $comment_author_email; ?>" size="22" tabindex="2" <?php if ($req) echo "aria-required='true'"; ?> />
      </p>
      <p>
        <label for="url"><small><?php _e('Website','yashfa')?></small></label>
        <input type="text" name="url" id="url" value="<?php echo $comment_author_url; ?>" size="22" tabindex="3" />
      </p>
      <?php endif; ?>
    </div>
    <div class="secondform">
      <p>
        <textarea name="comment" id="comment" cols="100%" rows="10" tabindex="4"></textarea>
      </p>
    </div>
    <p>
      <input name="submit" type="submit" id="submit" tabindex="5" value="<?php _e('Submit Comment','yashfa')?>" />
      <?php comment_id_fields(); ?>
    </p>
    <?php do_action(__('comment_form','yashfa'), $post->ID); ?>
  </form>
  <?php endif; // If registration required and not logged in ?>
</div>
<?php endif; // if you delete this the sky will fall on your head ?>
