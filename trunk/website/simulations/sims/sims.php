<?php
    ini_set('display_errors', '1');

    include_once("../../admin/db.inc");
    include_once("../../admin/web-utils.php");
    include_once("../../admin/sim-utils.php");
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
        @import url(../../css/main.css);
        @import url(../../css/test.css);
/*]]>*/
</style>
<script language="JavaScript" type="text/javascript">
//<![CDATA[
<!--

function SymError()
{
  return true;
}

window.onerror = SymError;

var SymRealWinOpen = window.open;

function SymWinOpen(url, name, attributes)
{
  return (new Object());
}

window.open = SymWinOpen;

//-->
//]]>
</script>
<script type="text/javascript" src="../../js/drop_down.js">
</script>
<script language="JavaScript" type="text/javascript">
//<![CDATA[
<!--

function SymError()
{
  return true;
}

window.onerror = SymError;

var SymRealWinOpen = window.open;

function SymWinOpen(url, name, attributes)
{
  return (new Object());
}

window.open = SymWinOpen;

//-->
//]]>
</script>
</head>

<body id="top">
    <div id="skipNav">
        <a href="#content" accesskey="0">Skip to Main Content</a>
    </div>

    <div id="header">
        <div id="headerContainer">
            <div class="images">
                <span class="logo">
                    <img src="../../images/phet-logo.gif" alt="" title="" />
                </span>
                
                <span class="title">
                    <img src="../../images/logo-title.jpg" alt="" title="" />
                </span>
            </div>

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li><a href="../../index.html" accesskey="1">Home</a></li>

                    <li class="selected"><a href="../../simulations/index.php" accesskey="2">Simulations</a></li>

                    <li><a href="../../research/index.html" accesskey="3">Research</a></li>

                    <li><a href="../../about/index.html" accesskey="4">About PhET</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div id="container">
        <div id="localNav">
            <ul>
                <li><a href="../../index.html" accesskey="1">Home</a></li>

                <li class=" selected"><a href="../index.php">Simulations</a></li>

                <?php
                    print_sim_categories("../");
                ?>

                <li><a href="../../teacher_ideas/index.html">Teacher Ideas &amp; Activities</a></li>

                <li><a href="../../get_phet/index.html">Get PhET</a></li>

                <li><a href="../../tech_support/index.html">Technical Support</a></li>

                <li><a href="../../contribute/index.html">Contribute</a></li>
                
                <li><a href="../../research/index.html">Research</a></li>

                <li><a href="../../about/index.html">About PhET</a></li>
            </ul>

            <h4><br />
            Principle Sponsors</h4>

            <dl>
                <dt><a href="http://www.hewlett.org/Default.htm">The William and Flora Hewlett Foundation</a></dt>

                <dd><a href="http://www.hewlett.org/Default.htm"><img src="../../images/hewlett-logo.jpg" alt="The Hewlett Logo"/></a><br />
                <br />
                Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.</dd>

                <dt><a href="http://www.nsf.gov/"><img class="sponsors" src="../../images/nsf-logo.gif" alt="The NSF Logo"/>National Science Foundation</a></dt>

                <dd><br />
                An independent federal agency created by Congress in 1950 to promote the progress of science.<br />
                <br />
                <a href="../sponsors/index.html"><img src="../../images/other-sponsors.gif" alt="Other Sponsors Logo"/></a></dd>
            </dl>
        </div>

        <div id="content">
            <!--<p><a class="breadcrumbs" href="../../index.html">Home</a><a class="breadcrumbs"  href="../../simulations/index.php">Main Simulations</a><a class="breadcrumbs"  href="../../simulations/index.php">Top Sims</a></p>-->


            <?php
                gather_sim_fields_into_globals($_REQUEST['sim_id']);
            ?>

            <div class="productListHeader">
                <?php
                    print "<h1>$sim_name</h1>"
                ?>
            </div>

            <p>
                <?php
                    print "<a href=\"$sim_launch_url\">";
                    print "<img class=\"sim-large\" src=\"$sim_image_url\"/>";
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

            <h2 class="sub-title">Ideas and Activites for this Sim</h2>
    
            <h2 class="sub-title">Ideas for Teachers from PhET</h2>
            
            <p class="indi-sim">
                <?php
                    print "<a href=\"$sim_phet_ideas_file\">Click here to see Ideas and Activities for this simulation (PDF file).</a>";
                ?>
            </p>
            
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

            <p class="footer">© 2007 PhET. All rights reserved.<br />
            
        </div>
    </div>

    <p><script language="JavaScript" type="text/javascript">
//<![CDATA[
<!--
var SymRealOnLoad;
var SymRealOnUnload;

function SymOnUnload()
{
  window.open = SymWinOpen;
  if(SymRealOnUnload != null)
     SymRealOnUnload();
}

function SymOnLoad()
{
  if(SymRealOnLoad != null)
     SymRealOnLoad();
  window.open = SymRealWinOpen;
  SymRealOnUnload = window.onunload;
  window.onunload = SymOnUnload;
}

SymRealOnLoad = window.onload;
window.onload = SymOnLoad;

//-->
//]]>
</script></p>
</body>
</html>
