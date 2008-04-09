<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class LoginAndRedirectPage extends SitePage {

    function update() {
        if ($this->authentication_level >= SP_AUTHLEVEL_USER) {
            $this->header_redirect($_REQUEST['url']);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print_login_and_new_account_form($_SERVER['REQUEST_URI'], $_SERVER['REQUEST_URI'], $this->referrer);
    }

}


if (isset($_REQUEST["url"]) && (strlen($_REQUEST["url"]) > 0)) {
    $redirect_url = $_REQUEST["url"];
}
else {
    $redirect_url = "../index.php";
}

$page = new LoginAndRedirectPage("Login", NAV_NOT_SPECIFIED, $redirect_url);
$page->update();
$page->render();

?>