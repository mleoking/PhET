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
        @import url(../css/test.css);
/*]]>*/
</style>
<script type="text/javascript" src="../js/dropdown.js">
</script>
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
                    <li><a href="../index.php" accesskey="1">Home</a></li>

                    <li><a href="../simulations/index.php" accesskey="2">Simulations</a></li>

                    <li><a href="../research/index.php" accesskey="3">Research</a></li>

                    <li><a href="../about/index.php" accesskey="4">About PhET</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div id="container">
        <?php 
            include_once("../admin/site-utils.php");

            print_navigation_bar(5);
        ?>

        <div id="content">
            <br />
            <br />

            <h1>Technical Support - Flash</h1>

            <p>This page will help you solve some of the problems people commonly have running our programs. If you can't solve your problem here, please notify us by email at the following address: <a href="mailto:phethelp@colorado.edu"><span class="red">phethelp@colorado.edu</span></a>.</p>

            <p>To run the Flashñbased simulations you must have Flash 7 (available free) or newer installed on your computer.</p><a href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash"><img src="../images/get-flash.gif" /></a>

            <p>If you get a blank window when you try to launch a flash simulation, you probably need a new version of the Flash player.</p>

            <p>Older versions of the Flash Player can cause problems. Updating your flash player is recommended if you receive an error similar to:<br />
            <br />
            <a href="/"><img src="../images/flash-error.gif" /></a></p>

            <p>If you are unsure if you currently have a version of Flash, we can check for you if you <a href="http://phet.colorado.edu/web-pages/misc-pages/flash_detect_v7.php">click here.</a></p>

            <p><a href="#top"><img src="../images/top.gif" /></a></p>

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
