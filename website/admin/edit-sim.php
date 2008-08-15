<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

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

        $is_checked = ($row_cat >= 1 ? "checked=\"checked\"" : "");

        $formatted_cat_name = format_string_for_html($cat_name);
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

    function print_rating_checkbox($rating, $selected) {
        global $SIM_RATING_TO_IMAGE_HTML;

        $check_status = generate_check_status($rating, $selected);

        // Special case, have a "none" type with no image
        if ($rating == SIM_RATING_NONE) {
            print "<input name=\"sim_rating\" type=\"radio\" value=\"$rating\" $check_status /> None";
            return;
        }

        $image_html   = $SIM_RATING_TO_IMAGE_HTML[$rating];

        print "<input name=\"sim_rating\" type=\"radio\" value=\"$rating\" $check_status /> $image_html";
    }

    function print_type_checkbox($type, $selected) {
        global $SIM_TYPE_TO_IMAGE_HTML;

        $image_html   = $SIM_TYPE_TO_IMAGE_HTML[$type];
        $check_status = generate_check_status($type, $selected);

        print "<input name=\"sim_type\" type=\"radio\" value=\"$type\" $check_status /> $image_html";
    }

    function print_teachers_guide($sim_id) {
        $teachers_guide = sim_get_teachers_guide_by_sim_id($sim_id);
        print "<p>\n";
        print "Teachers Guide options:<br />";
        if ($teachers_guide) {
            print "Current teacher's guide: <em><a href=\"{$this->prefix}admin/get-teachers-guide.php?teachers_guide_id={$teachers_guide["teachers_guide_id"]}\">{$teachers_guide["teachers_guide_filename"]}</a></em><br />\n";
        }
        else {
            print "This sim has no teacher's guide<br />\n";
        }
        print "<input type=\"radio\" name=\"radio_teachers_guide_action\" value=\"no_change\" id=\"rtga1\" checked=\"checked\" />Keep as is<br />\n";
        print "<input type=\"radio\" name=\"radio_teachers_guide_action\" value=\"remove\" id=\"rtga2\" />Remove the guide<br />\n";
        print "<input type=\"radio\" name=\"radio_teachers_guide_action\" value=\"upload\" id=\"rtga3\" />Upload a new guide: \n";
        // JS nicety: if the user uploads a file, automatically select the "upload" radio button
        print "<input name=\"sim_teachers_guide_file_upload\" type=\"file\" id=\"rtga4\" ".
            "onclick=\"if (document.getElementById('rtga4').value != '') { document.getElementById('rtga3').checked = true;}\" />";
        print "</p>\n";
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (isset($_REQUEST['sim_id'])) {
            $simulation = sim_get_sim_by_id($_REQUEST['sim_id']);
        }

        if (!isset($simulation) || !$simulation) {
            print "<h2>No Simulation Found</h2><p>There is no simulation with the specified id.</p>";
            return;
        }

        // Removing unsafe function 'get_code_to_create_variables_from_array',
        // just doing the equivalent by hand
        //eval(get_code_to_create_variables_from_array($simulation));
        $sim_id = $simulation["sim_id"];
        $sim_name = $simulation["sim_name"];
        $sim_dirname = $simulation["sim_dirname"];
        $sim_flavorname = $simulation["sim_flavorname"];
        $sim_rating = $simulation["sim_rating"];
        $sim_no_mac = $simulation["sim_no_mac"];
        $sim_crutch = $simulation["sim_crutch"];
        $sim_type = $simulation["sim_type"];
        $sim_size = $simulation["sim_size"];
        $sim_launch_url = $simulation["sim_launch_url"];
        $sim_image_url = $simulation["sim_image_url"];
        $sim_desc = $simulation["sim_desc"];
        $sim_keywords = $simulation["sim_keywords"];
        $sim_system_req = $simulation["sim_system_req"];
        $sim_teachers_guide_id = $simulation["sim_teachers_guide_id"];
        $sim_main_topics = $simulation["sim_main_topics"];
        $sim_design_team = $simulation["sim_design_team"];
        $sim_libraries = $simulation["sim_libraries"];
        $sim_thanks_to = $simulation["sim_thanks_to"];
        $sim_sample_goals = $simulation["sim_sample_goals"];
        $sim_sorting_name = $simulation["sim_sorting_name"];
        $sim_animated_image_url = $simulation["sim_animated_image_url"];
        $sim_is_real = $simulation["sim_is_real"];

        print <<<EOT
            <form enctype="multipart/form-data" action="update-sim.php" method="post">
                <p>
                    <input type="hidden" name="sim_id" value="$sim_id" />
                </p>

EOT;

        print_captioned_editable_area("Specify the name of the simulation", "sim_name", $sim_name, "1");

        print_captioned_editable_area("Specify the <em>dir-name</em> of the simulation", "sim_dirname", $sim_dirname, "1");

        print_captioned_editable_area("Specify the <em>flavor-name</em> of the simulation", "sim_flavorname", $sim_flavorname,      "1");

        print <<<EOT
    <div>Please select a rating for this simulation</div>
                <p>

EOT;

        $this->print_rating_checkbox(SIM_RATING_NONE,         $sim_rating);
        $this->print_rating_checkbox(SIM_RATING_ALPHA,         $sim_rating);
        $this->print_rating_checkbox(SIM_RATING_CHECK,         $sim_rating);

print <<<EOT
                </p>

    <div>Please select the type of the simulation</div>

        <p>

EOT;

        $this->print_type_checkbox(SIM_TYPE_JAVA,  $sim_type);
        $this->print_type_checkbox(SIM_TYPE_FLASH, $sim_type);

        print "</p>";

        print "<div>";

        print_checkbox(
            "sim_crutch",
            "<img src=\"".SIM_CRUTCH_IMAGE."\" alt=\"\" />".
            "Check here if the simulation is <strong>not</strong> standalone",
            $sim_crutch
        );

        print "</div>";

        print_captioned_editable_area("Simulation Description", "sim_desc", $sim_desc, "10");
        print_captioned_editable_area("Enter the keywords to associated with the simulation*",
                                      "sim_keywords", $sim_keywords, "2");
        print_captioned_editable_area("Enter the members of the design team*",               "sim_design_team",     $sim_design_team, "4");
        print_captioned_editable_area("Enter the libraries used by the simulation*",         "sim_libraries",       $sim_libraries,   "4");
        print_captioned_editable_area("Enter the 'thanks to' for the simulation*",           "sim_thanks_to",       $sim_thanks_to,   "4");

        $this->print_teachers_guide($sim_id);

        print_captioned_editable_area("Enter the main topics*",                              "sim_main_topics",     $sim_main_topics, "4");

        print_captioned_editable_area("Enter the sample learning goals*",                    "sim_sample_goals",    $sim_sample_goals,"6");

        print("<div>Please select the categories you would like the Simulation to appear under:</div>");
        print "<p>\n";
        $this->print_category_checkboxes();

        print <<<EOT
            </p>

            <p>
                <input type="submit" value="Update" name="submit" />
            </p>

            </form>

            <div>*Separated by commas or asterisks. Asterisk separation has precedence over comma separation.</div>
EOT;
    }

}

$page = new EditSimPage("Edit Simulation Parameters", NAV_ADMIN, null, AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>