<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/newsletter-utils.php");

class NewsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $changes_file = file_get_contents(SITE_ROOT."about/changes.txt");
        $changes_file = htmlentities($changes_file);
        $changes_html = preg_replace('/ *\n+ */', '<br/>', $changes_file);

        $newsletters_dir = PORTAL_ROOT."phet-dist/newsletters/";

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