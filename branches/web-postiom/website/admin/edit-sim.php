<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class EditSimPage extends SitePage {

    function print_category_checkbox($cat_id, $cat_name, $cat_is_visible) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $sim_id = $_REQUEST['sim_id'];

        $sql_cat        = "SELECT * FROM `simulation_listing` WHERE `sim_id`= '$sim_id' AND `cat_id`='$cat_id' ";
        $sql_result_cat = mysql_query($sql_cat, $connection);
        $row_cat        = mysql_num_rows($sql_result_cat);

        $is_checked = ($row_cat >= 1 ? 'checked="checked"' : "");

        // HACK: Make sure "All Sims" category is checked if this is a new simulation
        $default_checked_for_new_sim = array('All Sims', 'New Sims', 'Show Static Preview on Homepage');
        if (in_array($cat_name, $default_checked_for_new_sim)) {
            $simulation = SimFactory::inst()->getById($sim_id);
            if ($simulation->getWrapped()->getName() == DEFAULT_NEW_SIMULATION_NAME) {
                $is_checked = 'checked="checked"';
            }
        }

        $formatted_cat_name = WebUtils::inst()->toHtml($cat_name);
        if ($cat_is_visible == '1') {
            print "<input type=\"checkbox\" name=\"checkbox_cat_id_$cat_id\" value=\"true\" $is_checked />$formatted_cat_name<br/>";
        }
        else {
            print "<input type=\"checkbox\" name=\"checkbox_cat_id_$cat_id\" value=\"true\" $is_checked /><strong>$formatted_cat_name</strong><br/>";
        }
    }

    function print_category_checkboxes() {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $select_categories_st = "SELECT * FROM `category` ORDER BY `cat_order` ASC ";
        $category_rows        = mysql_query($select_categories_st, $connection);

        while ($category_row=mysql_fetch_assoc($category_rows)) {
            $cat_id         = $category_row['cat_id'];
            $cat_name       = $category_row['cat_name'];
            $cat_is_visible = $category_row['cat_is_visible'];

            $this->print_category_checkbox($cat_id, $cat_name, $cat_is_visible);
        }
    }

    function print_teachers_guide($sim) {
        print "<p  style=\"font-size: large;\">\n";
        print "<strong>Teachers Guide options:</strong><br />";
        if ($sim->hasTeachersGuide()) {
            print "This sim has no teacher's guide<br />\n";
        }
        else {
            print "Current teacher's guide: <em><a href=\"{$sim->getTeachersGuideUrl()}\">{$sim->getTeachersGuideFilename()}</a></em><br />\n";

        }
        $options = array(
            'no_change' => 'Keep as is',
            'remove' => 'Remove the guide',
            'upload' => 'Upload a new guide: '.WebUtils::inst()->buildFileInput('sim_teachers_guide_file_upload')
            );
        print WebUtils::inst()->buildVerticalRadioButtonInput('radio_teachers_guide_action', $options, 'no_change');
        print "</p>\n";
        // TODO: add ability to JavaScript in radion button inputs
        /*
        // JS nicety: if the user uploads a file, automatically select the "upload" radio button
        print "<input name=\"sim_teachers_guide_file_upload\" type=\"file\" id=\"rtga4\" ".
            "onclick=\"if (document.getElementById('rtga4').value != '') { document.getElementById('rtga3').checked = true;}\" />";
        */
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (!isset($_REQUEST['sim_id'])) {
            print "<h2>No Simulation Found</h2><p>Be sure to specify a simulation id.</p>";
            return;
        }

        try {
            $sim = SimFactory::inst()->getById($_REQUEST['sim_id']);
        }
        catch (PhetSimException $e) {
            print "<h2>No Simulation Found</h2><p>There is no simulation with the specified id.</p>";
            return;
        }

        print <<<EOT
            <form enctype="multipart/form-data" action="update-sim.php" method="post">
                <p>
                    <input type="hidden" name="sim_id" value="{$sim->getId()}" />
                </p>

EOT;

        $Web = WebUtils::inst();
        print "<p  style=\"font-size: large;\"><strong>Specify the name of the simulation</strong></p>\n";
        print "<p>\n";
        print $Web->buildTextInput('sim_name', $sim->getName());
        print "</p>\n";

        print "<p  style=\"font-size: large;\"><strong>Specify the <em>SVN project name</em> of the simulation</strong></p>\n";
        print "<p>\n";
        print $Web->buildTextInput('sim_dirname', $sim->getProjectName());
        print "</p>\n";

        print "<p  style=\"font-size: large;\"><strong>Specify the <em>SVN simulation name</em>'</strong></p>\n";
        print "<p>\n";
        print $Web->buildTextInput('sim_flavorname', $sim->getSimName());
        print "</p>\n";

        print <<<EOT
    <div><p  style="font-size: large;"><strong>Please select a rating for this simulation</strong></p></div>
                <p>

EOT;

        $SimUtils = SimUtils::inst();
        $ar = array();
        $ar['0'] = 'None';
        $ar['1'] = $SimUtils->getRatingImageTag(SimUtils::SIM_RATING_ALPHA);
        $ar['2'] = $SimUtils->getRatingImageTag(SimUtils::SIM_RATING_CHECK);
        $check = $sim->getRating();
        print $Web->buildHorizontalRadioButtonInput('sim_rating', $ar, $check);

print <<<EOT
                </p>

    <div><p  style="font-size: large;"><strong>Please select the type of the simulation</strong></p></div>

        <p>

EOT;

 $ar = array();
 $ar['0'] = 'Java';
 $ar['1'] = 'Flash';
 $check = ($sim->getType() == 'Flash') ? '1' : '0';
 print $Web->buildHorizontalRadioButtonInput('sim_type', $ar, $check);

        print "</p>";

        print "<div><p  style=\"font-size: large;\"><strong>Please check this box of the simulation is <em>NOT</em> Standalone</strong><br />";

        print $Web->buildCheckboxInput('sim_crutch', array(1 => $SimUtils->getGuidanceImageTag().$SimUtils->getGuidanceDescription()));
        print "</p></div>";

        print "<p  style=\"font-size: large;\"><strong>Simulation Description</strong></p>\n";
        print "<p>\n";
        print $Web->buildTextAreaInput('sim_desc', $sim->getDescription(), 10);
        print "</p>\n";

        print "<p style=\"font-size: large;\"><strong>Enter the keywords to associate with the simulation***</strong><br />\n";
        print <<<EOT
<span style="font-size: x-small; color: grey;"><strong>***</strong><em>Separated by commas or asterisks. Asterisk separation has precedence over comma separation.  Newlines are removed.</em></span></p>
EOT;
        print "<p>\n";
        $list = join("*\n", $sim->getKeywords());
        print $Web->buildTextAreaInput('sim_keywords', $list, 5);
        print "</p>\n";

        print "<p  style=\"font-size: large;\"><strong>Enter the members of the design team***</strong><br />\n";
        print <<<EOT
<span style="font-size: x-small; color: grey;"><strong>***</strong><em>Separated by commas or asterisks. Asterisk separation has precedence over comma separation.  Newlines are removed.</em></span></p>
EOT;
        print "<p>\n";
        $list = join("*\n", $sim->getDesignTeam());
        print $Web->buildTextAreaInput('sim_design_team', $list, 5);
        print "</p>\n";


        print "<p  style=\"font-size: large;\"><strong>Enter any 3rd-party libraries used by the simulation***</strong><br />\n";
        print <<<EOT
<span style="font-size: x-small; color: grey;"><strong>***</strong><em>Separated by commas or asterisks. Asterisk separation has precedence over comma separation.  Newlines are removed.</em></span></p>
EOT;
        print "<p>\n";
        $list = join("*\n", $sim->getLibraries());
        print $Web->buildTextAreaInput('sim_libraries', $list, 5);
        print "</p>\n";

        print "<p  style=\"font-size: large;\"><strong>Enter the 'thanks to' for the simulation***</strong><br />\n";
        print <<<EOT
<span style="font-size: x-small; color: grey;"><strong>***</strong><em>Separated by commas or asterisks. Asterisk separation has precedence over comma separation.  Newlines are removed.</em></span></p>
EOT;
        print "<p>\n";
        $list = join("*\n", $sim->getThanksTo());
        print $Web->buildTextAreaInput('sim_thanks_to', $list, 5);
        print "</p>\n";

        $this->print_teachers_guide($sim);

        print "<p  style=\"font-size: large;\"><strong>Enter the main topics***</strong><br />\n";
        print <<<EOT
<span style="font-size: x-small; color: grey;"><strong>***</strong><em>Separated by commas or asterisks. Asterisk separation has precedence over comma separation.  Newlines are removed.</em></span></p>
EOT;
        print "<p>\n";
        $list = join("*\n", $sim->getMainTopics());
        print $Web->buildTextAreaInput('sim_main_topics', $list, 5);
        print "</p>\n";


        print "<p  style=\"font-size: large;\"><strong>Enter the main topics***</strong><br />\n";
        print <<<EOT
<span style="font-size: x-small; color: grey;"><strong>***</strong><em>Separated by commas or asterisks. Asterisk separation has precedence over comma separation.  Newlines are removed.</em></span></p>
EOT;
        print "<p>\n";
        $list = join("*\n", $sim->getLearningGoals());
        print $Web->buildTextAreaInput('sim_sample_goals', $list, 5);
        print "</p>\n";


        print <<<EOT
            <div><p style="font-size: large;"><strong>Please select the categories you would like the Simulation to appear under:</strong><br />
            <span class="p-indentation"><em>(<strong>Boldface</strong> indicates this is a hidden category)</em></span></p></div>

EOT;
        print "<p>\n";
        $this->print_category_checkboxes();

        print <<<EOT
            </p>

            <p>
                <input type="submit" value="Update" name="submit" />
            </p>

            </form>

EOT;
    }

}

$page = new EditSimPage("Edit Simulation Parameters", NavBar::NAV_ADMIN, null, SitePage::AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>