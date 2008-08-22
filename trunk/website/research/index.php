<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/research-utils.php");

class ResearchPage extends SitePage {

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

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (isset($_REQUEST['action'])) {
            //sdo_authentication(true);

            $this->action = $_REQUEST['action'];

            $this->handle_action($this->action);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $site_root = SITE_ROOT;

        print <<<EOT
            <p>We direct our research at assessing the effectiveness of our interactive simulations in a variety of educational environments, particularly physics courses, and as stand-alone, informal educational tools.</p>

            <p>In addition, we are focusing our efforts to better understand how students learn from the simulations, and what implications this may have for designing effective in-class activities, homework and labs.  Please see our <a href="{$site_root}phet-dist/publications/PhET Look and Feel.pdf">PhET Look and Feel</a> guide.</p>

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

EOT;

            $can_edit = false;

            if ($this->authentication_level >= AUTHLEVEL_TEAM) {
                $can_edit = true;
            }

            foreach(research_get_all_categories() as $category) {
                print "<h3>$category</h3>";

                print "<ul class=\"content-points\">";

                foreach(research_get_all_by_category($category) as $research) {
                    print "<li>";

                    $research_id = $research['research_id'];

                    research_print($research_id);

                    if ($can_edit) {
                        print '(<a href="index.php?action=edit&amp;research_id='.$research_id.'#update-edit-form">edit</a>, <a href="index.php?action=delete&amp;research_id='.$research_id.'">delete</a>)';
                    }

                    print "</li>";
                }

                print "</ul>";
            }

            if ($can_edit) {
                if (isset($this->action) && $this->action == 'edit') {
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
                    <form id="update-edit-form" method="post" action="index.php">
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

}

$page = new ResearchPage("Research", NAV_RESEARCH, null, AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>
