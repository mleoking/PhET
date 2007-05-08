<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
        <h1>About PhET</h1>

        <h2 style="margin-bottom: -10px;">Our Goal</h2>

        <p>Our goal is to advance physics education by advancing the effectiveness of learning materials, to produce those learning materials, and to understand why they work.</p>

        <h2 style="margin-bottom: -10px;">Our Principles</h2>

        <p>We believe that everyone's life will be better if they understand some basic physics. That people's enjoyment of and ability to improve their lives increases with an increase in their fundamental understanding of the world around them.</p>

        <p>We believe that the best way to improve that understanding is through the use of learning materials and environments that are compelling, engaging, and motivated by everyday life in the real world.</p>

        <p>We believe that the design of these materials and environments should be based on the dual foundations of research and teaching experience.</p>
        <?php
    }

    print_site_page('print_content', 8);

?>
