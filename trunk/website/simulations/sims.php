<?php
    ini_set('display_errors', '1');

    include_once("../admin/global.php");
    include_once("../admin/db.inc");
    include_once("../admin/web-utils.php");
    include_once("../admin/sim-utils.php");
    include_once("../admin/site-utils.php");
    include_once("../admin/contrib-utils.php");
    
    $g_login_required = false;    
    include_once("../teacher_ideas/user-login.php");
    
    include_once("../teacher_ideas/referrer.php");    
    
    function print_content() {
        global $SIM_RATING_TO_IMAGE, $SIM_TYPE_TO_IMAGE;
        
        $simulation = sim_get_simulation_by_id($_REQUEST['sim_id']);
            
        eval(get_code_to_create_variables_from_array($simulation));
        
        $sim_keywords_xml = convert_comma_list_into_linked_keyword_list($sim_keywords);
        
        ?>

        <div>
            <?php
                print "<span id=\"floatingkeywords\">$sim_keywords_xml</span>";    
                print "<h1 class=\"page-title\">$sim_name</h1>";
            ?>
        </div>

        <div class="container">
            <p>
                <?php
                    print "<a href=\"../admin/get-upload.php?url=$sim_launch_url\">";
                    print "<img class=\"sim-large\" src=\"../admin/get-upload.php?url=$sim_image_url\"/>";
                    print "</a>";
                    print "$sim_desc";  
                ?>
            </p>

            <table id="indi-sim" cellspacing="0" summary="">
                <tr>
                    <th scope="row" abbr="" class="spec-sim">
                        <?php
                            $simrating_image = $SIM_RATING_TO_IMAGE["$sim_rating"];
                            $simtype_image   = $SIM_TYPE_TO_IMAGE["$sim_type"];
                        
                            $simrating = "<img src=\"../images/sims/ratings/$simrating_image\" width=\"16\" height=\"16\" />";
                            $simtype   = "<img src=\"../images/sims/ratings/$simtype_image\"   width=\"32\" height=\"16\" />";
                        
                            print "$simrating $simtype";
                        ?>
                    </th>
                </tr>
            </table>
        
            <span class="size">
                <?php
                    print "???kb";
                ?>
            </span>       
        </div>

        <div class="shortcuts">
            <span class="burg"><a href="#topics">Topics</a></span>
            
            <span class="burg"><a href="#ideas">Teaching Ideas</a></span>
            
            <span class="burg"><a href="#software">Software Requirements</a></span>
            
            <span class="burg"><a href="#versions">Translated Versions</a></span>
            
            <span class="burg"><a href="#credits">Credits</a></span>
        </div>

        <h1 class="indi-sim" id="topics">Topics</h1>

        <div class="compact">
            <table>
                <thead>
                    <tr>
                        <td>
                            Main Topics
                        </td>
                    
                        <td>
                            Subtopics
                        </td>
                    
                        <td>
                            Sample Learning Goals
                        </td>
                    </tr>
                </thead>
            
                <tbody>
                    <tr>
                        <td>
                            <?php
                                print_comma_list_as_bulleted_list($sim_main_topics);
                            ?>
                        </td>
                    
                        <td>
                            <?php
                                print_comma_list_as_bulleted_list($sim_subtopics);
                            ?>
                        </td>
                    
                        <td>
                            <?php
                                print_comma_list_as_bulleted_list($sim_sample_goals);
                            ?>
                        </td>                    
                    </tr>
                </tbody>
            </table>
        </div>

        <p><a href="#top"><img src="../images/top.gif" /></a></p>

        <h1 class="indi-sim" id="ideas">Teaching Ideas</h1>

        <h2 class="sub-title">Tips for Teachers</h2>
        
        <p class="indi-sim">
            <?php
                print "The <a href=\"../admin/get-upload.php?url=$sim_teachers_guide_url\">teacher's guide</a> contains tips for teachers created by the PhET team (PDF).";
            ?>
        </p>


        <h2 class="sub-title">Ideas and Activities for this Sim</h2>   
        
        <?php
            global $contributor_id, $contributor_is_team_member;
            
            $content_only = true;
            
            include_once(SITE_ROOT."teacher_ideas/browse.php");
        
        ?>
    
        <h2 class="sub-title">Submit Ideas &amp; Activities</h2>

        <form id="upload-form" enctype="multipart/form-data" action="submit-contribution.php" method="post">    
            <?php
                print "<input type=\"hidden\" name=\"sim_id\"   value=\"$sim_id\" />";
            ?>

            <p class="indi-sim">
                Please enter a title to describe your contribution:
            </p>   

            <input type="text" name="contribution_title" size="50" />
            
            <br/>
            <br/>            
            <!-- The file element -- NOTE: it has an ID -->
        	<input type="file" class="multi" name="contribution_file_url">

            <br/>
            
            <input type="submit" value="Contribute" class="buttonSubmit" />
        </form>

        <p><a href="#top"><img src="../images/top.gif" /></a></p>

        <h1 class="indi-sim" id="software">Software Requirements</h1>

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
            
                            <?php
                                if ($sim_type == '0') { 
                                    print "Sun Java 1.4.2_10 or later<br/>";
                                }
                                else if ($sim_type == '1') {
                                    print "Macromedia Flash 7 or later<br/>";
                                }
                            ?>
                        </td>
            

                        <td>
                            OS 10.3.9 or later
        
            
                            <?php
                                if ($sim_type == '0') {
                                    print "Apple Java 1.4.2_09 or later<br/>";
                                }
                                else if ($sim_type == '1') {
                                    print "Macromedia Flash 7 or later<br/>";
                                }
                            ?>
                        </td>
                    
                        <td>            
                        <?php
                            if ($sim_type == '0') {
                                print "Sun Java 1.4.2_10 or later<br/>";
                            }
                            else if ($sim_type == '1') {
                                print "Macromedia Flash 7 or later<br/>";
                            }
                        ?>
                    
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <p><a href="#top"><img src="../images/top.gif" /></a></p>

        <h1 class="indi-sim" id="versions">Translated Versions</h1>

        <p class="indi-sim">
        </p>

        <p><a href="#top"><img src="../images/top.gif" /></a></p>

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
                            <?php
                                print_comma_list_as_bulleted_list($sim_design_team);
                            ?>
                        </td>
                
                        <td>
                            <?php
                                print_comma_list_as_bulleted_list($sim_libraries);
                            ?>
                        </td>
                
                        <td>
                            <?php
                                print_comma_list_as_bulleted_list($sim_thanks_to);
                            ?>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        
        <?php
    }

    print_site_page('print_content', 2);

?>