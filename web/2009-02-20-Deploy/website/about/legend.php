<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class LegendPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        global $SIM_RATING_TO_IMAGE_HTML;

        $crutch_html     = SIM_CRUTCH_IMAGE_HTML;
        $no_mac_html     = SIM_NO_MAC_IMAGE_HTML;
        $check_html      = $SIM_RATING_TO_IMAGE_HTML[SIM_RATING_CHECK];
        $alpha_html      = $SIM_RATING_TO_IMAGE_HTML[SIM_RATING_ALPHA];
        $gold_star_html  = contribution_get_gold_star_html();
        $from_phet_html  = FROM_PHET_IMAGE_HTML;

        print <<<EOT
            <div id="simratinglegend">
                <table>
                    <thead>
                        <tr>
                            <td>Symbol</td> <td>Meaning</td>
                        </tr>
                    </thead>

                    <tbody>
                        <tr>
                            <td>$crutch_html</td>       <td><strong>Guidance Recommended</strong>: This simulation is very effective when used in conjunction with a lecture, homework  or other teacher designed activity.</td>
                        </tr>

                        <tr>
                            <td>$alpha_html</td>        <td><strong>Under Construction</strong>: This simulation is a preview version, and may have functional or usability bugs.</td>
                        </tr>

                        <tr>
                            <td>$check_html</td>        <td><strong>Classroom Tested</strong>: This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews.</td>
                        </tr>

                        <tr>
                            <td>$gold_star_html</td>    <td><strong>Gold Star Contribution</strong>: 
                            This contribution has received a Gold Star
                            because it is a high quality inquiry-based activity that follows the
                            <a href="{$this->prefix}teacher_ideas/contribution-guidelines.php">PhET design guidelines</a>
                            (<a href="{$this->prefix}teacher_ideas/contribution-guidelines.pdf">PDF</a>)
                            and teachers have found it useful.</td>
                        </tr>

                        <tr>
                            <td>$from_phet_html</td>    <td><strong>PhET Designed</strong>: This contribution was designed by PhET.</td>
                        </tr>
                    </tbody>
                </table>
            </div>

EOT;
    }

}

$page = new LegendPage("Legend", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>