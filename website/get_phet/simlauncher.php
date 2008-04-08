<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class SimulationLauncherPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <p>
                Click on the name of any simulation to download the simulation to your computer. To run the simulation, double-click it. If you have difficulties, please see our <a href="../tech_support/index.php">technical support</a> page.
            </p>

            <p>
                You may copy the simulation to any place on your computer. You may also copy the simulation to a CD-ROM Drive, USB Drive, or shared network location.
            </p>

            <div id="individual-sim-installers">

                <ul>

EOT;

            foreach (sim_get_all_sims() as $simulation) {
                $sim_name = format_string_for_html($simulation['sim_name']);

                $sim_run_offline_link = sim_get_run_offline_link($simulation);

                print <<<EOT
                    <li>
                        <a href="$sim_run_offline_link">$sim_name</a>
                    </li>

EOT;
            }

            print <<<EOT
                </ul>

            </div>

EOT;
    }
}

$page = new SimulationLauncherPage("Launch Sim One at a Time", NAV_GET_PHET, null);
$page->update();
$page->render();

?>