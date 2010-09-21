<?php

    // Called from Javascript for AJAX stuff

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/contrib-utils.php");
    require_once("include/web-utils.php");
    require_once("include/sys-utils.php");

    if (isset($_REQUEST['contributor_email'])) {
        $contributor_email = $_REQUEST['contributor_email'];

        if ($contributor = contributor_get_contributor_by_username($contributor_email)) {
            print <<<EOT
                <span class="validation-success" title="This is a valid, existing PhET account.">existing account</span>

EOT;
        }
        else {
            print <<<EOT
                <span class="validation-success" title="This email is not recognized. A new PhET account will be created.">new account</span>

EOT;
        }
    }
    else {
        print "This script must be invoked with contributor_email as an argument.";
    }
?>