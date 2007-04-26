<?php
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("sim-utils.php");
    include_once("db-utils.php");
    
    function print_site_page($content, $prefix = "..") {
        print <<<EOT
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>PhET :: Physics Education Technology at CU Boulder</title>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />
            <style type="text/css">
            /*<![CDATA[*/
                    @import url($prefix/css/main.css);
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
                                <img src="$prefix/images/phet-logo.gif" alt="" title="" />
                            </span>
                
                            <span class="title">
                                <img src="$prefix/images/logo-title.jpg" alt="" title="" />
                            </span>
                        </div>

                        <div class="clear"></div>

                        <div class="mainNav">
                            <ul>
                                <li><a href="$prefix/index.html" accesskey="1">Home</a></li>

                                <li><a href="$prefix/index.php" accesskey="2">Simulations</a></li>

                                <li><a href="$prefix/research/index.html" accesskey="3">Research</a></li>

                                <li><a href="$prefix/about/index.html" accesskey="4">About PhET</a></li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div id="container">
                    <div id="localNav">
                        <ul>
                            <li><a href="$prefix/index.html" accesskey="1">Home</a></li>

                            <li><a href="$prefix/simulations/index.php">Simulations</a></li>

                            <li><a href="$prefix/teacher_ideas/index.html">Teacher Ideas &amp; Activities</a></li>

                            <li><a href="$prefix/get_phet/index.html">Download PhET</a></li>

                            <li><a href="$prefix/tech_support/index.html">Technical Support</a></li>
                
                            <li><a href="$prefix/contribute/index.html">Contribute</a></li>
                
                            <li><a href="$prefix/research/index.html">Research</a></li>

                            <li><a href="$prefix/about/index.html">About PhET</a></li>
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

EOT;

        print $content;

        print <<<EOT
                        <p class="footer">© 2007 PhET. All rights reserved.<br />
                    </div>
                </div>
            </body>
            </html>
EOT;
