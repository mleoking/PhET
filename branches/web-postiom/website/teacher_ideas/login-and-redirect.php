<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class LoginAndRedirectPage extends SitePage {

    function update() {
        if ($this->authentication_level >= AUTHLEVEL_USER) {
            $this->header_redirect($_REQUEST['url']);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print_login_and_new_account_form($_SERVER['REQUEST_URI'], $_SERVER['REQUEST_URI']);
    }

}

if (isset($_REQUEST["url"]) && (strlen($_REQUEST["url"]) > 0)) {
    $redirect_url = $_REQUEST["url"];
}
else {
    $redirect_url = SITE_ROOT."index.php";
}

$page = new LoginAndRedirectPage("Login", NAV_NOT_SPECIFIED, null, AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>