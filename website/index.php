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
            <a href="http://phet.colorado.edu/web-pages/index.html"><img src="images/phet-logo.gif" alt="" title="" /><span class="logo-title"><img src="images/logo-title.jpg" alt="" title="" /></span></a> <!--<div class="globalNav">
<ul>
<li><a href="#" accesskey="6">Search</a></li>
<li class="last"><a href="contact.htm" accesskey="7">Contact &amp; Info</a></li>
</ul>
</div>-->

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li class=" selected"><a href="index.html" accesskey="1">Home</a></li>

                    <li><a href="simulations/index.php" accesskey="2">Simulations</a></li>

                    <li><a href="research/index.html" accesskey="3">Research</a></li>

                    <li><a href="about/index.html" accesskey="4">About PhET</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div id="container">
        <div class="section">

<div class="mainImage">
    
            <?php

/*

Instead of using Flash to display random slideshow, our strategy is to use PHP 
to generate a JavaScript script that randomly cycles through the images. This 
way, the user does not need Flash in order to correctly view the home page.

*/
            
print <<<EOT
    <script language="javascript">

    var delay=5000
    var curindex=0

    var randomimages=new Array()
    
EOT;

$thumbnails = glob("./admin/incl/*.jpg");

$index = 0;

print "\n";

foreach($thumbnails as $thumbnail) {
    print "randomimages[$index] = \"$thumbnail\"\n";
    
    $index++;
}
    
    /*
 	    randomimages[0]="1.jpg"
    	randomimages[1]="5.jpg"
    	randomimages[2]="2.jpg"
    	randomimages[3]="4.jpg"
    	randomimages[4]="3.jpg"
    	randomimages[5]="6.jpg"
    */

print <<<EOT
    var preload=new Array()

    for (n=0;n<randomimages.length;n++)
    {
    	preload[n]=new Image()
    	preload[n].src=randomimages[n]
    }

    document.write('<img name="defaultimage" width="350" height="277" src="'+randomimages[Math.floor(Math.random()*(randomimages.length))]+'">')

    function rotateimage()
    {

    if (curindex==(tempindex=Math.floor(Math.random()*(randomimages.length)))){
    curindex=curindex==0? 1 : curindex-1
    }
    else
    curindex=tempindex

    	document.images.defaultimage.src=randomimages[curindex]
    }

    setInterval("rotateimage()",delay)

    </script>
EOT;
            
            
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
                <dt><a href="get_phet/index.html"><img src="images/three-ways.jpg" alt="" title="" /></a></dt>

                <dd><a href="simulations/index.php">On Line</a></dd>

                <dd><a href="get_phet/full_install.htm">Full Installation</a></dd>

                <dd><a href="get_phet/simlauncher.htm">Partial Installation</a></dd>
            </dl>

            <dl>
                <dt><a href="teacher_ideas/index.html"><img src="images/get-materials.jpg" alt="" title="" /></a></dt>

                <dd><a href="teacher_ideas/index.html">Search for lesson plans and activites that were created by teachers to use with the PhET simulations</a></dd>
            </dl>

            <dl>
                <dt><a href="contribute/index.html"><img src="images/contribute.jpg" alt="" title="" /></a></dt>

                <dd><a href="teacher_ideas/index.html">Provide ideas you've used in class</a></dd>

                <dd><a href="contribute/index.html">Other contributions</a></dd>
            </dl>

            <dl class="last">
                <dt><a href="simulations/index.php"><img src="images/simulations.jpg" alt="" title="" /></a></dt>

                <dd><a class="nolink" href="simulations/index.php"><img src="random-thumbnail.php" /></a></dd>

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
