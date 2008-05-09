<?php

    // Called from Javascript for AJAX stuff

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");

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