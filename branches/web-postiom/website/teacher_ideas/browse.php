<?php

// The browse files, browse.php and browse-utils.php, used to be a bunch of global functions
// with lots of global variables "passed" between them.  What you see here is an first loose
// pass to decouple many functions from these globals.
//
// Further complicating the matter, a lot (but not all) functionality was called as a standalone
// page, and from sims.php which used some of the functionality, but not all.  You can still see
// some residue of this in the code.
//
// These files work, but still stand to have a lot of cleanup.


// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/browse-utils.php");

class BrowseContributionsPage extends SitePage {

    function filter_contributions($contributions, $field_name, $selected_values) {
        if (in_array('all', $selected_values)) return $contributions;

        $new_contribs = array();

        foreach($contributions as $contrib) {
            if (isset($contrib[$field_name]) && in_array($contrib[$field_name], $selected_values)) {
                $new_contribs[] = $contrib;
            }
        }

        return $new_contribs;
    }

    function build_option_string($id, $name, $selected_values) {
        $selected_status = '';

        if (in_array($id, $selected_values)) {
            $selected_status = 'selected="selected"';
        }

        $formatted_name = WebUtils::inst()->toHtml($name);
        $formatted_id = WebUtils::inst()->toHtml($id);

        return "<option value=\"{$formatted_id}\" $selected_status>{$formatted_name}</option>";
    }

    function build_association_filter_list($names, $all_filter_name, $selected_values, $size = '8') {
        $list = '';

        $on_change = 'onchange="javascript:browse_update_browser_for_select_element();"';

        $list .= '<select class="'.$all_filter_name.'" name="'.$all_filter_name.'[]" '.$on_change.' multiple="multiple" size="'.$size.'" id="'.$all_filter_name.'_uid">';

        $list .= $this->build_option_string('all', "All $all_filter_name", $selected_values);

        foreach($names as $name) {
            $list .= $this->build_option_string($name, $name, $selected_values);
        }

        $list .= '</select>';

        return $list;
    }

    function build_sim_list($selected_values) {
        $sim_names = SimUtils::inst()->getAllSimNames();

        return $this->build_association_filter_list($sim_names, "Simulations", $selected_values);
    }

    function build_level_list($selected_values) {
        $level_names = contribution_get_all_template_level_names();

        return $this->build_association_filter_list($level_names, "Levels", $selected_values);
    }

    function build_type_list($selected_values) {
        $type_names = contribution_get_all_template_type_names();

        return $this->build_association_filter_list($type_names, "Types", $selected_values);
    }

    function consolidate_identical_adjacent_titles($contributions) {
        $new = array();

        $new_last_title = null;
        $new_index = 0;

        foreach($contributions as $contrib) {
            $cur_title = $contrib['contribution_title'];

            // See if the title is the same as the last one:
            if ($cur_title == $new_last_title) {
                // Append information to last one:
                $new_last_index = $new_index - 1;

                foreach($contrib as $cur_field => $cur_value) {
                    if (!isset($new[$new_last_index][$cur_field])) {
                        $new[$new_last_index][$cur_field] = '';
                    }

                    $new_last_value = $new[$new_last_index][$cur_field];

                    if ($cur_value != '' && !strrchr($new_last_value, $cur_value)) {
                        if (strlen(trim($new[$new_last_index][$cur_field])) == 0) {
                            $new[$new_last_index][$cur_field] .= "$cur_value";
                        }
                        else {
                            $new[$new_last_index][$cur_field] .= ", $cur_value";
                        }
                    }
                }
            }
            else {
                ++$new_index;

                $new_last_title = $cur_title;

                $new[] = $contrib;
            }
        }

        return $new;
    }

