<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class SimulationsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        if (isset($_REQUEST['cat'])) {
            $cat_encoding = $_REQUEST['cat'];

            $cat_id = sim_get_cat_id_by_cat_encoding($cat_encoding);
        }
        else {
            $cat_encoding = 'Top_Simulations';
            $cat_id       = 1;
        }

        if (is_null($cat_id)) {
            $cat_encoding = 'Top_Simulations';
            $cat_id       = 1;
        }

        $select_category_st = "SELECT * FROM `category` WHERE `cat_id`='$cat_id'";
        $category_rows      = mysql_query($select_category_st, $connection);

        // Print the category header -- e.g. 'Top Sims':
        if (!$category_rows) {
            print "<h2>Invalid Category</h2>";
        }
        else {
            $category_row = mysql_fetch_assoc($category_rows);

            $cat_name = $category_row['cat_name'];

            print "<h2>$cat_name</h2>";
        }

        $sim_limit = SIMS_PER_PAGE;

        if (isset($_REQUEST['st'])) {
            $sim_start_number = $_REQUEST['st'];

            if ($sim_start_number == -1) {
                $sim_start_number = 0;
                $sim_limit        = 999;
            }
        }
        else {
            $sim_start_number = 0;
            $sim_limit        = 999;
        }

        if (isset($_REQUEST['view_type'])) {
            $view_type = $_REQUEST['view_type'];
        }
        else {
            if ($cat_encoding == "All_Sims") {
                $view_type = "index";
            }
            else {
                $view_type = "thumbs";
            }
        }

        // This statement selects for all sims in the category, and orders by the sim sorting name:
        $simulations = ($view_type == 'thumbs') ? sim_get_sims_by_cat_id($cat_id) : sim_get_sims_by_cat_id_alphabetically($cat_id);

        $num_sims_in_category = count($simulations);

        if ($view_type == "thumbs") {
            $link = sim_get_category_link_by_cat_id($cat_id, 'Index View', '&amp;view_type=index');

            // THUMBNAIL INDEX
            print "<div id=\"listing_type\">$link</div>";

            $pages_html = '';

            if ($num_sims_in_category > SIMS_PER_PAGE) {
                $current_page = $sim_start_number / SIMS_PER_PAGE + 1;

                // Don't bother printing this section unless there are more sims than will fit on one page:
                $pages_html .= "<div id=\"pg\">\n";

                if ($sim_limit == 999) {
                    $link = "View All";
                }
                else {
                    $link = sim_get_category_link_by_cat_id($cat_id, "View All", "&amp;st=-1", 'pg');
                }

                $pages_html .= "$link | ";

                $num_pages = (int)ceil((float)$num_sims_in_category / (float)SIMS_PER_PAGE);

                for ($n = 0; $n < $num_pages; $n = $n + 1) {
                    $page_number = $n + 1;

                    $page_sim_start_number = SIMS_PER_PAGE * $n;

                    if ($page_number == $current_page && $sim_limit != 999) {
                        $link = "$page_number";
                    }
                    else {
                        $link = sim_get_category_link_by_cat_id($cat_id, "$page_number", "&amp;st=$page_sim_start_number", 'pg');
                    }

                    $pages_html .=  "$link\n";
                }

                $pages_html .=  "</div>\n";
            }

            print $pages_html;

            //--------------------------------------------------

            print '<div class="full-width"></div>';

            // Setting the style to display: inline fixes an IE6 double-margin bug
            // Source: http://www.positioniseverything.net/explorer/floatIndent.html
            print '<div class="productList" style="display: inline;">';

            $sim_number   = -1;
            $sims_printed = 0;

            foreach($simulations as $simulation) {
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

                $formatted_sim_name = format_string_for_html($sim_name);

                // Make sure the simulation is valid:
                if (is_numeric($sim_id)) {
                    ++$sim_number;

                    if ($sim_number <  $sim_start_number) continue;
                    if ($sim_number >= $sim_start_number + $sim_limit) break;

                    print "<div class=\"productEntry\">\n";

                    $sim_url = sim_get_url_to_sim_page($sim_id);

                    $link_to_sim = "<a href=\"$sim_url\">";


                    $sim_thumbnail_link = sim_get_thumbnail($simulation);

                    print <<<EOT
                        <a href="$sim_url">
                            <img src="$sim_thumbnail_link"
                                 width="130"
                                 alt="Screenshot of $formatted_sim_name Simulation"
                                 title="Clear here to view the $formatted_sim_name simulation"
                             />
                        </a>

EOT;

                    print "<p>$link_to_sim$formatted_sim_name</a></p>\n";

                    // Close product:
                    print "</div>\n";

                    ++$sims_printed;
                }
            }

            print "</div>"; // Close product list

            if ($sims_printed == 9) {
                print $pages_html;
            }
        }
        else {
            $link = sim_get_category_link_by_cat_id($cat_id, "Thumbnail View", '&amp;view_type=thumbs');

            print "<div id=\"listing_type\">$link</a></div>";

            // ALPHABETICAL INDEX

            print "<div id=\"pg\">\n";

            $last_printed_char = '';

            foreach($simulations as $simulation) {
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

                $sim_sorting_name = get_sorting_name($sim_name);

                $cur_char = strtoupper($sim_sorting_name[0]);

                if ($cur_char !== $last_printed_char) {
                    print "<a class=\"pg\" href=\"#$cur_char\">$cur_char</a> ";

                    $last_printed_char = $cur_char;
                }
            }

            print "</div>\n";

            print '<div class="full-width"></div>';

            print "<div class=\"productList\">";

            $last_printed_char = '';

            foreach($simulations as $simulation) {
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

                $sim_sorting_name = get_sorting_name($sim_name);

                $cur_char = strtoupper($sim_sorting_name[0]);

                if ($cur_char !== $last_printed_char) {
                    print "<h3 id=\"$cur_char\">$cur_char</h3>";

                    $last_printed_char = $cur_char;
                }

                $sim_url = sim_get_url_to_sim_page($sim_id);

                print "<a href=\"$sim_url\">$sim_name</a><br />";
            }

            print "</div>";  // Close product list
        }

        print <<<EOT
            <div class="full-width">
                <div class="rage_button_218928">
                    <a href="{$this->prefix}teacher_ideas/browse.php?cat=$cat_encoding">Related Activities &amp; Ideas</a>
                </div>
            </div>

EOT;
    }

}

$page = new SimulationsPage("Simulations", NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>