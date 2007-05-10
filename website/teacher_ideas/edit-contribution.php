<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");    

    function print_content() {
        global $referrer;
        ?>
        
        <h1>Edit Contribution</h1>
        
        <p>TODO: This is where the user will be presented with all contribution fields.</p>
        
        <?php
        
        print "<p><a href=\"$referrer\">continue</a></p>";
    }
    
    if (isset($_REQUEST['referrer'])) {
        $referrer = $_REQUEST['referrer'];
    }
    else {
        $referrer = SITE_ROOT.'teacher_ideas/manage-contributions.php';
    }    
    
    print_site_page('print_content', 3);

?>