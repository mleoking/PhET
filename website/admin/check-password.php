<?php
    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");

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