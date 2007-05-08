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
            
            print_navigation_bar(8); 
        ?>

        <div id="content">
            <h1>Contact Info</h1>

            <div class="cellTwo">
                <h2 style="margin-bottom: -10px;">The PhET Project:</h2>

                <p style="margin-left:0px;">c/o Mindy Gratny<br />
                University of Colorado 390 UCB<br />
                Boulder, CO 80309-0390<br /></p>

                <h2 style="margin-bottom: -10px;">License Information::</h2>

                <p style="margin-left:0px;">The PhET software is open source, <a href="licensing.php"><em><u>Click here</u></em></a> to access the licensing information.</p>

                <h2 style="margin-bottom: -10px;">Email:</h2>

                <p style="margin-left:0px;">Please address all electronic correspondence to: <a href="mailto:phethelp@colorado.edu">phethelp@colorado.edu</a> Information on contributing to PhET can be found <a href="../contribute/index.php"><em><u>here.</u></em></a></p><img src="../images/contact-page.jpg" class="imageOne" alt="" />

                <p class="names">&nbsp;</p>

                <center>
                    <em><strong><u>Back Row:</u></strong></em> Mindy Gratny, Chris Keller, Michael Dubson, Noah Podolefsky, Carl Wieman, Sam Reid, Ron LeMaster
                </center>

                <p class="names">&nbsp;</p>

                <center>
                    <em><strong><u>Front Row:</u></strong></em> Wendy Adams, Alex Adams, Sarah McKagan, Kathy Perkins, Linda Wellmann, Danielle Harlow, Linda Koch, Noah Finkelstein Not Shown: Krista Beck, Trish Loeblein, Chris Malley
                </center>

                <p>&nbsp;</p>
                
                <h4>The PhET Team</h4>
                
                <ul class="people">
                    <?php
                    
                        include_once("../admin/contrib-utils.php");
                        
                        $team_members = contributor_get_team_members();
                        
                        foreach($team_members as $team_member) {
                            $name  = $team_member['contributor_name'];
                            $email = $team_member['contributor_email'];
                            
                            print "<li>$name</li>";
                        }
                    
                    ?>
                </ul>
                
                <!--
<h4>The PhET Team:</h4>         
<ul class="people">
<li>Member 1 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 2 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 3 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 4 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 5 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 6 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 7 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 8 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 9 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 10 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 11 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
<li>Member 12 <em>Title</em> <span>(303 555-5555 <a href="mailto:.edu">Email</a></span> </li>
</ul>
-->
            </div>

            <p class="footer">© 2007 PhET. All rights reserved.<br />
            
        </div>
    </div>
</body>
</html>
