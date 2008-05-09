<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class SubscribeNewsletterPage extends SitePage {

    function authenticate_user() {
        $this->new_user = false;
        if (isset($_REQUEST["contributor_email"])) {
            // See if the user exists
            $contributor = contributor_get_contributor_by_username($_REQUEST["contributor_email"]);
            if (!$contributor) {
                $this->new_user = true;
            }
        }

        return parent::authenticate_user();
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // User is logged in
        $this->already_subscribed = false;
        $this->success = false;
        if ($this->user["contributor_receive_email"]) {
            $this->already_subscribed = true;
        }
        else {
            $this->user["contributor_receive_email"] = 1;
            contributor_update_contributor($this->user["contributor_id"], $this->user);
            $this->success = true;
        }
    }

    function render_content() {
        if ($this->authentication_level == AUTHLEVEL_NONE) {
            print <<<EOT
                <p>
                    The PhET newsletter contains information on major updates to the simulations, and is issued <strong>four times per year</strong>.
                    You may unsubscribe at any time.
                </p>
                <p>
                    Your account has been created and you are now logged in.
                </p>

EOT;
        }

        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if ($this->success || $this->new_user) {
            print <<<EOT
                <h2>Subscription Successful!</h2>

                <p>
                    Thank you, {$this->user["contributor_name"]}, for subscribing to the PhET newsletter!
                </p>

                <p>
                    <a href="{$this->prefix}index.php">Home</a>
                </p>

EOT;
        }
        else if ($this->already_subscribed) {
            print <<<EOT
                <p>
                {$this->user["contributor_name"]}: you are already subscribed to the PhET newsletter. To unsubscribe, <a href="{$this->prefix}teacher_ideas/user-edit-profile.php">edit your profile</a> and uncheck the box marked 'Receive PhET Newsletter'.
                </p>

EOT;
        }

        return;

        if (isset($_REQUEST['contributor_email'])) {
            $subscribing = true;
        }
        else {
            $subscribing = false;
        }

        $contributor = $this->user;
        if (!$contributor && (isset($_REQUEST['contributor_email']))) {
            $contributor = contributor_get_contributor_by_username($_REQUEST['contributor_email']);
        }

        if (isset($contributor["contributor_receive_email"]) && 
            ($contributor["contributor_receive_email"] == 1) &&
            (!isset($this->new_user) || (!$this->new_user))) {
            print <<<EOT
                <p>
                {$contributor["contributor_name"]}: you are already subscribed to the PhET newsletter. To unsubscribe, <a href="{$this->prefix}teacher_ideas/user-edit-profile.php">edit your profile</a> and uncheck the box marked 'Receive PhET Newsletter'.
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
                    <a href="{$this->prefix}index.php">Home</a>
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

$page = new SubscribeNewsletterPage("Subscribe to PhET", NAV_TEACHER_IDEAS, null, AUTHLEVEL_USER, false);
$page->update();
$page->render();

?>