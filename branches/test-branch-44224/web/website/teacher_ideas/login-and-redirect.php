<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class LoginAndRedirectPage extends SitePage {

    function get_login_panel() {
        // This is the login page, don't provide the login panel
        return '';
    }

    function update() {
        if ($this->authentication_level >= SitePage::AUTHLEVEL_USER) {            
            if (!isset($_REQUEST['url'])) {
                $redirect_url = $this->prefix;
            }
            else {
                $redirect_url = $_REQUEST['url'];
            }
            $this->header_redirect($redirect_url);
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

$page = new LoginAndRedirectPage("PhET Login", NavBar::NAV_NOT_SPECIFIED, null, SitePage::AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>