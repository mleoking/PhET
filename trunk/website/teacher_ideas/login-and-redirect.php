<?php

include_once("../admin/BasePage.php");

class LoginAndRedirectPage extends BasePage {

    function render_content() {
        print_first_time_login_form();
    }
}

$result = auth_do_validation();
if ($result) {
    force_redirect($_REQUEST['url'], 0);
}
else {
    $page = new LoginAndRedirectPage(-1, $_REQUEST['url'], "Login And Redirect");
    $page->update();
    $page->render();
}

?>