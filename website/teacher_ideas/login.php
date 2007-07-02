<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/authentication.php");    
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");    
    
    function print_quick_login_form() {        
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
    
    do_authentication(false);
    
    if ($contributor_authenticated) {    
        force_redirect(SITE_ROOT.'teacher_ideas/user-edit-profile.php', 0);
    }
    else {
        print_site_page('print_quick_login_form', 3);
    }
?>