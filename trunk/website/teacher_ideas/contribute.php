<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Contribute</h1>

        <?php
        
        contribution_print_full_edit_form(-1, '../teacher_ideas/edit-contribution.php', '../teacher_ideas/edit-contribution.php', 'Contribute');
    }

    print_site_page('print_content', 3);

?>

