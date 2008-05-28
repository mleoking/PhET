<?php

// This file looks like it is in the middle of testing, and not completely implemented.
// I have not yet checked it thoroughly enough to see if this is so.  -Dano Apr 8, 2008

$ONLY_SEND_TO = "formv09@yahoo.com";

include_once("Mail/Queue.php");

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/newsletter-utils.php");
include_once(SITE_ROOT."admin/newsletter-config.php");

class DispatchNewsletterPage extends SitePage {

    function replace_jokers($text, $contributor) {
        $name = $contributor['contributor_name'];
        $date = date("F j, Y, g:i a");

        $text = str_replace('$NAME$', "$name", $text);
        $text = str_replace('$DATE$', "$date", $text);

        return $text;
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (!isset($_REQUEST['newsletter_subject']) ||
            !isset($_REQUEST['newsletter_from']) ||
            !isset($_REQUEST['newsletter_body'])) {
            return;
        }

        $newsletter_subject = $_REQUEST['newsletter_subject'];
        $newsletter_from    = $_REQUEST['newsletter_from'];
        $newsletter_body    = $_REQUEST['newsletter_body'];

        $no_contributor = array();

        $no_contributor['contributor_name'] = 'PhET User';

        newsletter_create(
            $this->replace_jokers($newsletter_subject, $no_contributor),
            $this->replace_jokers($newsletter_body,    $no_contributor)
        );

        global $news_db_options, $news_mail_options;
        $mail_queue =& new Mail_Queue($news_db_options, $news_mail_options);

        // Get all contributors with a UNIQUE email address
        foreach(contributor_get_all_contributors(true) as $contributor_email => $contributor) {
            if ($contributor['contributor_receive_email'] == 1) {
                // Replace the jokers
                $subs_newsletter_subject = $this->replace_jokers($newsletter_subject, $contributor);
                $subs_newsletter_body    = $this->replace_jokers($newsletter_body,    $contributor);

                // Create a mime container for the email
                $hdrs = array('From' => $newsletter_from,
                            'To' => $contributor_email,
                            'Subject' => $subs_newsletter_subject);
                $mime =& new Mail_mime();
                $mime->setTXTBody($subs_newsletter_body);
                $body = $mime->get();
                $hdrs = $mime->headers($hdrs);

                // Put message to queue
                $mail_queue->put( $newsletter_from, $contributor_email, $hdrs, $body );
            }
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (!isset($_REQUEST['newsletter_subject']) ||
            !isset($_REQUEST['newsletter_from']) ||
            !isset($_REQUEST['newsletter_body'])) {
            print <<<EOT
            <h2>Failure</h2>

            <p>
                The newsletter didn't have enough information specified.
            </p>

EOT;
            return;
        }

        print <<<EOT
            <h2>Success</h2>

            <p>
                The newsletter has been successfully dispatched.
            </p>

EOT;

        $this->meta_refresh('index.php', 5);
    }

}

$page = new DispatchNewsletterPage("Dispatch Newsletter", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>