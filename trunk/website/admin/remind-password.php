<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/contrib-utils.php");

    if (isset($_REQUEST['contributor_email'])) {
        $contributor_email = $_REQUEST['contributor_email'];

        contributor_send_password_reminder($contributor_email);

        print <<<EOT
            <span class="validation-success">check your email</span>

EOT;
    }

?>