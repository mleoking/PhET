<?php
    ini_set('display_errors', '1');

    include_once("../admin/db.inc");
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
            <a href="http://phet.colorado.edu/web-pages/index.html"><img src="../images/phet-logo.gif" alt="" title="" /><span class="logo-title"><img src="../images/logo-title.jpg" alt="" title="" /></span></a>

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li><a href="../index.html" accesskey="1">Home</a></li>

                    <li class="selected"><a href="index.html" accesskey="2">Simulations</a></li>

                    <li><a href="../research/index.html" accesskey="3">Research</a></li>

                    <li><a href="../about/index.html" accesskey="4">About PhET</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div id="container">
        <div id="localNav">
            <ul>
                <li class=" selected"><a href="#">Simulations</a></li>
                
                <?php
                    // List all the categories:

                    // start selecting SIMULATION CATEGORIES from database table
                    $select_simcat_def_st = "SELECT * FROM `simcat_def` ORDER BY `cat_id` ASC ";
                    $simcat_def_table     = mysql_query($select_simcat_def_st, $connection);

                    while ($simcat_def = mysql_fetch_row($simcat_def_table)) {
                        $cat_id     = $simcat_def[0];
                        $cat_name   = $simcat_def[1];
                    
                        print "<li class=\"sub\"><span class=\"sub-nav\"><a href=\"index.php?cat=$cat_id\">&rarr; $cat_name</a></span></li>";          
                    } 
                ?>

                <li><a href="../teacher_ideas/index.html">Teacher Ideas Database</a></li>

                <li><a href="../get_phet/index.html">Download PhET</a></li>

                <li><a href="../tech_support/index.html">Technical Support</a></li>

                <li><a href="../contribute/index.html">Contribute</a></li>

                <li><a href="../about/index.html">About PhET</a></li>
            </ul>

            <h4><br />
            Principle Sponsors</h4>

            <dl>
                <dt><a href="http://www.hewlett.org/Default.htm" target="_blank">The William and Flora Hewlett Foundation</a></dt>

                <dd><a href="http://www.hewlett.org/Default.htm" target="_blank"><img src="../images/hewlett-logo.jpg" /></a><br />
                <br />
                Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.</dd>

                <dt><a href="http://www.nsf.gov/" target="_blank"><img class="sponsors" src="../images/nsf-logo.gif" />National Science Foundation</a></dt>

                <dd><br />
                An independent federal agency created by Congress in 1950 "to promote the progress of science.<br />
                <br />
                <a href="../sponsors/index.html"><img src="../images/other-sponsors.gif" /></a></dd>
            </dl>
        </div>

        <div id="content">
            <?php
                    if (isset($_REQUEST['cat'])) {
                        $cat = $_REQUEST['cat'];
                    }
                    else {
                        $cat = 0;
                    }

                    $select_simcat_def_st = "SELECT * FROM `simcat_def` WHERE `cat_id`='$cat'";
                    $simcat_def_rows      = mysql_query($select_simcat_def_st, $connection);
                    
                    if (!$simcat_def_rows) {
                    }
                    else {
                        $simcat_def_row = mysql_fetch_row($simcat_def_rows);
                        
                        $cat_name = $simcat_def_row[1];

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
                    $select_all_simcats_st = "SELECT * FROM `simcat` WHERE `category`='$cat'";

                    $num_sims_in_category = mysql_num_rows(mysql_query($select_all_simcats_st, $connection));

                    $sims_per_page = 12;

                    if (isset($HTTP_GET_VARS['st'])) {
                        $sim_start_number = $_REQUEST['st'];
                        
                        if ($sim_start_number == -1) {
                            $sim_start_number = 0;
                            $sims_per_page    = 999;
                        }
                    } 
                    else { 
                        $sim_start_number = 0; 
                    }

                    print "<div id=\"pg\"><p>\n";
                    if ($num_sims_in_category > $sims_per_page) {
                        $num_pages = (int)ceil((float)$num_sims_in_category / (float)$sims_per_page);

                        for ($n = 0; $n < $num_pages; $n = $n + 1) {
                            $page_number = $n + 1;

                            $page_sim_start_number = $sims_per_page * $n; 

                            print "<a class=\"pg\" href=\"index.php?cat=$cat&st=$page_sim_start_number\">$page_number</a>::";
                        }
                    }
                    print "<a class=\"pg\" href=\"index.php?cat=$cat&st=-1\">all&raquo;</a>";
                    print "</p></div>\n";                    


                    //--------------------------------------------------

                    //first select which SIMS are in the category
                    $select_simcat_rows_st  = "SELECT * FROM `simcat` WHERE `category`='$cat'  LIMIT $sim_start_number, $sims_per_page";

                    $simcat_rows = mysql_query($select_simcat_rows_st, $connection);

                    $sim_column       = 1;
                    $sim_number       = 1;
                    $product_row_open = false;
                    $num_simcats      = mysql_num_rows($simcat_rows);

                    while ($simcat_row = mysql_fetch_row($simcat_rows)) {
                        $simid    = $simcat_row[0];
                        $category = $simcat_row[1];

                        // start selecting SIMULATIONS
                        $select_sim_st = "SELECT * FROM `simtest` WHERE `simid`= '$simid' ";

                        $sim_row = mysql_fetch_row(mysql_query($select_sim_st));

                        $sim_id   = $sim_row[0];
                        $thumburl = $sim_row[6];
                        $sim_name = $sim_row[1];

                        if (is_numeric($sim_id)) {
                            if ($sim_column == 1) { 
                                // OPEN product row
                                print "<div class=productRow>\n";

                                $product_row_open = true;
                            }    

                            if ($sim_column !== 3) {
                                // Just another product in the row:
                                print "<div class=\"productList\">\n";
                            }
                            else {
                                // Last simulation in row
                                print "<div class=\"productList lastProduct\">\n";
                            }

                            print "<a href=\"sim_page.php?simid=$sim_id\">";
                            print    "<img src=\"$thumburl\" width=\"130\" height=\"97\" alt=\"\" />";
                            print    "$sim_name";
                            print "</a>\n";

                            // Close product:
                            print "</div>\n";

                            if ($sim_column == 3) { 
                                // CLOSE product row
                                print "</div>\n";

                                $product_row_open = false;
                            }

                            $sim_number++;
                            $sim_column++;

                            if ($sim_column == 4) {
                                $sim_column = 1;
                            }
                        }
                    }

                    if ($product_row_open) {
                        // Close product row:
                        print "</div>\n";
                    }
                ?>

            <!-- <div id="pg">
                <p><a class="pg" href="/">1</a>:: <a class="pg" href="/">2</a>:: <a class="pg" href="/">3</a>:: <a class="pg" href="/">4</a>:: <a class="pg" href="/">5</a>:: <a class="pg" href="/">6</a>:: <a class="pg" href="/">7</a>:: <a class="pg" href="/">all»</a></p>
            </div>

            <p class="footer">© 2007 PhET. All rights reserved.<br />
            Web Design By: <a href="http://royalinteractive.com">Royal Interactive</a>.</p> -->
        </div>
    </div>
</body>
</html>
