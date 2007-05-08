<?php
    ini_set('display_errors', '1');

    include_once("../../admin/db.inc");
    include_once("../../admin/web-utils.php");
    include_once("../../admin/sim-utils.php");
    include_once("../../admin/site-utils.php");
    
    function print_content() {
            gather_sim_fields_into_globals($_REQUEST['sim_id']);
        ?>

        <div class="productListHeader">
            <?php
                print "<h1>$sim_name</h1>"
            ?>
        </div>

        <p>
            <?php
                print "<a href=\"../../admin/get-upload.php?url=$sim_launch_url\">";
                print "<img class=\"sim-large\" src=\"../../admin/get-upload.php?url=$sim_image_url\"/>";
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
                        
                        $simrating = "<img src=\"../../images/sims/ratings/$simrating_image\" width=\"16\" height=\"16\" />";
                        $simtype   = "<img src=\"../../images/sims/ratings/$simtype_image\"   width=\"32\" height=\"16\" />";
                        
                        print "$simrating $simtype";
                    ?>
                </th>
            </tr>
        </table><br />
        <span class="size">
            <?php
                print "???kb";
            ?>
        </span><br />

        <p class="indisim">
            <a class="d-arrow" href="#topics"><span class="burg">Topics</span></a> 
            
            <a class="d-arrow" href="#ideas"><span class="burg">Teaching Ideas</span></a> 
            
            <a class="d-arrow" href="#software"><span class="burg">Software Requirements</span></a> 
            
            <a class="d-arrow" href="#versions"><span class="burg">Translated Versions</span></a> 
            
            <a class="d-arrow" href="#credits"><span class="burg">Credits</span></a></p><br />
        <br />

        <h1 class="indi-sim" id="topics">Topics</h1>

        <h2 class="sub-title">Main Topics</h2>

        <p class="indi-sim">
            <?php
                print_comma_list_as_bulleted_list($sim_main_topics);
            ?>
        </p>

        <h2 class="sub-title">Subtopics</h2>

        <p class="indi-sim">
            <?php
                print_comma_list_as_bulleted_list($sim_subtopics);
            ?>
        </p>

        <h2 class="sub-title">Sample Learning Goals</h2>

        <p class="indi-sim">
            <?php
                print_comma_list_as_bulleted_list($sim_sample_goals);
            ?>
        </p>

        <p><a href="#top"><img src="../../images/top.gif" /></a></p>

        <h1 class="indi-sim" id="ideas">Teaching Ideas</h1>

        <h2 class="sub-title">Tips for Teachers</h2>
        
        <p class="indi-sim">
            <?php
                print "<a href=\"../../admin/get-upload.php?url=$sim_teachers_guide_url\">Click here to see the teacher's guide, which contains tips for teachers created by the PhET team (PDF).</a>";
            ?>
        </p>


        <h2 class="sub-title">Ideas and Activites for this Sim</h2>    
    
        <h2 class="sub-title">Submit Ideas &amp; Activities</h2>

        <form enctype="multipart/form-data" action="submit-file.php" method="post">
            <p class="indi-sim">
                If you have ideas or activities you would like to contribute, you can use this form to submit them to PhET.
                For security reasons, you may only submit PDF files, and submissions will not appear on the PhET website 
                until they have been reviewed and accepted by PhET personnel.
            </p>
            <br />
            <input name="keywords" type="file" size="50" accept="application/pdf">
            <br />
            <br />
            <p class="indi-sim">
                Enter keywords to associate with your submission:
            </p>             
            <input name="submission_file" type="text" size="50">
            <br />
            <br />
            <input type="submit" value="Submit" class="buttonSubmit" />
        </form>

        <p><a href="#top"><img src="../../images/top.gif" /></a></p>

        <h1 class="indi-sim" id="software">Software Requirements</h1>

        <h2 class="sub-title">Software Requirements</h2>

        <p class="indi-sim">
            <b>Windows Systems</b><br/>
            Microsoft Windows 98SE/2000/XP<br/>
            
            <?php
                if ($sim_type == '0') { 
                    print "Sun Java 1.4.2_10 or later<br/>";
                }
                else if ($sim_type == '1') {
                    print "Macromedia Flash 7 or later<br/>";
                }
            ?>
            
            <br/><b>Macintosh Systems</b><br/>
            OS 10.3.9 or later<br/>
            
            
            <?php
                if ($sim_type == '0') {
                    print "Apple Java 1.4.2_09 or later<br/>";
                }
                else if ($sim_type == '1') {
                    print "Macromedia Flash 7 or later<br/>";
                }
            ?>
            
            <br/><b>Linux Systems</b><br/>
            
            <?php
                if ($sim_type == '0') {
                    print "Sun Java 1.4.2_10 or later<br/>";
                }
                else if ($sim_type == '1') {
                    print "Macromedia Flash 7 or later<br/>";
                }
            ?>
        </p>

        <p><a href="#top"><img src="../../images/top.gif" /></a></p>

        <h1 class="indi-sim" id="versions">Translated Versions</h1>

        <h2 class="sub-title">Translated versions</h2>

        <p class="indi-sim">
            Coming soon.
        </p>

        <p><a href="#top"><img src="../../images/top.gif" /></a></p>

        <h1 class="indi-sim" id="credits">Credits</h1>

        <h2 class="sub-title">Design Team</h2>

        <p class="indi-sim">
            <?php
                print_comma_list_as_bulleted_list($sim_design_team);
            ?>
        </p>
        
        <h2 class="sub-title">Libraries</h2>

        <p class="indi-sim">
            <?php
                print_comma_list_as_bulleted_list($sim_libraries);
            ?>
        </p>
        
        <h2 class="sub-title">Thanks To</h2>

        <p class="indi-sim">
            <?php
                print_comma_list_as_bulleted_list($sim_thanks_to);
            ?>
        </p>

        <p><a href="#top"><img src="../../images/top.gif" /></a></p>
        
        <?php
    }

    print_site_page('print_content', 2, '../..');

?>