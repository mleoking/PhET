<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class AboutPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
        <p>
        The Physics Education Technology (PhET) project is an ongoing effort to provide an extensive suite of simulations for teaching and learning physics and chemistry and to make these resources both freely available from the <a href="{$this->prefix}index.php">PhET website</a> and easy to incorporate into classrooms.
        </p>

        <p>The <a href="{$this->prefix}simulations/index.php">simulations</a> are animated, interactive, and game-like environments in which students learn through exploration. In these simulations, we emphasize the connections between real life phenomena and the underlying science and seek to make the visual and conceptual models that expert physicists use accessible to students.
         </p>


        <p>Our team of scientists, software engineers and science educators uses a <a href="{$this->prefix}research/index.php">research-based approach</a> in our design – incorporating findings from prior research and our own testing – to create simulations that support student engagement with and understanding of physics concepts.
        </p>

        <p>
        PhET simulations animate what is invisible to the eye, such as atoms, electrons, photons and electric fields. User interaction is encouraged by engaging graphics and intuitive controls that include click-and-drag manipulation, sliders and radio buttons. By immediately animating the response to any user interaction, the simulations are particularly good at establishing cause-and-effect and at linking multiple representations.
        </p>

        <p>For quantitative exploration, the simulations have measurement instruments available, such as a ruler, stop-watch, voltmeter and thermometer. All the simulations are extensively tested for usability and educational effectiveness, and a <a href="{$this->prefix}about/legend.php">rating system</a> is used to indicate what level of testing they have received. The tests involve student interviews and use of the simulations in a variety of settings, including lectures, group work, homework and lab work.
        </p>

        <p>The PhET simulations are easy to use. They are written in <a href="{$this->prefix}tech_support/support-java.php">Java</a> and <a href="{$this->prefix}tech_support/support-flash.php">Flash</a>, and can be run using a standard web browser as long as the latest Flash and Java plug-ins are installed.
        </p>

EOT;
        return true;
    }

}

$page = new AboutPage("About PhET", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>
