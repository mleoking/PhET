<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class SimulationLauncherPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <p>
                Click on the name of any simulation to download the simulation to your computer. To run the simulation, double-click it. If you have difficulties, please see our <a href="{$this->prefix}tech_support/index.php">technical support</a> page.
            </p>

            <p>
                You may copy the simulation to any place on your computer. You may also copy the simulation to a CD-ROM Drive, USB Drive, or shared network location.
            </p>

            <div id="individual-sim-installers">

                <ul>

EOT;

        foreach (SimFactory::inst()->getAllSims(true) as $simulation) {
                print <<<EOT
                    <li>
                    <a href="{$simulation->getDownloadUrl()}">{$simulation->getName()}</a>
                    </li>

EOT;
            }

            print <<<EOT
                </ul>

            </div>

EOT;
    }

}

$page = new SimulationLauncherPage("Launch Sim One at a Time", NavBar::NAV_GET_PHET, null);
$page->update();
$page->render();

?>