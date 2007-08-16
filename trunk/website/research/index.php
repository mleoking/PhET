<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/authentication.php");    
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/research-utils.php");
    
    function handle_action($action) {
        if ($action == 'new') {
            research_add_from_script_params();
        }
        else if ($action == 'update') {
            research_update_from_script_params();
        }
        else if ($action == 'delete') {
            research_delete($_REQUEST['research_id']);
        }
    }
    
    function print_content() {
        ?>
            <h1>Research</h1>

            <p>We direct our research at assessing the effectiveness of our interactive simulations in a variety of educational environments, particularly physics courses, and as stand-alone, informal educational tools.</p>

			<p>In addition, we are focusing our efforts to better understand how students learn from the simulations, and what implications this may have for designing effective in-class activities, homework and labs.</p>

            <h2>Our immediate interests are:</h2><br />

            <h3>How the simulations affect the students'...</h3>

            <ul class="content-points">
                <li>ability to solve conceptual and quantitative problems;</li>

                <li>attitudes about learning physics; and</li>

                <li>perceptions of their own learning and of the simulations themselves.</li>
            </ul>

            <h3>Simulation effectiveness in different environments</h3>

            <ul class="content-points">
                <li>How might these simulations be used in labs? Can they replace traditional hands-on experiments?</li>

                <li>When students use simulations as preparation activities for class, how do they compare to traditional class preparation activities such as reading the text or doing homework problems?</li>

                <li>Are the simulations more effective standalone (as an open play area) or wrapped with a guiding tutorial?</li>
            </ul>

            <h2>Publications and Presentations</h2>
            
            <?php
            
            $can_edit = false;
            
            if (isset($GLOBALS['contributor_authenticated']) && $GLOBALS['contributor_authenticated'] == true) {
                if ($GLOBALS['contributor_is_team_member']) {
                    $can_edit = true;
                }
            }
            
            foreach(research_get_all_categories() as $category) {
                print "<h3>$category</h3>";
                
                print "<ul class=\"content-points\">";
                
                foreach(research_get_all_by_category($category) as $research) {
                    print "<li>";
                    
                    $research_id = $research['research_id'];
                    
                    research_print($research_id);
                    
                    if ($can_edit) {
                        print '(<a href="index.php?action=edit&amp;research_id='.$research_id.'">edit</a>, <a href="index.php?action=delete&amp;research_id='.$research_id.'">delete</a>)';
                    }
                    
                    print "</li>";
                }
                
                print "</ul>";
            }
            
            global $action;
            
            if ($can_edit) {                
                if (isset($action) && $action == 'edit') {
                    $legend         = "Update Research Item";
                    $op_desc        = 'edit an existing research item or <a href="index.php">add</a> a new item';
                    $research_id    = $_REQUEST['research_id'];
                    $button_caption = "Update";                    
                    $action_html    = '<input type="hidden" name="action" value="update" />';
                }
                else {
                    $legend         = "Add Research Item";
                    $op_desc        = "add a new research item";
                    $research_id    = null;
                    $button_caption = "Add";
                    $action_html    = '<input type="hidden" name="action" value="new" />';
                }
                
                print <<<EOT
                    <form method="post" action="index.php">
                        <fieldset>
                            <legend>$legend</legend>
                            
                            <p>As a PhET team member, you may use this form to $op_desc.</p>
EOT;

                research_print_edit($research_id);

                print <<<EOT
                            $action_html
                        
                            <input type="submit" name="submit" value="$button_caption" />
                        </fieldset>
                    </form>                        
EOT;
            }
    }

    if (isset($_REQUEST['action'])) {
        do_authentication(true);
        
        $action = $_REQUEST['action'];
        
        handle_action($action);
    }
    
    print_site_page('print_content', 7);
?>
