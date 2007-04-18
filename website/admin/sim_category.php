<?
    ini_set('display_errors', '1');

	include_once("db.inc");
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<!-- saved from url=(0057)http://www.ecoflavor.com/jobs/phet/simulations/index.html -->
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD><TITLE>PhET :: Physics Education Technology at CU Boulder</TITLE>
<META http-equiv=Content-Type content="text/html; charset=iso-8859-1"><LINK 
href="favicon.ico" type=image/x-icon rel="Shortcut Icon">
<STYLE type=text/css>@import url( incl/main.css );
</STYLE>

<META content="MSHTML 6.00.2900.3059" name=GENERATOR></HEAD>
<BODY>
<DIV id=skipNav><A accessKey=0 
href="http://www.ecoflavor.com/jobs/phet/simulations/index.html#content">Skip to 
Main Content</A></DIV>
<DIV id=header>
<DIV id=headerContainer><A 
href="http://phet.colorado.edu/web-pages/index.html"><IMG title="" alt="" 
src="incl/phet-logo.gif"><SPAN 
class=logo-title><IMG title="" alt="" 
src="incl/logo-title.jpg"></SPAN></A> 

<DIV class=clear></DIV>
<DIV class=mainNav>
<UL>
  <LI><A accessKey=1 
  href="http://www.ecoflavor.com/jobs/phet/index.html">Home</A> 
  <LI class=selected><A accessKey=2 
  href="http://www.ecoflavor.com/jobs/phet/simulations/index.html">Simulations</A> 

  <LI><A accessKey=3 
  href="http://www.ecoflavor.com/jobs/phet/research/index.html">Research</A> 
  <LI><A accessKey=4 
  href="http://www.ecoflavor.com/jobs/phet/about/index.html">About PhET</A> 
</LI></UL></DIV></DIV></DIV>
<DIV id=container>
<DIV id=localNav>
<UL>
  <LI class=" selected"><A 
  href="http://www.ecoflavor.com/jobs/phet/simulations/index.html#">Simulations</A> 


<?php
    //now start dynamic menu for SIMULATIONS//

    // start selecting SIMULATION CATEGORIES from database table
    $select_category_st = "SELECT * FROM `cat_id` ORDER BY `cat_id` ASC ";
    $category_table     = mysql_query($select_category_st);

    while ($category = mysql_fetch_row($category_table)) {
        $cat_id     = $category[0];
        $cat_name   = $category[1];
    
        print "<li class=sub><span class=sub-nav><a href=\"sim_category.php?cat=$cat_id\">&#8594; $cat_name</a></span>";		   
    } 
    //now let's output the rest//
?>

<LI class=sub><SPAN class=sub-nav><A 
  href="http://www.ecoflavor.com/jobs/phet/sims/all_sims.htm">&#8594; All 
  Sims</A></SPAN> 
  <LI><A 
  href="http://www.ecoflavor.com/jobs/phet/teacher_ideas/index.html">Teacher 
  Ideas Database</A> 
  <LI><A href="http://www.ecoflavor.com/jobs/phet/get_phet/index.html">Download 
  PhET</A> 
  <LI><A 
  href="http://www.ecoflavor.com/jobs/phet/tech_support/index.html">Technical 
  Support</A> 
  <LI><A 
  href="http://www.ecoflavor.com/jobs/phet/contribute/index.html">Contribute</A> 

  <LI><A href="http://www.ecoflavor.com/jobs/phet/about/index.html">About 
  PhET</A> </LI></UL>
