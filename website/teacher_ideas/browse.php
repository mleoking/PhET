<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."teacher_ideas/referrer.php");
include_once(SITE_ROOT."teacher_ideas/browse-utils.php");

/*
    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."teacher_ideas/referrer.php");
    include_once(SITE_ROOT."admin/nominate-utils.php");
    include_once(SITE_ROOT."admin/cache-utils.php");
*/


include_once("../admin/BasePage.php");

class BrowseContributions extends BasePage {
    function HIDE_sort_contributions($contributions, $sort_by, $order) {
        $keyed_contributions = array();
        $id_to_contribution  = array();

        foreach($contributions as $contribution) {
            $contribution_id = $contribution['contribution_id'];

            $id_to_contribution[$contribution_id] = $contribution;

            $key = "ZZZZZZZZZ";

            if (isset($contribution["$sort_by"])) {
                $key = $contribution["$sort_by"];

                if ($sort_by == 'contribution_authors') {
                    $names = explode(',', $key);

                    $parsed_name = parse_name($names[0]);

                    $key = $parsed_name['last_name'];
                }
            }

            $keyed_contributions[serialize($contribution)] = strtolower("$key");
        }

        if ($order == 'asc') {
            asort($keyed_contributions);
        }
        else {
            arsort($keyed_contributions);
        }

        $sorted_contributions = array();

        foreach($keyed_contributions as $serialized => $key) {
            $sorted_contributions[] = unserialize($serialized);
        }

        return $sorted_contributions;
    }

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

    function HIDE_get_contributions() {
        $contributions = contribution_get_specific_contributions($this->Simulations, $this->Types, $this->Levels);

        $contributions = $this->sort_contributions($contributions, $this->sort_by, $this->order);

        return $contributions;
    }

    function HIDE_get_sorting_link($link_sort_by, $desc) {
        $arrow_xml = '';

        if ($this->sort_by == $link_sort_by) {
            $link_order = $this->next_order;

            if ($this->order == 'asc') {
                $arrow_xml = '<img src="'.UP_ARROW.'" alt="Ascending Sort (Z-A)" />';
            }
            else {
                $arrow_xml = '<img src="'.DOWN_ARROW.'" alt="Descending Sort (A-Z)" />';
            }
        }
        else {
            $link_order = $this->order;
        }

        $filter_options =
            $this->url_encode_list('Simulations[]', $this->Simulations).
            $this->url_encode_list('Types[]',       $this->Types).
            $this->url_encode_list('Levels[]',      $this->Levels);

        // Remove all the parameters we insert ourselves:
        $php_self = remove_script_param_from_url("content_only",    $_SERVER['REQUEST_URI']);
        $php_self = remove_script_param_from_url("Simulations[]",   $php_self);
        $php_self = remove_script_param_from_url("Types[]",         $php_self);
        $php_self = remove_script_param_from_url("Levels[]",        $php_self);
        $php_self = remove_script_param_from_url("order",           $php_self);
        $php_self = remove_script_param_from_url("sort_by",         $php_self);
        $php_self = remove_script_param_from_url("referrer",        $php_self);

        // Decide whether to postfix URI with a question mark or an ampersand, depending
        // on whether or parameters are already being passed to the script.
        $prefix = '?';

        if (preg_match('/.+\\?.+=.+/i', $php_self) == 1) {
            $prefix = '&amp;';
        }

        $script = "${php_self}${prefix}order=${link_order}&amp;sort_by={$link_sort_by}{$filter_options}&amp;referrer={$this->referrer}";

        // Cleanup excess question marks:
        $script = preg_replace('/\?+/', '?', $script);

        $link = "<a href=\"${script}\">${arrow_xml}${desc}</a>";

        return <<<EOT
            <td>${link}</td>
EOT;
    }

