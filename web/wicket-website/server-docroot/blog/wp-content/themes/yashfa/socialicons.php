<?php function show_social_media_icons( ) { ?>
<div class="social-sub-div">
    <a href="http://www.facebook.com/sharer.php?u=<?php echo urlencode( get_permalink( ) ); ?>&amp;t=<?php echo urlencode( get_the_title( ) ); ?>"
       style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
       title="Share this on Facebook">

        <img src="/files/icons/social/16/facebook.png" alt="Social Media Icon"
             style="border: none;"/>
    </a>
</div>
<div class="social-sub-div">
    <a href="https://twitter.com/share?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;text=<?php echo urlencode( get_the_title( ) ); ?>"
       style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
       title="Share this on Twitter">
        <img src="/files/icons/social/16/twitter.png" alt="Social Media Icon"
             style="border: none;"/>
    </a>
</div>
<div class="social-sub-div">
    <a href="http://www.stumbleupon.com/submit?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
       style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
       title="Share this on Stumble Upon">
        <img src="/files/icons/social/16/stumbleupon.png"
             alt="Social Media Icon" style="border: none;"/>

    </a>
</div>
<div class="social-sub-div">
    <a href="http://digg.com/submit?phase=2&amp;url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
       style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
       title="Share this on Digg">
        <img src="/files/icons/social/16/digg.png" alt="Social Media Icon"
             style="border: none;"/>
    </a>
</div>
<div class="social-sub-div">
    <a href="http://reddit.com/submit?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
       style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
       title="Share this on Reddit">
        <img src="/files/icons/social/16/reddit.png" alt="Social Media Icon"
             style="border: none;"/>
    </a>

</div>
<div class="social-sub-div">
    <a href="http://del.icio.us/post?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
       style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
       title="Share this on Delicious">
        <img src="/files/icons/social/16/delicious.png" alt="Social Media Icon"
             style="border: none;"/>
    </a>
</div>
<?php } ?>