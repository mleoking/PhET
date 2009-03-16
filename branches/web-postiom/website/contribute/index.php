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

            <h2>Financial Contributions</h2>

            <p>Our philosophy is to make PhET simulations freely available to all users around the world. They have now been run millions of times from our web site, and the full PhET suite has been installed on thousands of computers. But while the simulations may be free to users, they are expensive for us to create, test and maintain, and our financial support is limited. If you would like to help make it possible for us to develop more and better simulations, please contact <a href="mailto:{$phet_help_email}?Subject=Financial%20Contribution&amp;Body=Dear%20Sir%20or%20Madam:%20I%20would%20like%20to%20make%20a%20generous%20donation%20to%20PhET.">{$phet_help_email}</a> or phone (303-492-4367) to find out how you can make a tax deductible contribution.</p>

            <p>PhET would like to thank <a href="{$this->prefix}sponsors/index.php">our sponsors</a>, and <a href="http://www.royalinteractive.com/">Royal Interactive</a> for original site design and layout.</p>

            <h2>Translate Simulations</h2>

            <p>The PhET simulations have been written so that they are easily translated to languages other than English. The process uses a simple tool called the <a href="{$this->prefix}contribute/translation-utility.php">PhET Translation Utility</a>, and requires no programming skills.</p>

            <p>Translating the PhET simulations into other languages greatly expands our target audience, and helps us accomplish the <a href="{$this->prefix}about/index.php">PhET mission</a>.</p>

EOT;
    }

}

$page = new ContributePage("Contribute", NAV_CONTRIBUTE, null);
$page->update();
$page->render();

?>