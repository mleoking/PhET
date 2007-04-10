<?
    ini_set('display_errors', '1');

    include_once("db.inc");

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!-- saved from url=(0057)http://www.ecoflavor.com/jobs/phet/simulations/index.html -->

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>PhET :: Physics Education Technology at CU Boulder</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="favicon.ico" type="image/x-icon" rel="Shortcut Icon" />
<style type="text/css">
/*<![CDATA[*/
@import url( incl/main.css );
/*]]>*/
</style>
    <meta content="MSHTML 6.00.2900.3059" name="GENERATOR" />
</head>

<body>
    <div id="skipNav">
        <a accesskey="0" href="http://www.ecoflavor.com/jobs/phet/simulations/index.html#content">Skip to Main Content</a>
    </div>

    <div id="header">
        <div id="headerContainer">
            <a href="http://phet.colorado.edu/web-pages/index.html"><img title="" alt="" src="incl/phet-logo.gif" /><span class="logo-title"><img title="" alt="" src="incl/logo-title.jpg" /></span></a>

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li><a accesskey="1" href="http://www.ecoflavor.com/jobs/phet/index.html">Home</a></li>

                    <li class="selected"><a accesskey="2" href="http://www.ecoflavor.com/jobs/phet/simulations/index.html">Simulations</a></li>

                    <li><a accesskey="3" href="http://www.ecoflavor.com/jobs/phet/research/index.html">Research</a></li>

                    <li><a accesskey="4" href="http://www.ecoflavor.com/jobs/phet/about/index.html">About PhET</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div id="container">
        <div id="localNav">
            <ul>
                <li class=" selected"><a href="http://www.ecoflavor.com/jobs/phet/simulations/index.html#">Simulations</a> 
                <?php

                //now start dynamic menu for SIMULATIONS//

                // start selecting SIMULATION CATEGORIES from database table
                $sql        = "SELECT * FROM `simcat_def` ORDER BY `cat_id` ASC ";
                $sql_result = mysql_query($sql);

                while ($row = mysql_fetch_row($sql_result)) {
                    $cat_id     = $row[0];
                    $cat_name   = $row[1];

                    print "  <LI class=sub><SPAN class=sub-nav><A href=\"sim_category.php?cat=$cat_id\">&#8594; $cat_name</A></SPAN>";
                } 

                //now let's output the rest//
                ?>
                </li>

                <li class="sub"><span class="sub-nav"><a href="http://www.ecoflavor.com/jobs/phet/sims/all_sims.htm">→ All Sims</a></span></li>

                <li><a href="http://www.ecoflavor.com/jobs/phet/teacher_ideas/index.html">Teacher Ideas Database</a></li>

                <li><a href="http://www.ecoflavor.com/jobs/phet/get_phet/index.html">Download PhET</a></li>

                <li><a href="http://www.ecoflavor.com/jobs/phet/tech_support/index.html">Technical Support</a></li>

                <li><a href="http://www.ecoflavor.com/jobs/phet/contribute/index.html">Contribute</a></li>

                <li><a href="http://www.ecoflavor.com/jobs/phet/about/index.html">About PhET</a></li>
            </ul>

            <h4><br />
            Principle Sponsors</h4>

            <dl>
                <dt><a href="http://www.hewlett.org/Default.htm" target="_blank">The William and Flora Hewlett Foundation</a></dt>

                <dd><a href="http://www.hewlett.org/Default.htm" target="_blank"><img src="incl/hewlett-logo.jpg" /></a><br />
                <br />
                Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.</dd>

                <dt><a href="http://www.nsf.gov/" target="_blank"><img class="sponsors" src="incl/nsf-logo.gif" />National Science Foundation</a></dt>

                <dd><br />
                An independent federal agency created by Congress in 1950 "to promote the progress of science.<br />
                <br />
                <a href="http://www.ecoflavor.com/"><img src="incl/other-sponsors.gif" /></a></dd>
            </dl>
        </div>

        <div id="content">
            <br />
            <br />

            <div class="productListHeader">
                <?php
                    $simid          = $_REQUEST['simid'];
                    $sql_sim        = "SELECT * FROM `simtest` WHERE `simid`= '$simid' ";
                    $sql_result_sim = mysql_query($sql_sim);
        
                    while ($row2 = mysql_fetch_row($sql_result_sim)) {
                        $sim_id         = $row2[0];
                        $sim_name       = $row2[1];
                        $rating         = $row2[2];
                        $type           = $row2[3];
                        $simsize        = $row2[4];
                        $url_sim        = $row2[5];
                        $url_thumb      = $row2[6];
                        $desc           = $row2[7];
                        $keywords       = $row2[8];
                        $systemreq      = $row2[9];
                        $teachingideas  = $row2[10];
                        $usertips       = $row2[11];
                        $learninggoals  = $row2[12];

                        print "<H1>$sim_name</H1></DIV>
                            <P><a href=$url_sim><IMG class=sim-large src=incl/cck-large.jpg></a> $sim_name: $desc<br></P>
                            <TABLE id=indi-sim cellSpacing=0 summary=\"\">
                            <TBODY>
                            <TR>";

                        //rating
                        if ($rating == "0") {
                            $simrating = "<img src=../web-pages/Design/Assets/images/beta-minus-rating.gif width=16 height=16>";
                        }
                        else if ($rating == "2") {
                            $simrating = "<img src=../web-pages/Design/Assets/images/beta-plus-rating.gif width=16 height=16>";
                        }
                        else if ($rating == "1") {
                            $simrating = "<img src=../web-pages/Design/Assets/images/beta-rating.gif width=16 height=16>";
                        }
                        else if ($rating == "3") {
                            $simrating = "<img src=../web-pages/Design/Assets/images/star-rating.gif width=16 height=16>";
                        }
                        else if ($rating == "4") {
                            $simrating = "<img src=../web-pages/Design/Assets/images/alpha-rating.gif width=16 height=14>";
                        }

                        if ($type == "0") {
                            $simtype = "<img src=../web-pages/Design/Assets/images/java.png width=32 height=16>";
                        }
                        else if ($type == "1") {
                            $simtype = "<img src=../web-pages/Design/Assets/images/flash.png width=32 height=16>";
                        }

                        print "<TH>Rating: $simrating Type: $simtype</TH>
                            <BR><a href=$url_sim>CLICK HERE to run this Simulation!</a><br><br><SPAN 
                        class=size>Size $simsize kb</SPAN><br><br>
                            </TR></TBODY></TABLE> <BR><!--<div id=navcontainer>
                        <ul id=navlist>
                        <li><a href=\"#topics\">Info</a></li>
                        <li><a href=\"#ideas\">Teaching Ideas</a></li>
                        <li><a href=\"#software\">Software Requirements</a></li>
                        <li><a href=\"#versions\">Translated Versions</a></li>
                        <li><a href=\"#credits\">Credits</a></li>
                        </ul>
                        </div>
                        <br/><br/>-->
                        <P class=indisim><A class=d-arrow 
                        href=\"#topics\"><img src=incl/double-arrow.png><SPAN 
                        class=burg>Topics</SPAN></A> <A class=d-arrow 
                        href=\"#ideas\"><img src=incl/double-arrow.png><SPAN 
                        class=burg>Teaching Ideas</SPAN></A> <A class=d-arrow 
                        href=\"#software\"><img src=incl/double-arrow.png><SPAN 
                        class=burg>Software Requirements</SPAN></A> <A class=d-arrow 
                        href=\"#versions\"><img src=incl/double-arrow.png><SPAN 
                        class=burg>Translated Versions</SPAN></A> <A class=d-arrow 
                        href=\"#credits\"><img src=incl/double-arrow.png><SPAN 
                        class=burg>Credits</SPAN></A> </P><BR>

                        Keywords: <font style=\"background-color: #e6e6fa; color: #9400d3; font-weight: bold\">$keywords</font>

                        <BR><br>
                        <H1 class=indi-sim id=topics>Simulation Information</H1>
                        <H2 class=sub-title>User Tips</H2>
                        <P class=indi-sim>";

                        //Type Info etc

                        print " $usertips
                        </P>
                        <H2 class=sub-title>Sample Learning Goals</H2>
                        <P class=indi-sim>$learninggoals</P>
                        <P><A 
                        href=\"#top\"><IMG 
                        src=\"incl/top.gif\"></A></P>
                        <H1 class=indi-sim id=ideas>Teaching Ideas</H1>
                        <H2 class=sub-title>Ideas and Activites for this Sim</H2>
                        <P class=indi-sim><a href=$teachingideas>Click here to see Ideas and Activities for this Simulation (pdf file)</a> </P>
                        <P><A 
                        href=\"#top\"><IMG 
                        src=\"incl/top.gif\"></A></P>
                        <H1 class=indi-sim id=software>Software Requirements</H1>
                        <H2 class=sub-title>Software Requirements</H2>
                        <P class=indi-sim>";

                        //software requirements


                        print "<b>Windowsô Systems</b><br>
                        Intel Pentium processor<br>
                        Microsoft Windows 98SE/2000/XP<br>
                        256MB RAM minimum<br>
                        Approximately 50MB available disk space (for full installation)<br>
                        Microsoft Internet Explorer 5.5 or later, Firefox 1.5 or later<br>
                        1024x768 screen resolution or better<br>
                        ";
                
                        if ($type == '0') { 
                            print "Sun Java 1.4.2_10 or later<br>";
                        }
                        else if ($type == '1') {
                            print "Macromedia Flash 7 or later<br>";
                        }

                        print "<br><b>Macintoshô Systems</b><br>
                        G3, G4, G5 or Intel processor<br>
                        OS 10.3.9 or later<br>
                        256MB RAM minimum<br>
                        Approximately 40 MB available disk space (for full installation)<br>
                        1024x768 screen resolution or better<br>
                        Safari 1.3 or later, Firefox 1.5 or later<br>";
                
                        if ($type == '0') {
                            print "Apple Java 1.4.2_09 or later<br>";
                        }
                        else if ($type == '1') {
                            print "Macromedia Flash 7 or later<br>";
                        }

                        print "<br><b>Linux Systems</b><br>
                        Intel Pentium processor<br>
                        256MB RAM minimum<br>
                        Approximately 40 MB disk space (for full
                         installation)<br>
                        1024x768 screen resolution or better<br>
                        Firefox 1.5 or later<br>";
                
                        if ($type == '0') {
                            print "Sun Java 1.4.2_10 or later<br>";
                        }
                        else if ($type == '1') {
                            print "Macromedia Flash 7 or later<br>";
                        }

                        print "
                         </P>
                        <P><A 
                        href=\"#top\"><IMG 
                        src=\"incl/top.gif\"></A></P>
                        <H1 class=indi-sim id=versions>Translated Versions</H1>
                        <H2 class=sub-title>Translated versions</H2>
                        <P class=indi-sim>Quisque sagittis commodo velit. Nunc porttitor sagittis 
                        tortor. Mauris metus. Maecenas eu nisi id elit tincidunt malesuada. </P>
                        <P><A 
                        href=\"#top\"><IMG 
                        src=\"incl/top.gif\"></A></P>
                        <H1 class=indi-sim id=credits>Credits</H1>
                        <H2 class=sub-title>Credits</H2>
                        <P class=indi-sim>Quisque sagittis commodo velit. Nunc porttitor sagittis 
                        tortor. Mauris metus. Maecenas eu nisi id elit tincidunt malesuada. </P>
                        <P><A 
                        href=\"#top\"><IMG 
                        src=\"incl/top.gif\"></A></P>

                        ";
                    } 

                    print "</DIV></DIV>";
                ?>
            </div>

            <p class="footer">© 2007 PhET. All rights reserved.<br />
            Web Design By: <a href="http://royalinteractive.com/">Royal Interactive</a>.</p>
        </div>
    </div>

    <p><br /></p>
</body>
</html>
