<?php
    ini_set('display_errors', '1');

    include_once("../admin/db.inc");
    include_once("../admin/web-utils.php");
    include_once("../admin/sim-utils.php");
    include_once("../admin/db-utils.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>PhET :: Physics Education Technology at CU Boulder</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />
<style type="text/css">
/*<![CDATA[*/
        @import url(../css/main.css);
/*]]>*/
</style>
</head>

<body>
    <div id="skipNav">
        <a href="#content" accesskey="0">Skip to Main Content</a>
    </div>

    <div id="header">
        <div id="headerContainer">
            <div class="images">
                <span class="logo">
                    <img src="../images/phet-logo.gif" alt="" title="" />
                </span>
                
                <span class="title">
                    <img src="../images/logo-title.jpg" alt="" title="" />
                </span>
            </div>

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li><a href="../index.html" accesskey="1">Home</a></li>

                    <li class="selected"><a href="index.php" accesskey="2">Simulations</a></li>

                    <li><a href="../research/index.html" accesskey="3">Research</a></li>

                    <li><a href="../about/index.html" accesskey="4">About PhET</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div id="container">
        <div id="localNav">
            <ul>
                <li><a href="../index.html" accesskey="1">Home</a></li>

                <li class=" selected"><a href="#">Simulations</a></li>
                
                <?php
                    print_sim_categories();
                ?>

                <li><a href="../teacher_ideas/index.html">Teacher Ideas &amp; Activities</a></li>

                <li><a href="../get_phet/index.html">Download PhET</a></li>

                <li><a href="../tech_support/index.html">Technical Support</a></li>
                
                <li><a href="../contribute/index.html">Contribute</a></li>
                
                <li><a href="../research/index.html">Research</a></li>

                <li><a href="../about/index.html">About PhET</a></li>
            </ul>

            <h4><br />
            Principle Sponsors</h4>

            <dl>
                <dt><a href="http://www.hewlett.org/Default.htm">The William and Flora Hewlett Foundation</a></dt>

                <dd><a href="http://www.hewlett.org/Default.htm"><img src="../images/hewlett-logo.jpg" alt="The Hewlett Logo"/></a><br />
                <br />
                Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.</dd>

                <dt><a href="http://www.nsf.gov/"><img class="sponsors" src="../images/nsf-logo.gif" alt="The NSF Logo"/>National Science Foundation</a></dt>

                <dd><br />
                An independent federal agency created by Congress in 1950 to promote the progress of science.<br />
                <br />
                <a href="../sponsors/index.html"><img src="../images/other-sponsors.gif" alt="Other Sponsors Logo"/></a></dd>
            </dl>
        </div>

        <div id="content">  
            <div class="productList">    
                <?php

                    if (isset($_REQUEST['cat'])) {
                        $cat = $_REQUEST['cat'];
                    }
                    else {
                        $cat = 1;
                    }

                    $select_category_st = "SELECT * FROM `category` WHERE `cat_id`='$cat'";
                    $category_rows      = mysql_query($select_category_st, $connection);
                    
                    // Print the category header -- e.g. 'Top Sims':
                    if (!$category_rows) {
                        print "<div class=\"productListHeader\"><h1>Invalid Category</h1></div>";
                    }
                    else {
                        $category_row = mysql_fetch_assoc($category_rows);
                        
                        $cat_name = $category_row['cat_name'];

                        print "<div class=\"productListHeader\"><h1>$cat_name</h1></div>";
                    }                    

                    //start numbering
                    
                    /*
                        <div id="pg">
                        <p>
                        <a class="pg" href="/">1</a>::
                        <a class="pg" href="/">2</a>::
                        <a class="pg" href="/">3</a>::
                        <a class="pg" href="/">4</a>::
                        <a class="pg" href="/">5</a>::
                        <a class="pg" href="/">6</a>::
                        <a class="pg" href="/">7</a>::
                        <a class="pg" href="/">all&raquo;</a>
                        </p>
                        </div>
                    */
                    $select_all_simulation_listings_st = "SELECT * FROM `simulation_listing` WHERE `cat_id`='$cat'";

                    $num_sims_in_category = mysql_num_rows(mysql_query($select_all_simulation_listings_st, $connection));

                    $sim_limit = SIMS_PER_PAGE;

                    if (isset($HTTP_GET_VARS['st'])) {
                        $sim_start_number = $_REQUEST['st'];
                        
                        if ($sim_start_number == -1) {
                            $sim_start_number = 0;
                            $sim_limit        = 999;
                        }
                    } 
                    else { 
                        $sim_start_number = 0; 
                    }
                    
                    if (isset($_REQUEST['view_type'])) {
                        $view_type = $_REQUEST['view_type'];
                    }
                    else {
                        $view_type = "thumbs";
                    }
                    
                    // This statement selects for all sims in the category, and orders by the sim sorting name:
                    $select_sims_st = sim_get_select_sims_by_category_statement($cat);
                    
                    if ($view_type == "thumbs") {
                        // THUMBNAIL INDEX
                        print "<div id=\"listing_type\"><a href=\"index.php?cat=$cat&amp;view_type=index\">index</a></div>";

                        if ($num_sims_in_category > SIMS_PER_PAGE) {
                            // Don't bother printing this section unless there are more sims than will fit on one page:
                            print "<div id=\"pg\">\n";
                            
                            $num_pages = (int)ceil((float)$num_sims_in_category / (float)SIMS_PER_PAGE);

                            for ($n = 0; $n < $num_pages; $n = $n + 1) {
                                $page_number = $n + 1;

                                $page_sim_start_number = SIMS_PER_PAGE * $n; 

                                print "<a class=\"pg\" href=\"index.php?cat=$cat&amp;st=$page_sim_start_number\">$page_number</a> ";
                            }

                            print "<a class=\"pg\" href=\"index.php?cat=$cat&amp;st=-1\">view all&raquo;</a>";
                            
                            print "</div>\n";    
                        }

                        //--------------------------------------------------
                        print "<div class=\"productList\">";

                        $sim_number = -1;

                        $simulations = run_sql_statement($select_sims_st, $connection);

                         while ($simulation = mysql_fetch_assoc($simulations)) {
                            gather_array_into_globals($simulation);

                            // Make sure the simulation is valid:
                            if (is_numeric($sim_id)) {
                                ++$sim_number;

                                if ($sim_number <  $sim_start_number) continue;
                                if ($sim_number >= $sim_start_number + $sim_limit) break;

                                print "<div class=\"productEntry\">\n";

                                /*
                                    <a href="#"><img src="../images/sims/baloon_static.jpg" width="130" height="97" alt="" /></a>

                                    <p><a href="/">Balloons &amp; Static</a><br /></p>
                                */

                                $link_to_sim = "<a href=\"sims/sims.php?sim_id=$sim_id\">";
                                

                                if ($sim_animated_image_url == "") {
                                    $sim_animated_image_url = $sim_image_url;
                                }
                                
                                print <<<EOT
                                    <a href="sims/sims.php?sim_id=$sim_id"
                                    
                                        onMouseOver="now = new Date();                                         document.images['image_preview_$sim_id'].src =                                       '../admin/get-upload.php?url=$sim_animated_image_url&amp;' + now.getTime();"
                                        
                                        onMouseOut="document.images['image_preview_$sim_id'].src = '../admin/get-upload.php?url=$sim_image_url'; "
                                        
                                        >

                                        <img src="../admin/get-upload.php?url=$sim_image_url" 
                                             name="image_preview_$sim_id"
                                             width="130" height="97" 
                                             alt="View $sim_name Simulation"
                                         />
                                    </a>
                                
EOT;
                                // print "<img src=\"../admin/get-upload.php?url=$sim_image_url\" width=\"130\" height=\"97\" alt=\"View $sim_name Simulation\" />";
                                // print "</a>\n";
                                //print "$link_to_sim$sim_name</a>\n";
                                print "<br/><p>$link_to_sim$sim_name</a></p>\n";

                                // Close product:
                                print "</div>\n";
                            }
                        }
                        
                        print "</div>\n";
                    }
                    else {
                        print "<div id=\"listing_type\"><a href=\"index.php?cat=$cat&amp;view_type=thumbs\">thumbnails</a></div>";
                        
                        // ALPHABETICAL INDEX                        
                        $simulations = run_sql_statement($select_sims_st, $connection);
                        
                        print "<div id=\"pg\">\n";
                        
                        $last_printed_char = '';
                        
                        while ($simulation = mysql_fetch_assoc($simulations)) {
                            gather_array_into_globals($simulation);
                            
                            $sim_sorting_name = get_sorting_name($sim_name);
                            
                            $cur_char = $sim_sorting_name[0];
                            
                            if ($cur_char !== $last_printed_char) {                        
                                print "<a class=\"pg\" href=\"#$cur_char\">$cur_char</a> ";
                                
                                $last_printed_char = $cur_char;
                            }
                        }
                        
                        print "</div>\n";
                        
                        print "<div class=\"productList\">";
                        
                        $simulations = run_sql_statement($select_sims_st, $connection);
                        
                        $last_printed_char = '';
                        
                        while ($simulation = mysql_fetch_assoc($simulations)) {
                            gather_array_into_globals($simulation);
                            
                            $sim_sorting_name = get_sorting_name($sim_name);
                            
                            $cur_char = $sim_sorting_name[0];
                            
                            if ($cur_char !== $last_printed_char) {                        
                                print "<h3 id=\"$cur_char\">$cur_char</h3>";
                                
                                $last_printed_char = $cur_char;
                            }
                            
                            print "<a href=\"sims/sims.php?sim_id=$sim_id\">$sim_name</a><br />";
                        }
                        
                        print "</div>";                    
                    }
                ?>
            </div>

            <p class="footer">© 2007 PhET. All rights reserved.<br />
        </div>
    </div>
</body>
</html>
