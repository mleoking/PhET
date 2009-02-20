<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class NewsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $changes_file = file_get_contents(SITE_ROOT."about/changes.txt");
        $changes_file = htmlentities($changes_file);
        $changes_html = preg_replace('/ *\n+ */', '<br/>', $changes_file);

        $newsletters_dir = PHET_DIST_ROOT."newsletters/";

        print <<<EOT
            <h2>Newsletter Archive</h2>

            <ul>
                <li><a href="{$newsletters_dir}phet_newsletter_july16_2008.pdf">July 16th 2008 - PhET's first newsletter</a></li>
            </ul>

            <h2>Website &amp; Simulation Updates</h2>

            <p>{$changes_html}</p>

EOT;

    }

}

$page = new NewsPage("News", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>