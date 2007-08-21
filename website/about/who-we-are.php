<?php

    include_once("../admin/site-utils.php");
    include_once("../admin/contrib-utils.php");
    
    function print_content() {
        ?>
        <h1>Who We Are</h1>

        <div>
			<img src="../images/contact-page.jpg" class="imageOne" alt="" />
			
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
            
            <ul id="people">
                <?php
                    
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