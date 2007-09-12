<?php

    include_once("../admin/authentication.php");
    include_once("../admin/site-utils.php");
    include_once("../admin/contrib-utils.php");
    
    do_authentication(false);
    
    function print_content() {
        ?>
            <h1>Submit an Activity</h1>

        <?php

		if (!$GLOBALS['contributor_authenticated']) {
			print "<p><strong>You must have a PhET account and be logged in to proceed.</strong></p>";
		}
        
        contribution_print_full_edit_form(-1, '../teacher_ideas/edit-contribution.php', '../teacher_ideas/edit-contribution.php', 'Submit');
    }

    print_site_page('print_content', 3);

?>

