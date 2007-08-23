<?php

    include_once("../admin/site-utils.php");
    include_once("../admin/contrib-utils.php");
    
    function print_content() {
        ?>
        <h1>Who We Are</h1>

		<img src="../images/contact-page.jpg" class="imageOne" alt="Image of the PhET Team" width="578"/>

        <div>
            <div class="caption">
                <em><strong><u>Back Row:</u></strong></em> Mindy Gratny, Chris Malley, Chris Keller, John De Goes, Sam Reid, Carl Wieman
            </div>

            <div class="caption">
                <em><strong><u>Front Row:</u></strong></em> Kathy Perkins, Alex Adams, Wendy Adams, Angie Jardine, Mike Dubson, Noah Finkelstein
            </div>

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