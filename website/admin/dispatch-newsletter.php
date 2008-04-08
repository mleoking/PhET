<?php

// This file looks like it is in the middle of testing, and not completely implemented.
// I have not yet checked it thoroughly enough to see if this is so.  -Dano Apr 8, 2008

$ONLY_SEND_TO = "formv09@yahoo.com";

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

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
            replace_jokers($newsletter_subject, $no_contributor),
            replace_jokers($newsletter_body,    $no_contributor)
        );

        foreach(contributor_get_all_contributors() as $contributor) {
            $subs_newsletter_subject = replace_jokers($newsletter_subject, $contributor);
            $subs_newsletter_body    = replace_jokers($newsletter_body,    $contributor);

            if (($contributor['contributor_receive_email'] == 1) && ($contributor['contributor_email'] == $ONLY_SEND_TO)) {
                mail($contributor['contributor_email'],
                     $subs_newsletter_subject,
                     $subs_newsletter_body,
                     "From: $newsletter_from");
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

$page = new DispatchNewsletterPage("Dispatch Newsletter", NAV_ADMIN, null, SP_AUTHLEVEL_TEAM);
$page->update();
$page->render();

?>