<H4><BR>Principle Sponsors</H4>
<DL>
  <DT><A href="http://www.hewlett.org/Default.htm" target=_blank>The William and 
  Flora Hewlett Foundation</A> 
  <DD><A href="http://www.hewlett.org/Default.htm" target=_blank><IMG 
  src="incl/hewlett-logo.jpg"></A><BR><BR>Makes 
  grants to address the most serious social and environmental problems facing 
  society, where risk capital, responsibly invested, may make a difference over 
  time. 
  <DT><A href="http://www.nsf.gov/" target=_blank><IMG class=sponsors 
  src="incl/nsf-logo.gif">National 
  Science Foundation</A> 
  <DD><BR>An independent federal agency created by Congress in 1950 "to promote 
  the progress of science.<BR><BR><A href="http://www.ecoflavor.com/"><IMG 
  src="incl/other-sponsors.gif"></A> 
  </DD></DL></DIV>
<DIV id=content><BR><BR>

<?php
    $cat = $_REQUEST['cat'];

    $select_category_st = "SELECT * FROM `cat_id` WHERE `cat_id`='$cat'";
    $category_rows      = mysql_query($select_category_st);

    while ($category_row = mysql_fetch_row($category_rows)) {
        $cat_id   = $category_row[0];
        $cat_name = $category_row[1];

        print "<DIV class=productListHeader><H1>$cat_name</H1></DIV>";
    } 

    //start numbering
    $select_all_simulation_listings_st = "SELECT * FROM `simulation_listing` WHERE `cat_id`='$cat'";

    $num_categories = mysql_num_rows(mysql_query($select_all_simulation_listings_st));

    if (isset($HTTP_GET_VARS['st'])) {
        $sim_start_number = $_REQUEST['st'];
    } 
    else { 
        $sim_start_number = 0; 
    }

    $sims_per_page = 12;

    if ($num_categories > $sims_per_page) {
        print "<div id=pg><p>";

        $num_pages = (int)ceil((float)$num_categories / (float)$sims_per_page);

        for ($n = 0; $n < $num_pages; $n = $n + 1) {
            $m = $n + 1;

            $page_sim_start_number = $sims_per_page * $n; 

            print "<a class=pg href=sim_category.php?cat=$cat&st=$page_sim_start_number>$m</a>::";
        }

        print "</p></div>\n";
    }


    //--------------------------------------------------

    //start simulation thumbnails//
    $cat = $_REQUEST['cat'];

    //first select which SIMS are in the category
    $select_simulation_listing_rows_st  = "SELECT * FROM `simulation_listing` WHERE `cat_id`='$cat'  LIMIT $sim_start_number, $sims_per_page";

    $simulation_listing_rows = mysql_query($select_simulation_listing_rows_st);

    $sim_column       = 1;
    $sim_number       = 1;
    $product_row_open = false;
    $num_simulation_listings      = mysql_num_rows($simulation_listing_rows)

    while ($simulation_listing_row = mysql_fetch_row($simulation_listing_rows)) {
        $sim_id    = $simulation_listing_row[0];
        $category = $simulation_listing_row[1];

        // start selecting SIMULATIONS
        $select_sim_st = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";

        $sim_row = mysql_fetch_row(mysql_query($select_sim_st));

        $sim_id   = $sim_row[0];
        $thumburl = $sim_row[6];
        $sim_name = $sim_row[1];

        if (is_numeric($sim_id)) {
            if ($sim_column == 1) { 
                // OPEN product row
                print "<DIV class=productRow>\n";
    
                $product_row_open = true;
            }    
    
            if ($sim_column !== 3) {
                // Just another product in the row:
                print "<DIV class=\"productList\">\n";
            }
            else {
                // Last simulation in row
                print "<DIV class=\"productList lastProduct\">\n";
            }
    
            print "<A href=\"sim_page.php?sim_id=$sim_id\"><IMG height=97 alt=\"\" src=\"$thumburl\" width=130>$sim_name</A>\n";
    
            // Close product:
            print "</DIV>\n";
    
            if ($sim_column == 3) { 
                // CLOSE product row
                print "</DIV>\n";
    
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
        print "</DIV>\n";
    }

?>

</DIV>

<P class=footer>© 2007 PhET. All rights reserved.<BR>Web Design By: <A 
href="http://royalinteractive.com/">Royal Interactive</A>. 
</P></DIV></DIV></BODY></HTML><br>

