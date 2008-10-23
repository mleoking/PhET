<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class ForgotPasswordPage extends SitePage {

    function reset_password($contributor_id) {
        // Generate a password
        // Doesn't need to be extremely fancy, a random string should suffice
        // This isn't the Pentagon, ya know!

        // Make a pool of valid characters, avoid confusions like 
        // 1 and i and l and 0 and O with certain fonts 
        $chars = "abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
        $chars_len = strlen($chars);
        $new_password = "";
        for ($i = 0; $i < 8; ++$i) {
            $new_password .= substr($chars, rand(0, $chars_len), 1);
        }

        $encrypted_password = encrypt_password($new_password);
        db_update_table('contributor', array('contributor_password' => $encrypted_password), 'contributor_id', $contributor_id);

        return $new_password;
    }

    function email_password($email, $password) {
        $message = "Your password has been reset.\n";
        $message .= "Heour new password: {$password}\n\n";
        $message .= wordwrap("You may use it to login.  You will probably want to ".
                "change your password to something more memorable right away.  ".
            "You can do so with this link:")."\n";
        $message .= "   http://".PHET_DOMAIN_NAME."/teacher_ideas/user-edit-profile.php\n\n";
        $message .= wordwrap("Login with this link and the above password, and then you ".
            "can change your password with the first two input boxes.")."\n\n";
        $message .= "If you have any problems, help is available from:\n";
        $message .= "   ".PHET_HELP_EMAIL."\n\n";
        $message .= "Thanks,\n";
        $message .= "The PhET Team\n";

        $headers =
            "From: 'PhET Help' <".PHET_HELP_EMAIL.">"."\n".
            "Reply-To: 'PhET Help' <".PHET_HELP_EMAIL.">";

        mail($email, "PhET: Your password has been reset", $message, $headers);
        return $message;
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->prompt_user = true;
        $this->success = false;
        $this->message = "";

        if (isset($_REQUEST["contributor_email"])) {
            $val_array = array('contributor_email' => VT_VALID_EMAIL);
            $result = $this->validate_array($_REQUEST, $val_array, false);

            if ($result) {
                // Get the contirbutor
                $contributor = contributor_get_contributor_by_username($_REQUEST["contributor_email"]);
                if ($contributor) {
                    // Got contributor email
                    $new_password = $this->reset_password($contributor["contributor_id"]);
                    $message = $this->email_password($contributor["contributor_email"], $new_password);
                    $this->success = true;
                    $this->prompt_user = false;
                    $phet_domain_name = PHET_DOMAIN_NAME;
                    $phet_help_email = PHET_HELP_EMAIL;
                    $this->message = <<<EOT
                    <p>
                        Your password has been reset and emailed to your email account.
                        Check your email, and if it doesn't show up right away, check your junk mail filters.                    </p>
                    <p>
                        You may use your new password to login.  You will probably want to 
                        change your password to something more memorable right away.
                        You can do so by going to the 
                        <a href="http://{$phet_domain_name}/teacher_ideas/user-edit-profile.php">Edit User Profile page</a>
                    </p>
                    <p>
                        Login with your new password, and then you
                        can change your password with the first two input boxes.
                    </p>
                    <p>
                        If you have any problems, help is available from:
                        <a href="mailto:{$phet_help_email}">{$phet_help_email}</a>.
                    </p>

EOT;
                }
                else {
                    // INVALID: email address is not on file
                    $this->message = "<span class=\"error_text\">Sorry, that email address does not exist in our database</span>";
                }
            }
            else {
                // INVALID INFO: email address is invalid
                $this->message = "<span class=\"error_text\">Sorry, that email address is invalid, please try again.</span>";
            }
        }

        if ($result) {
            // An email was submitted
            $this->elseish = true;
        }
        else {
            // No email, give option to enter
            $this->elseish = false;
        }
    }
    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if ($this->authenticate_get_level() > AUTHLEVEL_NONE) {
            $phet_domain_name = PHET_DOMAIN_NAME;
            print <<<EOT
            <p>
                You are already logged in.  If you would like to change your password, use the 
                <a href="http://{$phet_domain_name}/teacher_ideas/user-edit-profile.php">Edit User Profile page</a>.
            </p>

EOT;
            return;
        }

        $given_email = (isset($_REQUEST["contributor_email"])) ? $_REQUEST["contributor_email"] : "";

        if (!empty($this->message)) {
            print "<p>".$this->message."</p>";
        }

        if ($this->prompt_user) {
            print <<<EOT
            <form method="post" action="forgot-password.php">
            <table class="form">
                <tr>
                    <td colspan="3">
                        <p>
                            Enter your email address and your password will be regenerated and emailed to you.
                        </p>
                    </td>
                </tr>
                <tr>
                    <td>
                        email*
                    </td>
                    <td>
                        <input type="text" name="contributor_email" size="15" value="{$given_email}" />
                    </td>
                    <td>
                        <input type="submit" value="Submit" />
                    </td>
                </tr>
            </table>
            </form>

EOT;
        }
    }

}

$page = new ForgotPasswordPage("Forgot Password", NAV_GET_PHET, get_referrer(), AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>