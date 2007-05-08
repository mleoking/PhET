<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Contribute to PhET!</h1>

            <h2>Academic Contributions</h2>

            <p>If you have developed concept questions, problem sets, lesson plans, and other educational activities based on PhET simulations that may be of use to others, we encourage you to share your work with other educators by contributing to our <a href="../teacher_ideas/index.php">activities database</a>.</p>

            <h2>Financial Contributions</h2>

            <p>Our philosophy is to make PhET simulations freely available to all users around the world. They have now been run hundreds of thousands of times from our web site, and the full PhET suite has been installed on thousands of computers. But while the simulations may be free to users, they are expensive for us to create, test and maintain, and our financial support is limited. If you would like to help make it possible for us to develop more and better simulations, please contact Krista Beck at kristab@jila.colorado.edu or phone (303-492-7746) to find out how you can make a tax deductible contribution.</p>

            <p>PhET would like to thank <a href="../sponsors/index.php">→ our sponsors</a>.</p>

            <h2>Translate Simulations</h2>

            <p>The PhET simulations have been written so that they are easily translated to languages other than English. The process, which requires no programming skills, is described in these two documents:</p>

            <ul class="content-points">
                <li><a href="http://phet.colorado.edu/web-pages/publications/phet-translation.htm">Translating PhET Simulations (also in DOC format)</a></li>

                <li><a href="http://phet.colorado.edu/web-pages/publications/phet-translation-deployment.htm">Deploying Translated PhET Simulations (also in DOC format)</a></li>
            </ul>
        <?php
    }

    print_site_page('print_content', 6);

?>