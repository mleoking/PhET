<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class authenticationTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing encrypt_password()
     *
     */

    public function testEncryptPassword_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_clear_cookie()
     *
     */

    public function testAuthClearCookie_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_auth_error()
     *
     */

    public function testAuthAuthError_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_get_error()
     *
     */

    public function testAuthGetError_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_cookie_is_valid()
     *
     */

    public function testAuthCookieIsValid_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_complete()
     *
     */

    public function testAuthComplete_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_user_validated()
     *
     */

    public function testAuthUserValidated_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_get_username()
     *
     */

    public function testAuthGetUsername_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auth_do_validation()
     *
     */

    public function testAuthDoValidation_testCase() {
        $this->markTestIncomplete();
    }

}

?>
