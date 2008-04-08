<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class SubscribeNewsletter extends SitePage {

    function authenticate_user() {
        if (isset($_REQUEST["contributor_email"])) {
            // See if the user exists
            $contributor = contributor_get_contributor_by_username($_REQUEST["contributor_email"]);
            if (!$contributor) {
                $this->new_user = true;
            }
        }

        return parent::authenticate_user();
    }

    function subscribe_user() {
        if (isset($_REQUEST['contributor_email'])) {
            $contributor_email = $_REQUEST['contributor_email'];

            $contributor = contributor_get_contributor_by_username($contributor_email);

            if (!$contributor) {
                $contributor_id = contributor_add_new_contributor($contributor_email, "");

                $contributor = contributor_get_contributor_by_id($contributor_id);

                // Fill in organization, name, desc, if present:
                if (isset($_REQUEST['contributor_organization'])) {
                    $contributor['contributor_organization'] = $_REQUEST['contributor_organization'];
                }
                if (isset($_REQUEST['contributor_name'])) {
                    $contributor['contributor_name'] = $_REQUEST['contributor_name'];
                }
                if (isset($_REQUEST['contributor_desc'])) {
                    $contributor['contributor_desc'] = $_REQUEST['contributor_desc'];
                }
            }

            $contributor['contributor_receive_email'] = '1';

            contributor_update_contributor($contributor['contributor_id'], $contributor);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (isset($_REQUEST['contributor_email'])) {
            $subscribing = true;
        }
        else {
            $subscribing = false;
        }

        $contributor = contributor_get_contributor_by_username(auth_get_username());
        if (!$contributor && (isset($_REQUEST['contributor_email']))) {
            $contributor = contributor_get_contributor_by_username($_REQUEST['contributor_email']);
        }

        if (isset($contributor["contributor_receive_email"]) && 
            ($contributor["contributor_receive_email"] == 1) &&
            (!isset($this->new_user) || (!$this->new_user))) {
            print <<<EOT
                <p>
                {$contributor["contributor_name"]}: you are already subscribed to the PhET newsletter. To unsubscribe, <a href="../teacher_ideas/user-edit-profile.php">edit your profile</a> and uncheck the box marked 'Receive PhET Newsletter'.
                </p>

EOT;
        }
        else if ($subscribing) {
            $this->subscribe_user();

            print <<<EOT
                <h2>Subscription Successful!</h2>

                <p>
                    Thank you, {$contributor["contributor_name"]}, for subscribing to the PhET newsletter!
                </p>

                <p>
                    <a href="../index.php">Home</a>
                </p>

EOT;
        }
        else {
            print <<<EOT
                <p>
                    The PhET newsletter contains information on major updates to the simulations, and is issued <strong>four times per year</strong>.
                    You may unsubscribe at any time.
                </p>
                <p>
                    Your account has been created and you are now logged in.
                </p>

EOT;
            print_login_and_new_account_form("subscribe-newsletter.php", "subscribe-newsletter.php", null);
        }
    }
}

$page = new SubscribeNewsletter("Subscribe to PhET", NAV_TEACHER_IDEAS, null);
$page->update();
$page->render();

?>