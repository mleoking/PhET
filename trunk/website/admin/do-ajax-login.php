<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");      

    if (isset($_REQUEST['contributor_name'])) {
        $contributor_name = $_REQUEST['contributor_name'];
        
        if ($contributor = contributor_get_contributor_by_name($contributor_name)) {
            $contributor_email = $contributor['contributor_email'];
            
            print <<<EOT
                <div class="field">
                    <span class="label">Email:</span>
                    <span class="label_content">
                        <input type="text" name="contributor_email" id="contributor_email_uid" value="$contributor_email" onchange="javascript:on_email_change();" onkeyup="javascript:on_email_change();"/>
                        
                        <span id="ajax_email_comment_uid">
                        </span>
                    </span>
                </div>

                <div class="field">
                    <span class="label">Password:</span>
                    <span class="label_content">
                        <input type="password" name="contributor_password" id="contributor_password_uid" onchange="javascript:on_password_change();" onkeyup="javascript:on_password_change();"/>
                        
                        <span id="ajax_password_comment_uid">
                        </span>
                    </span>
                </div>
EOT;
        }
        else {
            print <<<EOT
                <div class="field">
                    <span class="label">Email:</span>
                    <span class="label_content">
                        <input type="text" name="contributor_email" id="contributor_email_uid" onchange="javascript:on_email_change();"/>
                        
                        <span id="ajax_email_comment_uid">
                        </span>
                    </span>
                </div>
                
                <div class="field">
                    <span class="label">Desired Password:</span>
                    <span class="label_content">
                        <input type="password" name="contributor_password" id="contributor_password_uid" />
                        
                        <span id="ajax_password_comment_uid">
                        </span>
                    </span>
                </div>
                
                <div class="field">
                    <span class="label">Organization:</span>
                    <span class="label_content">
                        <input type="text" name="contributor_organization" id="contributor_organization_uid" />
                    </span>
                </div>                
EOT;
        }
    }

?>