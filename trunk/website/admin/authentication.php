<?php

    if (!defined('SITE_ROOT')) {
        include_once('../admin/global.php');
    }

    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");

    // File local variables
    $authentication_complete = false;
    $authentication_result = null;
    $authentication_error = null;

    $script = get_self_url();

    $title = isset($GLOBALS['custom_title']) ? urldecode($GLOBALS['custom_title']) : "Login/Register";
    $body  = isset($GLOBALS['custom_body'])  ? urldecode($GLOBALS['custom_body'])  : "<p>Please login with your existing PhET account, or create a new one.</p>";

    function print_first_time_login_form() {
        global $title, $body, $script;

        print "<h1>$title</h1>";

        print_contribute_login_form($script, null, $script, "$body");
    }

    function print_retry_login_form() {
        global $title, $body, $script;

        print "<h1>$title - Login Incorrect</h1>";
        print_contribute_login_form($script, null, $script,
            "<p><strong>The password you entered is incorrect.</strong> If you entered the correct email address, please check your email now for a password reminder.</p>
            $body");
    }

    function print_not_an_existing_account() {
        global $title, $body, $script;

        print "<h1>$title - Unknown Account</h1>";

        print_contribute_login_form($script, null, $script,
            "<p><strong>The email address you entered does not have an existing PhET account.</strong></p>
             $body");
    }

    function print_not_an_email_login_form() {
        global $title, $body, $script;

        print "<h1>$title - Invalid Email</h1>";

        print_contribute_login_form($script, null, $script,
            "<p><strong>The email address you entered is not a valid email address.</strong></p>
             $body");
    }

    function print_empty_password_login_form() {
        global $title, $body, $script;

        print "<h1>$title - No Password Specified</h1>";

        print_contribute_login_form($script, null, $script,
                         "<p><strong>You forgot to specify a password for your new account.</strong></p>
                         $body");
    }

    function auth_clear_cookie() {
        cookie_var_clear("contributor_username");
        cookie_var_clear("contributor_password_hash");
    }

    function auth_auth_error() {
        global $authentication_error;
        return !is_null($authentication_error);
    }

    function auth_get_error() {
        global $authentication_error;
        if (is_null($authentication_error)) {
            return "No authentication error";
        }
        else {
            return $authentication_error;
        }
    }

    function auth_cookie_is_valid() {
        static $authenticated = false;

        if (cookie_var_is_stored("contributor_username") &&
            cookie_var_is_stored("contributor_password_hash")) {
            // We have a cookie, revalidate it
            $email = cookie_var_get("contributor_username");
            $encrypted_password = cookie_var_get("contributor_password_hash");

            $valid = contributor_is_valid_login($email, $encrypted_password);
            if (!$valid) {
                auth_clear_cookie();
            }

            return $valid;
        }

        return false;
    }


    function auth_complete() {
        global $authentication_complete;

        return $authentication_complete;
    }


    function auth_user_validated() {
        global $authentication_complete;
        global $authentication_result;
        // Keep this turned off until authentication has 1 method
        //assert($authentication_complete);

        return $authentication_result;
    }

    function auth_get_username() {
        global $authentication_complete;
        global $authentication_result;
        assert($authentication_complete);

        if (!$authentication_result) return null;
        return cookie_var_get("contributor_username");
    }


    function auth_do_validation() {
        // Only do the authentication once
        global $authentication_error;
        global $authentication_complete;
        global $authentication_result;
        assert(!$authentication_complete);

        // If we're here, there is a new account.  Create one.
        if (isset($_REQUEST['create_new_account']) && $_REQUEST['create_new_account'] == '1') {
            if ((isset($_REQUEST['contributor_name']) && (strlen(trim($_REQUEST['contributor_name'])) != 0)) &&
                (isset($_REQUEST['contributor_organization']) && (strlen(trim($_REQUEST['contributor_organization'])) != 0)) &&
                (isset($_REQUEST['contributor_desc']) && (strlen(trim($_REQUEST['contributor_desc'])) != 0)) &&
                (isset($_REQUEST['contributor_email']) && (strlen(trim($_REQUEST['contributor_email'])) != 0)) &&
                (isset($_REQUEST['contributor_password1']) && (strlen(trim($_REQUEST['contributor_password1'])) != 0)) &&
                (isset($_REQUEST['contributor_password2']) && (strlen(trim($_REQUEST['contributor_password2'])) != 0)) &&
                ($_REQUEST['contributor_password1'] == $_REQUEST['contributor_password2'])) {
                // make sure the account doesn't exist
                if (contributor_get_contributor_by_username($_REQUEST['contributor_email'])) {
                    $authentication_error = "Account creation faild, that email already exists";
                    $authentication_complete = true;
                    $authentication_result = false;
                    return $authentication_result;
                }

                // Create new user account:
                $contributor_id = contributor_add_new_contributor($_REQUEST["contributor_email"], $_REQUEST["contributor_password1"]);

                // Check for the other required fields that have been passed along:
                if (isset($_REQUEST['contributor_name'])) {
                    contributor_update_contributor(
                        $contributor_id,
                        array(
                            'contributor_name' => $_REQUEST['contributor_name']
                        )
                    );
                }

                if (isset($_REQUEST['contributor_organization'])) {
                    contributor_update_contributor(
                        $contributor_id,
                        array(
                            'contributor_organization' => $_REQUEST['contributor_organization']
                        )
                    );
                }

                if (isset($_REQUEST['contributor_desc'])) {
                    contributor_update_contributor(
                        $contributor_id,
                        array(
                            'contributor_desc' => $_REQUEST['contributor_desc']
                        )
                    );
                }
            }
            else {
                $authentication_error = "Account creation failed, not all information was specified or valid";
                $authentication_complete = true;
                $authentication_result = false;
                return $authentication_result;
            }
        }

        if (auth_cookie_is_valid()) {
            $authentication_complete = true;
            $authentication_result = true;
            return $authentication_result;
        }
        else {
            // No cookie, check for a login
            if (!((isset($_REQUEST['submit']) && ($_REQUEST['submit']) == 'Login') ||
                (isset($_REQUEST['create_new_account']) && $_REQUEST['create_new_account'] == '1'))) {
                $authentication_complete = true;
                $authentication_result = false;
                return $authentication_result;
            }

            // Validate email and password
            $email = $_REQUEST['contributor_email'];
            if (isset($_REQUEST['contributor_password'])) {
                $encrypted_password = md5($_REQUEST['contributor_password']);
            }
            else if (isset($_REQUEST['contributor_password1'])) {
                $encrypted_password = md5($_REQUEST['contributor_password1']);
            }

            $valid = contributor_is_valid_login($email, $encrypted_password);
            if ($valid === false) {
                $authentication_complete = true;
                $authentication_result = false;
                return $authentication_result;
            }

            cookie_var_store("contributor_username",      $email);
            cookie_var_store("contributor_password_hash", $encrypted_password);

            $authentication_complete = true;
            $authentication_result = true;
            return $authentication_result;
        }

        // Shouldn't be here
        return null;
    }

    function do_authentication($login_required = true) {
        // Don't mix authentication/validation methods
        global $authentication_complete;
        assert(!$authentication_complete);

        static $already_tried_login_required = null;
        global $contributor_authenticated;

        if (isset($_REQUEST['login_required']) && $_REQUEST['login_required'] == "true") {
            $login_required = true;
        }

        if ($login_required === $already_tried_login_required) {
            return $contributor_authenticated;
        }

        $already_tried_login_required = $login_required;

        $contributor_authenticated = false;

        // Look for cookie variables:
        if (cookie_var_is_stored("contributor_username")) {
            $contributor_email         = cookie_var_get("contributor_username");
            $contributor_password_hash = cookie_var_get("contributor_password_hash");

            // Don't trust the cookie; validate it:
            if (!contributor_is_valid_login($contributor_email, $contributor_password_hash)) {
                // Cookie is invalid. Clear it:
                cookie_var_clear("contributor_username");
                cookie_var_clear("contributor_password_hash");

                if ($login_required) {
                    force_redirect(get_self_url(), 0);

                    exit;
                }
            }
            else {
                $contributor_authenticated = true;
            }
        }
        else {
            // No cookie variables
            if (isset($_REQUEST['contributor_email'])) {
                $contributor_email = $_REQUEST['contributor_email'];
            }
            if (isset($_REQUEST['contributor_name'])) {
                $contributor_name = $_REQUEST['contributor_name'];
            }

            if ((!isset($contributor_email) || trim($contributor_email) == '') &&
                isset($contributor_name) && trim($contributor_name) != '') {
                // Username not present, but contributor name is.
                // Deduce email from contributor name:
                $contributor = contributor_get_contributor_by_name($contributor_name);

                if ($contributor) {
                    $contributor_email = $contributor['contributor_email'];
                }
            }

            if (isset($_REQUEST['contributor_password'])) {
                $contributor_password = $_REQUEST['contributor_password'];
            }

            if (!isset($contributor_email) || !isset($contributor_password)) {
                // No contributor_email/contributor_password specified, and no cookie variables.
                // Print the first-time login form:
                if ($login_required) {
                    print_site_page('print_first_time_login_form', 3);

                    exit;
                }
            }
            else {
                // Both contributor_email and contributor_password were specified.
                if (contributor_is_contributor($contributor_email)) {
                    // The contributor_email already exists and denotes a contributor. Check
                    // the contributor_password to make sure it is correct.

                    $contributor_password_hash = md5($contributor_password);


                    if (!contributor_valid_email_and_password($contributor_email, $contributor_password)) {
                        contributor_send_password_reminder($contributor_email);

                        if ($login_required) {
                            print_site_page('print_retry_login_form', 3);

                            exit;
                        }
                    }
                    else {
                        cookie_var_store("contributor_username",         $contributor_email);
                        cookie_var_store("contributor_password_hash", $contributor_password_hash);

                        $contributor_authenticated = true;
                    }
                }
                else if (is_email($contributor_email)) {
                    // The contributor_email does not exist, and is a valid e-mail address.
                    if (trim($contributor_password) == '') {
                        if ($login_required) {
                            print_site_page('print_empty_password_login_form', 3);

                            exit;
                        }
                    }
                    else if (isset($_REQUEST['create_new_account']) && $_REQUEST['create_new_account'] == '1') {
                        // Create new user account:
                        $contributor_id = contributor_add_new_contributor($contributor_email, $contributor_password);

                        // Check for optional fields that may have been passed along:
                        if (isset($_REQUEST['contributor_name'])) {
                            contributor_update_contributor(
                                $contributor_id,
                                array(
                                    'contributor_name' => $_REQUEST['contributor_name']
                                )
                            );
                        }

                        if (isset($_REQUEST['contributor_organization'])) {
                            contributor_update_contributor(
                                $contributor_id,
                                array(
                                    'contributor_organization' => $_REQUEST['contributor_organization']
                                )
                            );
                        }

                        if (isset($_REQUEST['contributor_desc'])) {
                            contributor_update_contributor(
                                $contributor_id,
                                array(
                                    'contributor_desc' => $_REQUEST['contributor_desc']
                                )
                            );
                        }

                        // Store the information in a cookie so user won't have to re-login:
                        cookie_var_store("contributor_username",         $contributor_email);
                        cookie_var_store("contributor_password_hash", md5($contributor_password));

                        $contributor_authenticated = true;
                    }
                    else {
                        if ($login_required) {
                            print_site_page('print_not_an_existing_account', 3);

                            exit;
                        }
                    }
                }
                else {
                    // The contributor_email does not exist, nor is it a valid email address.
                    if ($login_required) {
                        print_site_page('print_not_an_email_login_form', 3);

                        exit;
                    }
                }
            }
        }

        if ($contributor_authenticated) {
            // Store the contributor id globally so the including script can use it:
            $contributor_id = contributor_get_id_from_contributor_email($contributor_email);

            // Stuff all the contributor fields into global variables:
            gather_array_into_globals(contributor_get_contributor_by_id($contributor_id));
        }

        return $contributor_authenticated;
    }

?>