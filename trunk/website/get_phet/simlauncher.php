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
            
            print_navigation_bar(4);
        ?>

        <div id="content">
            <br />
            <br />

            <h1>Individual Simulation Installers</h1>

            <table id="get_phet" cellspacing="0" summary="">
                <caption>
                    These installers of the PhET Simulations were released on: 1/2/2007. For changes, see <a href="/">What's New</a>. **We suggest uninstalling your earlier versions of PhET before installing.
                </caption>

                <tr>
                    <th scope="row" abbr="" class="specalt-none">
                        <center>
                            Choose your computer platform
                        </center>
                    </th>

                    <th scope="row" abbr="" class="specalt-none">
                        <center>
                            Full PhET Installation
                        </center>
                    </th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="spec">
                        <ul>
                            <li>PC with Windows and Java 1.4 or later</li>

                            <li>Mac with OS 10.3.9 or later</li>

                            <li>Linux</li>

                            <li>Other UNIX</li>
                        </ul>
                    </th>

                    <th scope="row" abbr="" class="spec"><a href="/"><img src="../images/checkbox.jpg" />&nbsp;download (approx. 40MB)</a></th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="spec">
                        <ul>
                            <li>PC with Windows that does NOT have Java 1.4 or later on it</li>
                        </ul>
                    </th>

                    <th scope="row" abbr="" class="spec"><a href="/"><img src="../images/checkbox.jpg" />&nbsp;download (approx. 65MB)</a></th>
                </tr>
            </table>

            <p>For instructions on creating your own installation CD, <a href="simlauncher.php"><u>click here</u></a>.</p>

            <p>If you have an Internet connection that is too slow, we can send you a CD with the full installation package. Click <a href="../about/contact.php"><u>here to contact us</u></a>.</p>

            <p class="footer">© 2007 PhET. All rights reserved.<br />
            
        </div>
    </div>
</body>
</html>
