<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class ContactPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_help_email = PHET_HELP_EMAIL;

        print <<<EOT
        <div>
            <h2 style="margin-bottom: -10px;">The PhET Project:</h2>

            <p style="margin-left:0px;">c/o Marj Frankel<br />
            University of Colorado 390 UCB<br />
            Boulder, CO 80309-0390<br /></p>

            <h2 style="margin-bottom: -10px;">License Information:</h2>

            <p style="margin-left:0px;"><a href="licensing.php"><em>Click here</em></a> to access the licensing information for the interactive simulations and their source code.</p>  

            <h2 style="margin-bottom: -10px;">Email:</h2>

            <p style="margin-left:0px;">Please address all electronic correspondence to: <a href="mailto:{$phet_help_email}">{$phet_help_email}</a> Information on contributing to PhET can be found <a href="{$this->prefix}contribute/index.php"><em>here.</em></a></p>
        </div>

EOT;
    }

}

$page = new ContactPage("Contact", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>
