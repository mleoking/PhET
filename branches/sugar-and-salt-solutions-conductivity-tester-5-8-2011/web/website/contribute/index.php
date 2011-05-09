<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class ContributePage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_help_email = PHET_HELP_EMAIL;

        print <<<EOT
            <h2>Academic Contributions</h2>

            <p>If you have developed concept questions, problem sets, lesson plans, and other educational activities based on PhET simulations that may be of use to others, we encourage you to share your work with other educators by <a href="{$this->prefix}teacher_ideas/contribute.php">contributing it to PhET</a>.</p>

            <h2>Suggest a Simulation</h2>

            <p>If you have an idea for a PhET simulation, please <a href="mailto:{$phet_help_email}?Subject=Simulation%20Suggestion&amp;Body=I%20would%20like%20to%20suggest%20the%20following%20simulation:">let us know</a>.</p>

            <h2>Translate Simulations</h2>

            <p>The PhET simulations have been written so that they are easily translated to languages other than English. The process uses a simple tool called the <a href="{$this->prefix}contribute/translation-utility.php">PhET Translation Utility</a>, and requires no programming skills.</p>

            <p>Translating the PhET simulations into other languages greatly expands our target audience, and helps us accomplish the <a href="{$this->prefix}about/index.php">PhET mission</a>.</p>

EOT;
    }

}

$page = new ContributePage("Contribute", NavBar::NAV_CONTRIBUTE, null);
$page->update();
$page->render();

?>