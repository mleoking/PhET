<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/referrer.php");

class ComingSoonPage extends SitePage {

    function render_content() {
        print <<<EOT
        <div>   
            <p><em>Coming soon for the April 2010 issue of the Physics Teacher.</em></p>
        </div>

EOT;

    }

}

$page = new ComingSoonPage("Using PhET Sims in the Classroom", NavBar::NAV_TEACHER_IDEAS, get_referrer(SITE_ROOT.'teacher_ideas/manage-contributions.php'), SitePage::AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>