    function build_option_string($id, $name, $selected_values) {
        $selected_status = '';

        if (in_array($id, $selected_values)) {
            $selected_status = 'selected="selected"';
        }

        return "<option value=\"$id\" $selected_status>$name</option>";
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
        $sim_names = sim_get_all_sim_names();

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

    function HIDE_url_encode_list($name, $list) {
        // [name1]=[value1]&[name2]=[value2]&[name3]=[value3]&

        $encoded = '';

        if (count($list) > 0) {
            foreach($list as $item) {
                $encoded .= '&amp;'.$name.'=';

                $encoded .= urlencode($item);
            }
        }
        else {
            $encoded .= '&amp;'.$name.'=all';
        }

        return $encoded;
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

    function HIDE_print_content_only($print_simulations = true) {
        // Create an id that uniquely identifies the browse parameters:
        $browse_id = md5(implode('', $this->Simulations).implode('', $this->Types).implode('', $this->Levels).$this->sort_by.$this->order );

        $browse_resource = "${browse_id}.cache";

        $cached_browse = cache_get(BROWSE_CACHE, $browse_resource);

        if ($cached_browse) {
            print $cached_browse;

            return;
        }

        $contributions = $this->get_contributions();

        $title  = $this->get_sorting_link('contribution_title',        'Title');
        $author = $this->get_sorting_link('contribution_authors',      'Author');
        $level  = $this->get_sorting_link('contribution_level_desc',   'Level');
        $type   = $this->get_sorting_link('contribution_type_desc',    'Type');

        if ($print_simulations) {
            $sims = $this->get_sorting_link('sim_name', 'Simulations');
        }
        else {
            $sims = '';
        }

        $date = $this->get_sorting_link('contribution_date_updated', 'Updated');

        //$contributions = consolidate_identical_adjacent_titles($contributions);

        $num_contributions = count($contributions);
        if ($num_contributions == 0) {
            if ($GLOBALS['g_content_only']) {
                print "<p>There are no contributions for this simulation.</p>";
            }
            else {
                print "<p>There are no contributions meeting the specified criteria.</p>";
            }

            return;
        }

        $html = '';

        $html .= <<<EOT
            <div id="browseresults" class="compact">
            <p>There are {$num_contributions} contributions listed.</p>
            <table>
                    <thead>
                        <tr>

                            $title

                            $author

                            $level

                            $type

                            $sims

                            $date

                        </tr>
                    </thead>

                    <tbody>
EOT;

        foreach($contributions as $contribution) {
            $html .= contribution_get_contribution_summary_as_html($contribution, $print_simulations, true);
        }

        $html .= <<<EOT

                    </tbody>

                </table>
            </div>
EOT;

        cache_put(BROWSE_CACHE, $browse_resource, preg_replace('/\s\s+/', ' ', trim($html)));

        print $html;
    }

    function print_content_with_header() {
        $sim_list   = $this->build_sim_list($this->Simulations);
        $type_list  = $this->build_type_list($this->Types);
        $level_list = $this->build_level_list($this->Levels);

        print <<<EOT
            <form id="browsefilter" method="post" action="browse.php">
                <div>
                    <input type="hidden" name="order"    value="{$this->order}"     />
                    <input type="hidden" name="sort_by"  value="{$this->sort_by}"   />
                    <input type="hidden" name="referrer" value="{$this->referrer}"  />
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

        browse_print_content_only($this->Simulations, $this->Types, $this->Levels,
                                    $this->sort_by, $this->order, $this->next_order, true, $this->referrer);
        //$this->print_content_only();

        if (isset($_REQUEST['cat'])) {
            $cat_encoding = $_REQUEST['cat'];

            print <<<EOT
                <div class="full-width">
                    <div class="rage_button_357660">
                        <a href="../simulations/index.php?cat=$cat_encoding">Back to Simulations</a>
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

            $cat_id = sim_get_cat_id_by_cat_encoding($cat_encoding);

            $sims = sim_get_sims_by_cat_id($cat_id);

            foreach($sims as $sim) {
                $this->Simulations[] = $sim['sim_name'];
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
                    $sim = sim_get_sim_by_id($sim_id);

                    $this->Simulations = array( html_entity_decode($sim['sim_name']) );
                }
            }
        }

        $this->Levels = array( 'all' );

        if (isset($_REQUEST['Levels'])) {
            $this->Levels = $_REQUEST['Levels'];
        }

        /*
        if (isset($_REQUEST['content_only']) || isset($content_only)) {
            $GLOBALS['g_content_only'] = true;
            assert(false);
            //print_content_only(!isset($sim_id));
        }
        else {
            $GLOBALS['g_content_only']          = false;
            $GLOBALS['g_cache_current_page'] = true;
            $this->print_content_with_header();
            //print_site_page('print_content_with_header', 3);
        }
*/
        return true;
    }

    function render() {
        if (isset($_REQUEST['content_only']) || isset($content_only)) {
            $GLOBALS['g_content_only'] = true;
            browse_print_content_only($this->Simulations, $this->Types, $this->Levels,
                                        $this->sort_by, $this->order, $this->next_order, true, $this->referrer);
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
            //print_site_page('print_content_with_header', 3);
        }
    }
}

auth_do_validation();
$page = new BrowseContributions(3, get_referrer(), "Browse Contributions");
$page->add_javascript_file("../js/browse.js");
$page->update();
$page->render();

?>