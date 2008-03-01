<?php

    include_once("../admin/site-utils.php");
    include_once("../admin/contrib-utils.php");
    
    function print_content() {
        ?>
        <h1>Contact</h1>

        <div>
            <h2 style="margin-bottom: -10px;">The PhET Project:</h2>

            <p style="margin-left:0px;">c/o Mindy Gratny<br />
            University of Colorado 390 UCB<br />
            Boulder, CO 80309-0390<br /></p>

            <h2 style="margin-bottom: -10px;">License Information::</h2>

            <p style="margin-left:0px;">The PhET software is open source, <a href="licensing.php"><em><u>Click here</u></em></a> to access the licensing information.</p>

            <h2 style="margin-bottom: -10px;">Email:</h2>

            <p style="margin-left:0px;">Please address all electronic correspondence to: <a href="mailto:phethelp@colorado.edu">phethelp@colorado.edu</a> Information on contributing to PhET can be found <a href="../contribute/index.php"><em><u>here.</u></em></a></p>
        </div>
        
        <?php
    }

    print_site_page('print_content', 8);

?>