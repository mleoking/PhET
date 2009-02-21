<?php

    // Used in Java Scripts for AJAX stuff

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/contrib-utils.php");

    if (isset($_REQUEST['contributor_email'])) {
        $contributor_email = $_REQUEST['contributor_email'];

        contributor_send_password_reminder($contributor_email);

        print <<<EOT
            <span class="validation-success">check your email</span>

EOT;
    }

?>