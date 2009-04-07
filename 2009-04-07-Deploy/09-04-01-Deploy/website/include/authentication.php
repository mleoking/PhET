<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/web-utils.php");
    require_once("include/contrib-utils.php");

    // Salt should go at the end
    define("PASSWORD_SALT", "_phetx1225");

    // File local variables
    $authentication_complete = false;
    $authentication_result = null;
    $authentication_error = null;

    $script = get_self_url();

    $title = isset($GLOBALS['custom_title']) ? urldecode($GLOBALS['custom_title']) : "Login/Register";
    $body  = isset($GLOBALS['custom_body'])  ? urldecode($GLOBALS['custom_body'])  : "<p>Please login with your existing PhET account, or create a new one.</p>";

    function encrypt_password($plaintext_password) {
        return md5($plaintext_password.PASSWORD_SALT);
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
                    $authentication_error = "Account creation failed, that email already exists";
                    $authentication_complete = true;
                    $authentication_result = false;
                    return $authentication_result;
                }

                // Encrypt the password
                $encrypted_password = encrypt_password($_REQUEST["contributor_password1"]);

                // Create new user account:
                $contributor_id = contributor_add_new_contributor($_REQUEST["contributor_email"], $encrypted_password);

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
                $encrypted_password = encrypt_password($_REQUEST['contributor_password']);
            }
            else if (isset($_REQUEST['contributor_password1'])) {
                $encrypted_password = encrypt_password($_REQUEST['contributor_password1']);
            }

            $valid = contributor_is_valid_login($email, $encrypted_password);
            if ($valid === false) {
                $authentication_error = "Login failed, email and/or password is incorrect";
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

?>