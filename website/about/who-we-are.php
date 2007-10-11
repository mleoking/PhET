<?php

    include_once("../admin/authentication.php");
    include_once("../admin/site-utils.php");
    include_once("../admin/contrib-utils.php");

	do_authentication(false);
    
    function print_content() {
		$is_team_member = isset($GLOBALS['contributor_is_team_member']) ? $GLOBALS['contributor_is_team_member'] : 0;
		
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
	            		eval(get_code_to_create_variables_from_array($team_member));
                        
                        print "<li>$contributor_name - $contributor_title";

						if ($is_team_member) {
							print <<<EOT
								<br/>
								<table class="form">
									<tr>
										<td>
											primary phone:
										</td>
										
										<td>
											$contributor_primary_phone
										</td>
									</tr>
									
									<tr>
										<td>
											secondary phone:
										</td>
										
										<td>
											$contributor_secondary_phone
										</td>
									</tr>
									
									<tr>
										<td>
											office:
										</td>
										
										<td>
											$contributor_office
										</td>
									</tr>									
								</table>
								<br/>
EOT;
						}

						print "</li>";
                    }
                
                ?>
            </ul>
        </div>
        
        <?php
    }

    print_site_page('print_content', 8);

?>