    function print_content_with_header() {
        $sim_list   = $this->build_sim_list($this->Simulations);
        $type_list  = $this->build_type_list($this->Types);
        $level_list = $this->build_level_list($this->Levels);

        print <<<EOT
            <form id="browsefilter" method="post" action="browse.php">
                <div>
                    <input type="hidden" id="browse_order" name="order"    value="{$this->order}"     />
                    <input type="hidden" id="browse_sort_by" name="sort_by"  value="{$this->sort_by}"   />
                </div>

                <table>
                    <thead>
                        <tr>
                            <td class="Simulations">
                                Simulations
                            </td>

                            <td class="Types">
                                Type
                            </td>

                            <td class="Levels">
                                Level
                            </td>
                        </tr>
                    </thead>

                    <tbody>
                        <tr>
                            <td>
                                $sim_list
                            </td>

                            <td>
                                $type_list
                            </td>

                            <td>
                                $level_list
                            </td>
                        </tr>

                        <tr>
                            <td colspan="3">
                                <input type="submit" name="Filter" value="Update" />
                            </td>
                        </tr>
                    </tbody>
                </table>

                <div class="separator">
                </div>
            </form>

EOT;

        $result =
        browse_print_content_only($this->Simulations, $this->Types, $this->Levels,
                                    $this->sort_by, $this->order, $this->next_order, true);
        if (!$result) {
            print "<p>There are no contributions meeting the specified criteria.</p>";
        }

        if (isset($_REQUEST['cat'])) {
            $cat_encoding = $_REQUEST['cat'];

            print <<<EOT
                <div class="full-width">
                    <div class="rage_button_357660">
                        <a href="{$this->prefix}simulations/index.php?cat=$cat_encoding">Back to Simulations</a>
                    </div>
                </div>

EOT;
        }
    }

    function update() {
        if (isset($_REQUEST['sort_by'])) {
            $this->sort_by = $_REQUEST['sort_by'];
        }
        else {
            $this->sort_by = 'contribution_title';
        }

        if (isset($_REQUEST['order'])) {
            $this->order = strtolower($_REQUEST['order']);
        }
        else {
            $this->order = 'asc';
        }

        $this->next_order = 'asc';

        if ($this->order == 'asc') {
            $this->next_order = 'desc';
        }

        $this->Types = array( 'all' );

        if (isset($_REQUEST['Types'])) {
            $this->Types = $_REQUEST['Types'];
        }

        $this->Simulations = array( 'all' );

        if (isset($_REQUEST['cat'])) {
            $this->Simulations = array();

            $cat_encoding = $_REQUEST['cat'];

            $cat = CategoryUtils::inst()->getCategory($_REQUEST['cat']);
            $sims = SimFactory::inst()->getSimsByCatId($cat['cat_id'], true);

            foreach($sims as $sim) {
                $this->Simulations[] = $sim->getWrapped()->getName();
            }
        }
        else {
            if (isset($_REQUEST['Simulations'])) {
                $this->Simulations = $_REQUEST['Simulations'];
            }
            else {
                if (isset($GLOBALS['sim_id'])) {
                    $sim_id = $GLOBALS['sim_id'];
                }
                else if (isset($_REQUEST['sim_id'])) {
                    $sim_id = $_REQUEST['sim_id'];
                }

                if (isset($sim_id)) {
                    $sim = SimFactory::inst()->getById($sim_id);

                    $this->Simulations = array($sim->getWrapped()->getName());
                }
            }
        }

        $this->Levels = array( 'all' );

        if (isset($_REQUEST['Levels'])) {
            $this->Levels = $_REQUEST['Levels'];
        }

        return true;
    }

    function render() {
        if (isset($_REQUEST['content_only']) || isset($content_only)) {
            $GLOBALS['g_content_only'] = true;
            $result =
                browse_print_content_only($this->Simulations, $this->Types, $this->Levels,
                                          $this->sort_by, $this->order, $this->next_order, true);
            if (!$result) {
                print "<p>There are no contributions meeting the specified criteria.</p>";
            }
        }
        else {
            parent::render();
        }
    }

    function render_content() {
        if (isset($_REQUEST['content_only']) || isset($content_only)) {
        }
        else {
            $GLOBALS['g_content_only']          = false;
            $GLOBALS['g_cache_current_page'] = true;
            $this->print_content_with_header();
        }
    }

}

$page = new BrowseContributionsPage("Browse Contributions", NavBar::NAV_TEACHER_IDEAS, null, SitePage::AUTHLEVEL_NONE, false);
$page->add_javascript_file("js/browse.js");
$page->update();
$page->render();

?>