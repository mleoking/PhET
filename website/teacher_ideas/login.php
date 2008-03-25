<?php

/*
    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/authentication.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
*/

include_once("../admin/BasePage.php");

class LoginPage extends BasePage {

    function render_content() {
        print <<<EOT
            <h1>Login</h1>

            <form method="post" action="../teacher_ideas/user-edit-profile.php">
                <fieldset>
                    <legend>Your Account</legend>

                    <p>Please enter your name:</p>

                    <div class="p-indentation">

EOT;

                        contributor_print_quick_login();

        print <<<EOT

                    </div>

                </fieldset>
            </form>

EOT;
    }

}

auth_do_validation();
$page = new LoginPage(3, get_referrer(), "Login Page");
$page->update();
$page->render();

?>