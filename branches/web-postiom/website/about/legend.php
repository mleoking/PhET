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

        $Sim = SimUtils::inst();
        $gold_star_html  = contribution_get_gold_star_html();

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
                            <td>{$Sim->getGuidanceImageTag()}</td>       <td><strong>Guidance Recommended</strong>: This simulation is very effective when used in conjunction with a lecture, homework  or other teacher designed activity.</td>
                        </tr>

                        <tr>
                            <td>{$Sim->getRatingImageTag(SimUtils::SIM_RATING_ALPHA)}</td>        <td><strong>Under Construction</strong>: This simulation is a preview version, and may have functional or usability bugs.</td>
                        </tr>

                        <tr>
            <td>{$Sim->getRatingImageTag(SimUtils::SIM_RATING_CHECK)}</td>        <td><strong>Classroom Tested</strong>: This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews.</td>
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
                                <td>{$Sim->getContributionFromPhetImageTag()}</td>    <td><strong>PhET Designed</strong>: This contribution was designed by PhET.</td>
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