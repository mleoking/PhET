<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class AboutPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
        <p>PhET Interactive Simulations is an ongoing effort to provide
an extensive suite of simulations to improve the way that physics, chemistry,
biology, earth science and math are taught and learned.   The <a
href="{$this->prefix}simulations/index.php">simulations</a> are
interactive tools that enable students to make connections between real life
phenomena and the underlying science which explains such phenomena.  Our team
of scientists, software engineers and science educators use a <a
href="{$this->prefix}research/index.php">research-based approach</a> –
incorporating findings from prior research and our own testing – to create
simulations that support student engagement with and understanding of
scientific concepts. </p>

<p>In order to help students visually comprehend thse concepts,
PhET simulations animate what is invisible to the eye through the use of
graphics and intuitive controls such as click-and-drag manipulation, sliders
and radio buttons.  In order to further encourage quantitative exploration, the
simulations also offer measurement instruments including rulers, stop-watchs,
voltmeters and thermometers.  As the user manipulates these interactive tools,
responses are immediately animated thus effectively illustrating
cause-and-effects relationships as well as multiple linked representations
(motion of the objects, graphs, number readouts, etc…).</p>

<p>To ensure eduational effectiveness and usability, all of the
simulations are extensively tested and evaluated.  These tests include student
interviews in addition to actual utilization of the simulations in a variety of
settings, including lectures, group work, homework and lab work.   Our <a
href="{$this->prefix}about/legend.php">rating system</a> indicates
what level of testing has been completed on each simulation. </p>

<p>All PhET simulations are freely available from the <a
href="{$this->prefix}index.php">PhET website</a> and are easy to use
and incorpate into the classroom. They are written in <a
href="{$this->prefix}tech_support/support-java.php">Java</a> and <a
href="{$this->prefix}tech_support/support-flash.php">Flash</a>, and
can be run using a standard web browser as long as <a
href="{$this->prefix}tech_support/support-flash.php">Flash</a> and <a
href="{$this->prefix}tech_support/support-java.php">Java</a> are
installed. </p>

<br/>
EOT;
        return true;
    }

}

$page = new AboutPage("About PhET", NavBar::NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>
