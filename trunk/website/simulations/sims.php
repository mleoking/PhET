<?php
    ini_set('display_errors', '1');

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/authentication.php");
    
    include_once(SITE_ROOT."teacher_ideas/referrer.php");    

    // Don't require authentication, but do it if the cookies are available:
    do_authentication(false);
    
    function print_content() {
        global $SIM_RATING_TO_IMAGE_HTML, $SIM_TYPE_TO_IMAGE_HTML, $contributor_is_team_member;

		if (!isset($_REQUEST['sim'])) {
			print "<h1>No Simulation</h1><p>There is no simulation by the specified id.</p>";
			
			return;
		}
        
        $sim_encoding = $_REQUEST['sim'];

        
        $sim_id = sim_get_sim_id_by_sim_encoding($sim_encoding);
        
        $simulation = sim_get_sim_by_id($sim_id);
            
        eval(get_code_to_create_variables_from_array($simulation));
        
        $sim_no_mac_html = '';
        
        if ($sim_no_mac) {
            $sim_no_mac_html = SIM_NO_MAC_IMAGE_HTML;
        }
        
        $sim_crutch_html = '';
        
        if ($sim_crutch) {
            $sim_crutch_html = SIM_CRUTCH_IMAGE_HTML;
        }
        
        
        // Gather sim_rating_html & sim_type_html information:
        $sim_rating_html = $SIM_RATING_TO_IMAGE_HTML["$sim_rating"];
		$sim_type_html   = $SIM_TYPE_TO_IMAGE_HTML[$sim_type];
		$sim_launch_url  = sim_get_launch_url($simulation);
		$sim_image_url   = sim_get_screenshot($simulation);
        
        ?>

        <div>
            <?php                
                if (isset($contributor_is_team_member) && $contributor_is_team_member == '1') {
                    print "<h1 class=\"first-child\"><a href=\"../admin/edit-sim.php?sim_id=$sim_id\" title=\"Click here to edit the simulation\">$sim_name</a></h1>";  
                } 
                else {
                    print "<h1 class=\"first-child\"><a href=\"$sim_launch_url\">$sim_name</a></h1>";
                }
            ?>
        </div>

        <div class="container">
            <?php            

			$sim_run_offline_link = sim_get_run_offline_link($simulation);

            print <<<EOT
            

            <div class="simsummary">
                <p>$sim_desc</p>

                <table>
                    <tr>
                        <td>$sim_crutch_html</td>   <td>$sim_no_mac_html</td>     <td>$sim_rating_html</td> 
                    </tr>
                </table>

                <div class="size" title="The size of the simulation, in kilobytes">
                    $sim_size KB
                </div>

            </div>
                
            <div class="simpreview">    
                <a href="$sim_launch_url">
                    <img src="$sim_image_url" alt="Sim preview image" title="Click here to launch the simulation from your browser" width="300"/>
                </a>

                <div id="simrunoptions">
                    <table>
                        <tr>
                            <td>
                                <div class="rage_button_358398">
                                	<a href="$sim_launch_url" title="Click here to run the simulation from your browser">Run Now!</a>
                                </div>
                            </td>
                        
                            <td>
                                <div class="rage_button_928365">
                                	<a href="$sim_run_offline_link" title="Click here to download the simulation to your computer, to run when you do not have an Internet connection'">Run Offline</a>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>

        <div class="shortcuts">
            <span class="burg"><a href="#topics">Topics</a></span>
            
            <span class="burg"><a href="#ideas">Teaching Ideas</a></span>
            
            <span class="burg"><a href="#software">Software Requirements</a></span>
            
            <span class="burg"><a href="#versions">Translated Versions</a></span>
            
            <span class="burg"><a href="#credits">Credits</a></span>
        </div>

        <h1 class="indi-sim" id="topics">Topics</h1>

        <h2>Main Topics</h2>
EOT;
        
        print_comma_list_as_bulleted_list($sim_main_topics);
        
        print <<<EOT
                
        <h2>Related Topics</h2>
        
        <ul>
            <li>
EOT;
                print convert_comma_list_into_linked_keyword_list($sim_keywords, true);
                
                print <<<EOT
            </li>
        </ul>      
        
        <h2>Sample Learning Goals</h2>
EOT;

        print_comma_list_as_bulleted_list($sim_sample_goals);

		$guide = resolve_url_upload($sim_teachers_guide_url);
		
		if (file_or_url_exists($guide)) {
			$guide_html = <<<EOT
				The <a href="../admin/get-upload.php?url=$sim_teachers_guide_url">teacher's guide</a> contains tips for teachers created by the PhET team (PDF).
EOT;
		}
		else {
			$guide_html = "There is no teacher's guide for this simulation.";
		}

        print <<<EOT
        <p><a href="#top"><img src="../images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="ideas">Teaching Ideas</h1>

        <h2>Tips for Teachers</h2>
        
        <p class="indi-sim">
            $guide_html
        </p>

        <h2>Ideas and Activities for this Sim</h2>   
        
        <div id="simcontribbrowser">
EOT;

            global $contributor_id, $contributor_is_team_member;
            
            $content_only = true;
            
            include_once(SITE_ROOT."teacher_ideas/browse.php");
        
        print <<<EOT
        </div>
    
        <h2>Contribute Your Ideas &amp; Activities</h2>
        
        <div class="p-indentation">
        <form id="quicksubmit" enctype="multipart/form-data" action="submit-contribution.php" method="post">    
			<div>
	            <input type="hidden" name="sim_id"   value="$sim_id" />

	            <div class="field">
	                <span class="label_content">
	                    <input type="text" name="contribution_title" size="50" />
	                </span>
                
	                <span class="label">
	                    title:
	                </span>
	            </div>  
            
	            <div class="field">                
	                <span class="label_content">
	                    <input type="file" class="multi" name="contribution_file_url" />
	                </span>
                
	                <span class="label">
	                    files:
	                </span>
	            </div>
            
	            <hr/>
            
	            <div class="field">                
	                <span class="label_content">
EOT;

                    print_multiple_selection(
						'Level',
                        contribution_get_all_template_level_names(),
                        array()
                    );

                print <<<EOT
	                </span>
                
	                <span class="label">
	                    level:
	                </span>
	            </div>
            
	            <div class="button">
	                <input type="submit" value="Contribute"  />
	            </div>
			</div>
        </form>
        </div>

        <p><a href="#top"><img src="../images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="software">Software Requirements</h1>

        <p>$sim_type_html</p>

        <div class="compact">
            <table>
                <thead>
                    <tr>
                        <td>
                            Windows Systems
                        </td>

                        <td>
                            Macintosh Systems
                        </td>
            
                        <td>
                            Linux Systems
                        </td>
                    </tr>
                </thead>
            
                <tbody>
                    <tr>
                        <td>
                            Microsoft Windows 98SE/2000/XP
EOT;
                            
                                if ($sim_type == '0') { 
                                    print "Sun Java 1.4.2_10 or later<br/>";
                                }
                                else if ($sim_type == '1') {
                                    print "Macromedia Flash 7 or later<br/>";
                                }
                                
                            print <<<EOT
                        </td>
            

                        <td>
                            OS 10.3.9 or later
EOT;

                            if ($sim_type == '0') {
                                print "Apple Java 1.4.2_09 or later<br/>";
                            }
                            else if ($sim_type == '1') {
                                print "Macromedia Flash 7 or later<br/>";
                            }
                                
                            print <<<EOT
                        </td>
                    
                        <td>            
EOT;
                            if ($sim_type == '0') {
                                print "Sun Java 1.4.2_10 or later<br/>";
                            }
                            else if ($sim_type == '1') {
                                print "Macromedia Flash 7 or later<br/>";
                            }

                            print <<<EOT
                    
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <p><a href="#top"><img src="../images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="versions">Translated Versions</h1>

EOT;

		$translations = sim_get_translations($simulation);
		
		if (count($translations) > 0) {
			print <<<EOT
				<p class="indi-sim">	
				
				<ul>		
EOT;
			
			foreach ($translations as $language => $launch_url) {
				print "<li><a href=\"$launch_url\" title=\"Click here to launch the $language version of $sim_name\">$language</a></li>";
			}
			
			print '</ul>';
			
			print '</p>';
		}
		else {
			print <<<EOT
				<p class="indi-sim">
					There are no translations available for this simulation.
		        </p>
EOT;
		}

        

		print <<<EOT

        <p><a href="#top"><img src="../images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="credits">Credits</h1>

        <div class="compact">
            <table>
                <thead>
                    <tr>
                        <td>
                            Design Team
                        </td>
                    
                        <td>
                            Libraries
                        </td>

                        <td>
                            Thanks To
                        </td>                    
                    </tr>
                </thead>
            
                <tbody>
                    <tr>
                        <td>
EOT;
                            print_comma_list_as_bulleted_list($sim_design_team);
                            
                            print <<<EOT
                        </td>
                
                        <td>
EOT;
                            print_comma_list_as_bulleted_list($sim_libraries);
                            
                            print <<<EOT
                        </td>
                
                        <td>
EOT;
                            print_comma_list_as_bulleted_list($sim_thanks_to);
                            
                            print <<<EOT
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
EOT;
    }

    print_site_page('print_content', 2);

?>