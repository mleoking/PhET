<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
        <h1>Contact Info</h1>

        <div class="cellTwo">
            <h2 style="margin-bottom: -10px;">The PhET Project:</h2>

            <p style="margin-left:0px;">c/o Mindy Gratny<br />
            University of Colorado 390 UCB<br />
            Boulder, CO 80309-0390<br /></p>

            <h2 style="margin-bottom: -10px;">License Information::</h2>

            <p style="margin-left:0px;">The PhET software is open source, <a href="licensing.php"><em><u>Click here</u></em></a> to access the licensing information.</p>

            <h2 style="margin-bottom: -10px;">Email:</h2>

            <p style="margin-left:0px;">Please address all electronic correspondence to: <a href="mailto:phethelp@colorado.edu">phethelp@colorado.edu</a> Information on contributing to PhET can be found <a href="../contribute/index.php"><em><u>here.</u></em></a></p><img src="../images/contact-page.jpg" class="imageOne" alt="" />

            <p class="names">&nbsp;</p>

            <center>
                <em><strong><u>Back Row:</u></strong></em> Mindy Gratny, Chris Keller, Michael Dubson, Noah Podolefsky, Carl Wieman, Sam Reid, Ron LeMaster
            </center>

            <p class="names">&nbsp;</p>

            <center>
                <em><strong><u>Front Row:</u></strong></em> Wendy Adams, Alex Adams, Sarah McKagan, Kathy Perkins, Linda Wellmann, Danielle Harlow, Linda Koch, Noah Finkelstein
            </center>

            <p>&nbsp;</p>
            
            <h4>The PhET Team</h4>
            
            <ul class="people">
                <?php
                
                    include_once("../admin/contrib-utils.php");
                    
                    $team_members = contributor_get_team_members();
                    
                    foreach($team_members as $team_member) {
                        $name  = $team_member['contributor_name'];
                        $title = $team_member['contributor_title'];
                        
                        print "<li>$name - $title</li>";
                    }
                
                ?>
            </ul>
        </div>
        
        <?php
    }

    print_site_page('print_content', 8);

?>