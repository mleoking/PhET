<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."teacher_ideas/user-login.php");  

    include_once(SITE_ROOT."admin/web-utils.php");       
    include_once(SITE_ROOT."admin/contrib-utils.php");   
    include_once(SITE_ROOT."teacher_ideas/referrer.php");
    
    if (isset($_REQUEST['contribution_id']) && isset($contributor_id) && isset($_REQUEST['contribution_comment_text'])) {
        $contribution_id = $_REQUEST['contribution_id'];
        $contribution_comment_text = $_REQUEST['contribution_comment_text'];
    
        contribution_add_comment($contribution_id, $contributor_id, $contribution_comment_text);
    }
    
    force_redirect($referrer, 0);
?>