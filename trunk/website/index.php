<?php
    include_once("admin/sim-utils.php");
    include_once("admin/web-utils.php");
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
        @import url(css/main.css);
        @import url(css/home.css);
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
                    <img src="images/phet-logo.gif" alt="" title="" />
                </span>
                
                <span class="title">
                    <img src="images/logo-title.jpg" alt="" title="" />
                </span>
            </div>

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li  class="selected"><a href="index.php" accesskey="1">Home</a></li>

                    <li><a href="simulations/index.php" accesskey="2">Simulations</a></li>

                    <li><a href="research/index.php" accesskey="3">Research</a></li>

                    <li><a href="about/index.php" accesskey="4">About PhET</a></li>
                </ul>
            </div>
            
            <div class="clear"></div>
        </div>
    </div>

    <div id="container">
        <div class="section">

<div class="mainImage">


    
            <?php
                print "<img width=\"350\" height=\"277\" src=\"random-thumbnail.php\" alt=\"\">";            
            ?>
            
</div>

            <!-- <object class="mainImage" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="350" height="277">
                <param name="movie" value="slide-home.swf" />
                <param name="quality" value="high" />
                
                <embed src="slide-home.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="350" height="277" />
                
                
                
                
            </object> -->

            <h1>Interactive Physics Simulations</h1>

            <p class="openingParagraph">Fun, interactive simulations of physical phenomena from the Physics Education Technology project at the University of Colorado.</p>

            <p class="findOutMore"><a href="simulations/index.php"><img src="images/findOutMore.gif" alt="Find out More" title="Find out More" /></a><br />
            <br />
            <img src="images/version_es.gif" /> <a class="espanol" href="simulations-espanol.htm">Simulaciones en espa&ntilde;ol</a></p>

            <div class="clear"></div>
        </div>

        <div class="practices">
            <dl>
                <dt><a href="get_phet/index.php"><img src="images/three-ways.jpg" alt="" title="" /></a></dt>

                <dd><a href="simulations/index.php">On Line</a></dd>

                <dd><a href="get_phet/full_install.php">Full Installation</a></dd>

                <dd><a href="get_phet/simlauncher.php">Partial Installation</a></dd>
            </dl>

            <dl>
                <dt><a href="teacher_ideas/index.php"><img src="images/get-materials.jpg" alt="" title="" /></a></dt>

                <dd><a href="teacher_ideas/index.php">Search for lesson plans and activities that were created by teachers to use with the PhET simulations</a></dd>
            </dl>

            <dl>
                <dt><a href="contribute/index.php"><img src="images/contribute.jpg" alt="" title="" /></a></dt>

                <dd><a href="teacher_ideas/index.php">Provide ideas you've used in class</a></dd>

                <dd><a href="contribute/index.php">Other contributions</a></dd>
            </dl>

            <dl class="last">
                <dt><a href="simulations/index.php"><img src="images/simulations.jpg" alt="" title="" /></a></dt>

                <dd>
                <a class="nolink" href="simulations/index.php">
                    <?php
                        display_slideshow(sim_get_static_previews(), "150", "110");
                    ?>
                </a></dd>

                <dd class="readMore"><a href="simulations/index.php"><img src="images/search.gif" alt="Search" title="Search" /></a></dd>
            </dl>

            <div class="clear">
                &nbsp;
            </div>
        </div>

        <p class="footer">© 2007 PhET. All rights reserved.<br />
        
    </div>
</body>
</html>
