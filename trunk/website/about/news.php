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

        $newsletters = newsletter_get_all();

        print <<<EOT
            <h2>Subscribe to Newsletter</h2>

            <p>The best way to learn about changes to the PhET simulations or website is to <a href="{$this->prefix}teacher_ideas/subscribe-newsletter.php">subscribe to PhET newsletters</a>, distributed approximately four times per year.</p>

            <p>You may unsubscribe from the newsletters at any time.</p>

            <h2>Website &amp; Simulation Updates</h2>

            <p>{$changes_html}</p>

            <h2>Newsletter Archive</h2>

EOT;

            if (count($newsletters) == 0) {
                print "<p>No newsletters have been issued yet.</p>";
            }
            else {
                $counter = 0;

                print "<ul>";

                foreach ($newsletters as $time => $newsletter) {
                    $date    = date('F j Y', $time);
                    $subject = $newsletter['newsletter_subject'];

                    print "<li><a href=\"#newsletter$counter\">$date - $subject</a>";

                    $counter++;
                }

                print "</ul>";

                $counter = 0;

                foreach ($newsletters as $time => $newsletter) {
                    $date    = date('F j Y', $time);
                    $subject = $newsletter['newsletter_subject'];
                    $body    = $newsletter['newsletter_body'];

                    $body = str_replace('  ', '&nbsp;&nbsp;', $body);
                    $body = preg_replace('/\s*(\r)?\n+(\s*((\r)?\n)+)?\s*/', '<br/>', $body);

                    print <<<EOT
                        <h3 id="newsletter$counter">$date - $subject</h3>
                        <p><em>$body</em></p>

EOT;
                    $counter++;
                }
            }
    }

}

$page = new NewsPage("News", NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>