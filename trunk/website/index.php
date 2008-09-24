<?php

define("SITE_ROOT", "./");
include_once(SITE_ROOT."page_templates/SitePage.php");

class MainPage extends SitePage {
    function __construct($title, $nav, $referrer) {
        parent::__construct($title, $nav, $referrer);
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
                            <p>&copy; 2008 University of Colorado. <a href="about/licensing.php">Some rights reserved.</a></p>
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
        print <<<EOT
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
                            <a href="about/news.php">What's&nbsp;New</a> |
                            <a href="about/index.php">About&nbsp;PhET</a>
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

                <div id="hotlinks">
                <p class="findOutMore" onclick="javascript:location.href='simulations/index.php?cat=Top_Simulations'">
                    <a href="simulations/index.php?cat=Top_Simulations">
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
        </div>

        <div id="home-page-sponsors">
            <div class="sponsor">
                <a href="http://www.ksu.edu.sa/" title="Click here to visit the King Saud University">
                    <img src="images/ksu-logo.gif" height="28" alt="King Saud University Logo"/>
                </a>
                <p>
                    <a href="http://www.ksu.edu.sa/" title="Click here to visit the King Saud University">
                        King Saud University
                    </a>
                </p>
            </div>

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

                <dd><a href="teacher_ideas/browse.php">Search for lessons created by teachers using PhET simulations.</a></dd>

                <dd><a href="teacher_ideas/workshops.php">Workshops</a></dd>

            </dl>

            <dl>
                <dt onclick="javascript:location.href='contribute/index.php'"><a href="contribute/index.php">Contribute</a></dt>

                <dd><a href="teacher_ideas/index.php">Provide ideas you've used in class</a></dd>

                <dd><a href="contribute/index.php">Support PhET</a></dd>

                <dd><a href="contribute/translation-utility.php">Translate Simulations</a></dd>
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

            <div class="clear"></div>
        </div>

EOT;
    }

}

$page = new MainPage("PhET: Free online physics, chemistry, biology, earth science and math simulations", NAV_NOT_SPECIFIED, null);
$page->set_prefix(SITE_ROOT);
$page->add_stylesheet("css/home.css");
$page->update();
$page->render();

?>
