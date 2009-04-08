<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "./");

// This file is in the root, don't need to change include_path,
// see global.php for an explaination.
if (!defined("INCLUDE_PATH_SET")) define("INCLUDE_PATH_SET", "true");

require_once("include/PageTemplates/BasePage.class.php");
require_once("include/PageTemplates/SitePage.class.php");

class MainPage extends SitePage {
    function __construct($title, $nav) {
        parent::__construct($title, $nav, null);
    }

    function open_xhtml_body() {
        BasePage::open_xhtml_body();
    }

    /**
     * Output HTML associated with end of the body section
     *
     */
    function close_xhtml_body() {
        $utility_panel_html = $this->get_login_panel();

        print <<<EOT
                        </div>

                        <div id="footer">
                            <p>&copy; 2009 University of Colorado. <a href="about/licensing.php">Some rights reserved.</a></p>
                        </div>

                <div id="utility-panel">
                    $utility_panel_html
                </div>
            </body>

EOT;
    }

    function render_title() {
    }

    function render_navigation_bar() {
        // No nav bar
    }

    function render_content() {
        $default_category = CategoryUtils::inst()->getDefaultCategory();
        $default_sim_category = WebUtils::inst()->encodeString($default_category['cat_name']);

        print <<<EOT
        <div class="home-page">
            <div id="newsflashes">
                <table>
                    <tr>
                        <td class="warning-notice">
                        </td>

                        <td class="links">
                            <a href="about/news.php">What's New</a> |
                            <a href="about/index.php">About PhET</a> <!-- ' -->
                        </td>
                    </tr>
                </table>
            </div>

            <div class="introduction">

                <div class="mainImage">
                    <a href="simulations/index.php" title="Click here to view the simulations"><img width="320" height="240" src="random-thumbnail.php" alt="Random screenshot of a simulation"/></a>
                </div>

                <h1>Interactive Science Simulations</h1>

                <p class="openingParagraph">Fun, interactive, <a href="research/index.php">research-based</a> simulations of physical phenomena from the PhET project at the University of Colorado.</p>

                <div id="hotlinks">
                <p class="findOutMore" onclick="javascript:location.href='simulations/index.php?cat={$default_sim_category}'">
                    <a href="simulations/index.php?cat={$default_sim_category}">
                        Play with sims... &gt;
                    </a>
                </p>

                <div>
                <div style="float: right;">
                    <a href="simulations/translations.php" style="text-decoration: none;">
                        <img src="images/un-flag-43x43.png" alt="UN Flag" style="float:left;"/>
                        <span style="float:left;padding-left:1em;padding-right:1em;padding-top:0.5em;">Other<br /> languages...</span>
                    </a>
                    &nbsp;
                </div>
                </div>

                </div>


            </div>

            <div class="clear"></div>

        <div id="home-page-spons">
            <div class="spon">
                <a href="http://www.ksu.edu.sa/" title="Click here to visit the King Saud University">
                    <img src="images/ksu-logo.gif" height="28" alt="King Saud University Logo"/>
                </a>
                <p>
                    <a href="http://www.ksu.edu.sa/" title="Click here to visit the King Saud University">
                        King Saud University
                    </a>
                </p>
            </div>

            <div class="spon">
                <a href="http://jila-amo.colorado.edu/" title="Click here to visit the JILA Center for Atomic, Molecular, &amp; Optical Physics">
                    <img src="images/jila_logo_small.gif" height="28" alt="JILA AMO Logo"/>
                </a>
                <p>
                    <a href="http://jila-amo.colorado.edu/" title="Click here to visit the JILA Center for Atomic, Molecular, &amp; Optical Physics">
                      JILA Center for Atomic, Molecular, &amp; Optical Physics 
                    </a>
                </p>
            </div>

            <div class="spon">
                <a href="http://www.nsf.gov/" title="Click here to visit the National Science Foundation" >
                    <img src="images/nsf-logo-small.gif" height="28" alt="National Science Foundation Logo"/>
                </a>
                <p>
                    <a href="http://www.nsf.gov/" title="Click here to visit the National Science Foundation" >
                        National Science Foundation
                    </a>
                </p>
            </div>

            <div class="spon spon-wide">
                <a href="http://www.hewlett.org/" title="Click here to visit the William and Flora Hewlett Foundation">
                    <img src="images/hewlett-logo-small.jpg" height="28" alt="William and Flora Hewlett Foundation Logo"/>
                </a>
                <p>
                    <a href="http://www.hewlett.org/" title="Click here to visit the William and Flora Hewlett Foundation">
                        The William and Flora Hewlett Foundation
                    </a>
                </p>
            </div>

            <div class="clear"></div>
        </div>
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

                <dd><a href="teacher_ideas/browse.php">Search for lessons created by teachers using PhET simulations.</a></dd>

                <dd><a href="teacher_ideas/workshops.php">Workshops</a></dd>

            </dl>

            <dl>
                <dt onclick="javascript:location.href='contribute/index.php'"><a href="contribute/index.php">Contribute</a></dt>

                <dd><a href="teacher_ideas/index.php">Provide ideas you've used in class</a></dd><!-- ' -->

                <dd><a href="contribute/index.php">Support PhET</a></dd>

                <dd><a href="contribute/translation-utility.php">Translate Simulations</a></dd>
            </dl>

            <dl class="last">
                <dt onclick="javascript:location.href='simulations/index.php'"><a href="simulations/index.php">Browse Sims</a></dt>

                <dd>
                    <a class="nolink" href="simulations/index.php">

EOT;
            display_slideshow(SimUtils::inst()->getAllStaticPreviewUrls(), "150", "110");

                        print <<<EOT
                    </a>
                </dd>

                <dd class="readMore"><a href="simulations/index.php"><img src="images/search.gif" alt="Search" title="Search" /></a></dd>
            </dl>

            <div class="clear"></div>
        </div>

EOT;
    }

}

$page = new MainPage("PhET: Free online physics, chemistry, biology, earth science and math simulations", NavBar::NAV_NOT_SPECIFIED);
$page->set_prefix(SITE_ROOT);
$page->add_stylesheet("css/home.css");
$page->update();
$page->render();

?>
