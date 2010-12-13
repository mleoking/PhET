<?php
include 'socialicons.php';
function print_respond_reference( ) { // prints an "Add a Comment" link that moves the user to the comment area
    ?>
    <a href="<?php the_permalink( ); ?>#respond">Add a Comment</a>
    <?php

}

?>