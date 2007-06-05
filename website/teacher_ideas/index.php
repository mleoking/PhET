<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Teacher Ideas &amp; Activities</h1>
            
            <p>
                Welcome to the Teacher Ideas &amp; Activities page. This page is your gateway to teacher-submitted contributions, designed to be used in conjunction with the <a href="../simulations/index.php">PhET simulations</a>.
            </p>
            
            <p>
                These contributions include homework assignments, lectures, activities, concept questions and more, 
                and enable you to get the most out of your PhET experience.
            </p>
            
            <p>
                Start by <a href="browse.php">browsing</a> existing contributions. If you already have a simulation in mind,
                head over to the <a href="../simulations/index.php">PhET simulations</a> to browse the contributions designed
                for that simulation. Finally, if you have developed some material you'd like to share with others, please
                consider <a href="contribute.php">contributing it to PhET</a>.
            </p>
        <?php
    }

    print_site_page('print_content', 3);

?>