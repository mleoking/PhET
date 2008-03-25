<?php
    if (!defined('SITE_ROOT')) {
        define("SITE_ROOT", "./");
    }

    session_start();

    include_once("admin/cache-utils.php");

    cache_auto_start();

    include_once("admin/sim-utils.php");
    include_once("admin/web-utils.php");
    include_once("admin/site-utils.php");

    $referrer           = $_SERVER['PHP_SELF'];
    $utility_panel_html = get_sitewide_utility_html('.');

    if (!isset($contributor_email)) {
        $contributor_email = '';
    }

    if (!isset($_REQUEST['subscribed'])) {
        $just_subscribed = false;
    }
    else {
        $just_subscribed = true;
    }

    /*

        TODO:

            Reinsert <?xml version="1.0" encoding="UTF-8"?>
                after IE6 is dead

    */

    print <<<EOT
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
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
<!-- compliance patch for microsoft browsers -->
<!--[if lt IE 7]><script src="js/ie7/ie7-standard-p.js" type="text/javascript"></script><![endif]-->
<script src="js/jquery.pack.js" type="text/javascript"></script>
</head>

<body>
    <div id="skipNav">
        <a href="#content" accesskey="0">Skip to Main Content</a>
    </div>

    <div id="header">
        <div id="headerContainer">
            <div class="images">
                <div class="logo">
                    <a href="index.php"><img src="images/phet-logo.gif" alt="PhET Logo" title="Click here to go to the home page" /></a>
                </div>

                <div class="title">
                    <img src="images/logo-title.jpg" alt="Physics Education Technology - University of Colorado, Boulder" title="Physics Education Technology - University of Colorado, Boulder" />

                    <div id="quicksearch">
                        <form method="post" action="simulations/search.php">
                            <fieldset>
                                <span>Search</span>
                                <input type="text" size="15" name="search_for" title="Enter the text to search for" class="always-enabled" />
                                <input type="submit" value="Go" title="Click here to search the PhET website" class="always-enabled" />
                                <input type="hidden" name="referrer" value="$referrer"  class="always-enabled" />
                            </fieldset>
                        </form>
                    </div>
                </div>
            </div>

            <div class="clear"></div>
        </div>
    </div>

    <div id="container">
        <div class="home-page">
            <div id="newsflashes">
                <table>
                    <tr>
                        <td class="warning-notice">
                        </td>

                        <td class="links">

EOT;

            if (!isset($contributor_receive_email) || $contributor_receive_email == 0) {
                print <<<EOT
                            <a href="teacher_ideas/subscribe-newsletter.php">Subscribe to PhET Newsletter</a> |

EOT;
            }

            print <<<EOT
                            <a href="about/news.php">What's New</a>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="introduction">

                <div class="mainImage">
                    <a href="simulations/index.php" title="Click here to view the simulations"><img width="320" height="240" src="random-thumbnail.php" alt="Random screenshot of a simulation"/></a>
                </div>

                <h1>Interactive Physics Simulations</h1>

                <p class="openingParagraph">Fun, interactive, <a href="research/index.php">research-based</a> simulations of physical phenomena from the Physics Education Technology project at the University of Colorado.</p>

                <table id="hotlinks">
                    <tr>
                        <td colspan="2">
                            <p class="findOutMore" onclick="javascript:location.href='simulations/index.php?cat=Top_Simulations'">
                                <a href="simulations/index.php?cat=Top_Simulations">
                                    Play with sims... &gt;
                                </a>
                            </p>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="clear"></div>
        </div>

        <div id="home-page-sponsors">
            <div class="sponsor">
                <a href="http://www.nsf.gov/" title="Click here to visit the National Science Foundation" >
                    <img src="images/nsf-logo-small.gif" height="28" alt="National Science Foundation Logo"/>
                </a>
                <p>
                    <a href="http://www.nsf.gov/" title="Click here to visit the National Science Foundation" >
                        The National Science Foundation
                    </a>
                </p>
            </div>

            <div class="sponsor">
                <a href="http://www.hewlett.org/Default.htm" title="Click here to visit the William and Flora Hewlett Foundation">
                    <img src="images/hewlett-logo-small.jpg" height="28" alt="William and Flora Hewlett Foundation Logo"/>
                </a>
                <p>
                    <a href="http://www.hewlett.org/Default.htm" title="Click here to visit the William and Flora Hewlett Foundation">
                        The William and Flora Hewlett Foundation
                    </a>
                </p>
            </div>

            <div class="sponsor wide">
                <table>
                    <tr>
                        <td class="other-languages">
                            <a href="simulations/translations.php">
                                <img src="images/un-flag-43x43.png" alt="UN Flag" />
                            </a>
                        </td>

                        <td>
                            <p>
                                <a href="simulations/translations.php">
                                    Other languages...
                                </a>
                            </p>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="clear"></div>
        </div>

        <div class="clear"></div>

        <div class="practices">
            <dl>
                <dt onclick="javascript:location.href='get_phet/index.php'"><a href="get_phet/index.php">Run our Simulations</a></dt>

                <dd><a href="simulations/index.php">On Line</a></dd>

                <dd><a href="get_phet/full_install.php">Full Installation</a></dd>

                <dd><a href="get_phet/simlauncher.php">One at a Time</a></dd>
            </dl>

            <dl>
                <dt onclick="javascript:location.href='teacher_ideas/index.php'"><a href="teacher_ideas/index.php">Teacher Ideas &amp; Activities</a></dt>

                <dd><a href="teacher_ideas/browse.php">Search for lesson plans and activities that were created by teachers to use with the PhET simulations</a></dd>
            </dl>

            <dl>
                <dt onclick="javascript:location.href='contribute/index.php'"><a href="contribute/index.php">Contribute</a></dt>

                <dd><a href="teacher_ideas/index.php">Provide ideas you've used in class</a></dd>

                <dd><a href="contribute/index.php">Support PhET</a></dd>

                <dd><a href="contribute/translate.php">Translate Simulations</a></dd>
            </dl>

            <dl class="last">
                <dt onclick="javascript:location.href='simulations/index.php'"><a href="simulations/index.php">Browse Sims</a></dt>

                <dd>
                    <a class="nolink" href="simulations/index.php">
EOT;
                            display_slideshow(sim_get_static_previews(), "150", "110");
                        print <<<EOT
                    </a>
                </dd>

                <dd class="readMore"><a href="simulations/index.php"><img src="images/search.gif" alt="Search" title="Search" /></a></dd>
            </dl>

            <div class="home-page-links">
                <a href="about/index.php">About PhET</a>
            </div>
        </div>

        <div id="footer">
            <p>&copy; 2007 University of Colorado. All rights reserved.</p>
        </div>

        <div id="utility-panel">
            $utility_panel_html
        </div>
    </div>
</body>
</html>

EOT;

    cache_auto_end();
?>