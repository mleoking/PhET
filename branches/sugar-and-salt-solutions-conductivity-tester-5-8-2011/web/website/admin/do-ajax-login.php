<?php

// TODO: I have not yet set about grokking this file.

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/authentication.php");
    require_once("include/contrib-utils.php");
    require_once("include/web-utils.php");
    require_once("include/sys-utils.php");

    $lookup_by_email = false;
    $lookup_by_name  = false;

    if (isset($_REQUEST['action'])) {
        $action = $_REQUEST['action'];

        if ($action == 'login') {
            $user_authenticated = auth_do_validation();
            if ($user_authenticated) {
                print <<<EOT
                    <p class="validation-success">You have been successfully logged in.</p>
EOT;
                exit;
            }
        }
        else if ($action == 'create') {
            $contributor_email        = $_REQUEST['contributor_email'];
            $contributor_name         = $_REQUEST['contributor_name'];
            $contributor_password     = $_REQUEST['contributor_password'];
            $contributor_organization = $_REQUEST['contributor_organization'];

            if (!contributor_is_contributor($contributor_email)) {
                $contributor_id = contributor_add_new_contributor($contributor_email, $contributor_password);

                contributor_update_contributor($contributor_id,
                    array(
                        'contributor_name'         => $contributor_name,
                        'contributor_organization' => $contributor_organization
                    )
                );
                $user_authenticated = auth_do_validation();

                print <<<EOT
                    <p class="validation-success">An account for $contributor_email has successfully been created.</p>
EOT;

                exit;
            }
        }
    }

    if (isset($_REQUEST['contributor_password'])) {
        $contributor_password = $_REQUEST['contributor_password'];
    }
    else {
        $contributor_password = '';
    }

    $contributor_email = '';

    if (isset($_REQUEST['contributor_email'])) {
        $contributor_email = $_REQUEST['contributor_email'];

        $contributor = contributor_get_contributor_by_username($contributor_email);

        $lookup_by_email = true;
    }
    else if (isset($_REQUEST['contributor_name'])) {
        $contributor_name = $_REQUEST['contributor_name'];

        $contributor = contributor_get_contributor_by_name($contributor_name);

        $lookup_by_name = true;
    }

    if (isset($contributor) && $contributor) {
        // The name/email, at least, refers to a real contributor.

        if ($lookup_by_name) {
            // Have to (?) scramble the e-mail, for security reasons:
            $contributor_email = $contributor['contributor_email'];

            $matches = array();

            if (preg_match('/([^@]+)(@.+)$/i', $contributor_email, $matches) == 1) {
                $email_username = $matches[1];
                $email_domain   = $matches[2];

                $disguised_email_username = preg_replace('/(?<=.)./i', '?', $email_username);

                $scrambled_contributor_email = $disguised_email_username.$email_domain;
            }
            else {
                $scrambled_contributor_email = '?';
            }
        }
        else if ($lookup_by_email) {
            $contributor_name = $contributor['contributor_name'];

            $scrambled_contributor_email = $contributor_email;
        }

        print <<<EOT
            <div class="field">
                <span class="label_content">
                    <input type="text" name="contributor_email" id="contributor_email_uid"
                        value="$scrambled_contributor_email" onchange="javascript:on_email_entered();"
                        onkeyup="javascript:on_email_change();"
                        onfocus="javascript:select_question_marks_in_input('contributor_email_uid');"
                        onclick="javascript:select_question_marks_in_input('contributor_email_uid');"
                         />

                    <span id="ajax_email_comment_uid">
                    </span>
                </span>

                <span class="label">your email</span>
            </div>
EOT;


        print <<<EOT
            <div class="field">
                <span class="label_content">
                    <input type="password" name="contributor_password" id="contributor_password_uid" onchange="javascript:on_password_change();" onkeyup="javascript:on_password_change();" value="$contributor_password" />

                    <span id="ajax_password_comment_uid">
                    </span>
                </span>

                <span class="label">your password</span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="button" name="enter" value="Login" onclick="javascript:login_login();"/>
                </span>
            </div>
EOT;
    }
    else {
        print <<<EOT
            <div class="field">
                <span class="label_content">
                    <input type="text" name="contributor_email" id="contributor_email_uid" onchange="javascript:on_email_entered();" onkeyup="javascript:on_email_change();" value="$contributor_email" />

                    <span id="ajax_email_comment_uid">
                    </span>
                </span>

                <span class="label">your email</span>
            </div>

            <div class="field">
                <span class="label_content">
                    // FIXME: Possible security problem here, not sure where this file is used yet
                    <input type="password" name="contributor_password" id="contributor_password_uid" value="$contributor_password" />

                    <span id="ajax_password_comment_uid">
                    </span>
                </span>

                <span class="label">choose password</span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="text" name="contributor_organization" id="contributor_organization_uid"
                        onkeyup="javascript:on_contributor_organization_change();" />
                </span>

                <span class="label">your organization</span>
            </div>

            <div class="field">
                <span class="label_content">
                    <input type="button" name="enter" value="Create Account" onclick="javascript:login_create_account();"/>
                </span>
            </div>
EOT;
    }

?>