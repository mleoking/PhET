<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."teacher_ideas/user-login.php");  
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."teacher_ideas/referrer.php");  
    
    function handle_action($action) {
        eval(get_code_to_create_variables_from_array($_REQUEST));
        
        
    }

    function print_content() {
        global $referrer, $contribution_id;
        
        ?>
        
        <h1>Edit Contribution</h1>
        
        <?php
        
        contribution_print_full_edit_form($contribution_id, "edit-contribution.php", $referrer);        
        
        print "<p><a href=\"$referrer\">cancel</a></p>";
    }
    
    $contribution_id = $_REQUEST['contribution_id'];
    
    if (isset($_REQUEST['action'])) {
        handle_action($_REQUEST['action']);
    }
    
    print_site_page('print_content', 3);

?>