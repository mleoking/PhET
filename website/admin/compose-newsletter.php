<?php

include_once("Mail/Queue.php");

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/newsletter-config.php");

class ComposeNewsletterPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $contributor_email = $this->user["contributor_email"];

        $unsubscribe_link = "http://".PHET_DOMAIN_NAME."/new/teacher_ideas/user-edit-profile.php";

        print <<<EOT
            <p>
                Using the form below, you can send email to everyone who has a PhET account
                and who has elected to receive emails from PhET.
            </p>

            <p>
                The following jokers are dynamically replaced for each recipient:
            </p>

            <ul>
                <li>\$NAME\$</li>

                <li>\$DATE\$</li>
            </ul>

            <form id="composenewsletter" action="dispatch-newsletter.php" method="post">
                <fieldset>
                    <legend>Newsletter</legend>

                    <div class="field">
                        <span class="label_content">
                            <input type="text" size="40" name="newsletter_from" value="$contributor_email" />
                        </span>

                        <span class="label">
                            from address
                        </span>
                    </div>

                    <div class="field">
                        <span class="label_content">
                            <input type="text" size="40" name="newsletter_subject" value="Announcement from PhET"/>
                        </span>

                        <span class="label">
                            email subject
                        </span>
                    </div>

                    <div class="field">
                        <span class="label_content">
<textarea rows="30" cols="40" name="newsletter_body" onfocus="javascript:this.select();">

Dear \$NAME\$,

PhET is great!

Regards,

The PhET Team

----

If you would like to unsubscribe from the PhET mailing list, please visit {$unsubscribe_link}

</textarea>
                        </span>

                        <span class="label">
                            email body
                        </span>
                    </div>

                    <div class="field">
                        <span class="label_content">
                            <input type="submit" value="Send" />
                        </span>
                    </div>

                    <br/>
                </fieldset>
            </form>

EOT;
    }

}

$page = new ComposeNewsletterPage("Compose Newsletter", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>