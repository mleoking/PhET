<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");      

    $lookup_by_email = false;
    $lookup_by_name  = false;

    if (isset($_REQUEST['contributor_email'])) {
        $contributor_email = $_REQUEST['contributor_email'];
        
        $contributor = contributor_get_contributor_by_email($contributor_email);
        
        $lookup_by_email = true;
    }
    else if (isset($_REQUEST['contributor_name'])) {
        $contributor_name = $_REQUEST['contributor_name'];
        
        $contributor = contributor_get_contributor_by_name($contributor_name);
        
        $lookup_by_name = true;
    }
    
    if (isset($contributor) && $contributor) {
        $contributor_name  = $contributor['contributor_name'];
        $contributor_email = $contributor['contributor_email'];
        
        if ($lookup_by_name) {
            $matches = array();
            
            if (preg_match('/([^@]+)(@.+)$/i', $contributor_email, $matches) == 1) {
                $email_username = $matches[1]; 
                $email_domain   = $matches[2];           
            
                $disguised_email_username = preg_replace('/(?<=.)./i', '?', $email_username);
            
                $contributor_email = $disguised_email_username.$email_domain;
            }
        }
        
        print <<<EOT
            <div class="field">
                <span class="label">your email</span>
                <span class="label_content">
                    <input type="text" name="contributor_email" id="contributor_email_uid" value="$contributor_email" onchange="javascript:on_email_entered();" onkeyup="javascript:on_email_change();"/>

                    <span id="ajax_email_comment_uid">
                    </span>
                </span>
            </div>

            <div class="field">
                <span class="label">your password</span>
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
                <span class="label">your email</span>
                <span class="label_content">
                    <input type="text" name="contributor_email" id="contributor_email_uid" onchange="javascript:on_email_entered();" onkeyup="javascript:on_email_change();" value="$contributor_email"/>
                    
                    <span id="ajax_email_comment_uid">
                    </span>
                </span>
            </div>
            
            <div class="field">
                <span class="label">choose password</span>
                <span class="label_content">
                    <input type="password" name="contributor_password" id="contributor_password_uid" />
                    
                    <span id="ajax_password_comment_uid">
                    </span>
                </span>
            </div>
            
            <div class="field">
                <span class="label">your organization</span>
                <span class="label_content">
                    <input type="text" name="contributor_organization" id="contributor_organization_uid" 
                        onkeyup="javascript:on_contributor_organization_change();" />
                </span>
            </div>                
EOT;
    }

?>