<?php

    // Called from Javascript for AJAX stuff

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/contrib-utils.php");
    require_once("include/web-utils.php");
    require_once("include/sys-utils.php");

    if (isset($_REQUEST['contributor_email']) && isset($_REQUEST['contributor_password'])) {
        $contributor_email    = $_REQUEST['contributor_email'];
        $contributor_password = $_REQUEST['contributor_password'];
        
        if ($contributor = contributor_get_contributor_by_username($contributor_email)) {
            if ($contributor_password == $contributor['contributor_password']) {
                print <<<EOT
                    <span class="validation-success">password is correct</span>
EOT;
            }
            else {
                print <<<EOT
                    <span class="validation-failure"><a href="javascript:void;" onclick="javascript:on_remind_me();">remind me</a></span>
EOT;
            }
        }
        else {
            print <<<EOT
                <span class="validation-success">unknown</span>
EOT;
        }
    }
?>