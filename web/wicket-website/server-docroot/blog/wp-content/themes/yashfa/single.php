<?php get_header( ); ?>

<div id="content-wrap">
    <div id="content">
        <div class="gap">

        <?php if ( have_posts( ) ) : while ( have_posts( ) ) : the_post( ); ?>

            <div class="post" id="post-<?php the_ID( ); ?>">
                <h2><a href="<?php the_permalink( ) ?>" rel="bookmark"
                       title="Permanent Link to <?php the_title_attribute( ); ?>"><?php the_title( ); ?></a></h2>

                <div class="posties clearfix">
                    <div class="one">


                        <div class="entry">
                        <?php the_content( ); ?>

                            <div class="social-sub-div">
                            <a href="http://www.facebook.com/sharer.php?u=<?php echo urlencode( get_permalink( ) ); ?>&amp;t=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Facebook">

                                <img src="/images/icons/social/16/facebook.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="https://twitter.com/share?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;text=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Twitter">
                                <img src="/images/icons/social/16/twitter.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="http://www.stumbleupon.com/submit?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Stumble Upon">
                                <img src="/images/icons/social/16/stumbleupon.png"
                                     alt="Social Media Icon" style="border: none;"/>

                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="http://digg.com/submit?phase=2&amp;url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Digg">
                                <img src="/images/icons/social/16/digg.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="http://reddit.com/submit?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Reddit">
                                <img src="/images/icons/social/16/reddit.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>

                        </div>
                        <div class="social-sub-div">
                            <a href="http://del.icio.us/post?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Delicious">
                                <img src="/images/icons/social/16/delicious.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>

                        <div class="new-comments-area">
                        <?php comments_popup_link( __( '0 Comments', 'yashfa' ), __( '1 Comment', 'yashfa' ), __( '% Comments', 'yashfa' ) ); ?>
                            &nbsp;|&nbsp;
                        <?php comments_popup_link( __( 'Add a Comment', 'yashfa' ), __( 'Add a Comment', 'yashfa' ), __( 'Add a Comment', 'yashfa' ) ); ?>
                        </div>

                            <div style="height: 20px;"></div>

                        <?php wp_link_pages( array ( 'before' => '<p><strong>', __( 'Pages:', 'yashfa' ), '</strong> ', __( 'after', 'yashfa' ) => '</p>', 'next_or_number' => __( 'number', 'yashfa' ) ) ); ?>


                            <div class="tagged">
                            <?php the_tags( __( 'Tags: ', 'yashfa' ), ', ', '<br />' ); ?>
                            </div>
                        </div>

                    <?php if ( function_exists( 'similar_posts' ) ) : ?>
                        <div class="related">
                            <h2><?php _e( 'Related Posts', 'yashfa' );?></h2>
                        <?php similar_posts( ); ?>
                        </div>
                    <?php endif; ?>

                        <div class="author-info">


                            <h4><?php _e( 'About The Author', 'yashfa' )?></h4>
                        <?php
                        if ( function_exists( 'get_avatar' ) ) {
                            echo get_avatar( get_the_author_email( ), $size = '45', $default = '' );
                        }
                        ?>
                            <h3><?php the_author( ); ?></h3>

                            <p class="bio"><?php the_author_description( ); ?></p>

                            <p class="lines"><span class="sleft"><?php _e( 'Other posts by', 'yashfa' )?></span><span
                                    class="sright"><?php the_author_posts_link( ); ?></span></p>

                            <p class="lines"><span
                                    class="sleft"><?php _e( 'Author his web site', 'yashfa' )?></span><span
                                    class="sright"><a href="<?php the_author_url( ); ?>"><?php the_author_url( ); ?></a>
                            </p>
                        </div>


                    </div>
                    <div class="two">
                        <div class="dater">
                            <h3 style="font-size: 150%;"><?php the_time( 'D d' ) ?></h3>
                            <h5><?php the_time( 'M Y' ) ?></h5>
                        </div>
                        <p class="metadata"><br/><?php _e( 'by', 'yashfa' ); ?> <?php the_author( ) ?>
                            <br/><?php _e( 'posted in<br/>', 'yashfa' );?><?php the_category( ', ' ) ?> <?php edit_post_link( __( 'Edit', 'yashfa' ), '', '' ); ?>
                        </p>
                    </div>
                </div>

            </div>

            <div class="navigation">
                <div class="alignleft"><?php previous_post_link( '&larr; %link' ) ?></div>
                <div class="alignright"><?php next_post_link( '%link &rarr;' ) ?></div>
            </div>

        <?php comments_template( '', true ); ?>

        <?php endwhile;
        else: ?>

            <p><?php _e( 'Sorry, no posts matched your criteria.', 'yashfa' );?></p>

        <?php endif; ?>

        </div>
    </div>
</div>

<?php get_sidebar( ); ?>

<?php get_footer( ); ?>
