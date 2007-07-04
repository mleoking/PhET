<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");    
    include_once(SITE_ROOT."teacher_ideas/referrer.php");
    
    define("UP_ARROW",   SITE_ROOT."images/sorting-uarrow.gif");
    define("DOWN_ARROW", SITE_ROOT."images/sorting-darrow.gif");    
    
    function sort_contributions($contributions, $sort_by, $order) {
        $keyed_contributions = array();
        $id_to_contribution  = array();
        
        foreach($contributions as $contribution) {
            $contribution_id = $contribution['contribution_id'];
            
            $id_to_contribution[$contribution_id] = $contribution;            
            
            $key = "ZZZZZZZZZ";
            
            if (isset($contribution["$sort_by"])) {
                $key = $contribution["$sort_by"];
                
                if ($sort_by == 'contributor_authors') {
                    $names = explode(',', $key);
                    
                    $parsed_name = parse_name($names[0]);
                    
                    $key = $parsed_name['last_name'];
                }
            }
            
            $keyed_contributions[serialize($contribution)] = "$key";
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
    
    function get_contributions() {
        global $sort_by, $order, $next_order;
        
        $contributions = contribution_get_all_contributions();
        
        $contributions = contribution_explode_contributions($contributions);
        
        $contributions = sort_contributions($contributions, $sort_by, $order);
        
        global $Simulations, $Types, $Levels;
        
        $contributions = filter_contributions($contributions, 'sim_name',                $Simulations);
        $contributions = filter_contributions($contributions, 'contribution_type_desc',  $Types);
        $contributions = filter_contributions($contributions, 'contribution_level_desc', $Levels);
        
        return $contributions;        
    }
    
    function get_sorting_link($link_sort_by, $desc) {
        global $sort_by, $order, $next_order, $referrer;
            
        $arrow_xml = '';
        
        if ($sort_by == $link_sort_by) {
            $link_order = $next_order;
            
            if ($order == 'asc') {
                $arrow_xml = '<img src="'.UP_ARROW.'"/>';
            }
            else {
                $arrow_xml = '<img src="'.DOWN_ARROW.'"/>';
            }
        }
        else {
            $link_order = $order;
        }
        
        global $Simulations, $Types, $Levels;
        
        $filter_options = 
            url_encode_list('Simulations[]', $Simulations).
            url_encode_list('Types[]',       $Types).
            url_encode_list('Levels[]',      $Levels);

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
                
        $script = "${php_self}${prefix}order=${link_order}&amp;sort_by=${link_sort_by}${filter_options}&amp;referrer=${referrer}";
        
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
        
        $list .= build_option_string('all', "All $all_filter_name", $selected_values);
        
        foreach($names as $name) {
            $list .= build_option_string($name, $name, $selected_values);
        }
        
        $list .= '</select>';        
        
        return $list;
    }
    
    function build_sim_list($selected_values) {
        $sim_names = sim_get_all_sim_names();
        
        return build_association_filter_list($sim_names, "Simulations", $selected_values);
    }
    
    function build_level_list($selected_values) {
        $level_names = contribution_get_all_level_names();
        
        return build_association_filter_list($level_names, "Levels", $selected_values);
    }
    
    function build_type_list($selected_values) {
        $type_names = contribution_get_all_type_names();
        
        return build_association_filter_list($type_names, "Types", $selected_values);
    }
    
    function url_encode_list($name, $list) {
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
                    $new_last_value = $new[$new_last_index][$cur_field];
                    
                    if ($cur_value != '' && !strrchr($new_last_value, $cur_value)) {
                        $new[$new_last_index][$cur_field] .= ", $cur_value";
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
    
    function print_content_only($print_simulations = true) {
        $contributions = get_contributions();
        
        $title  = get_sorting_link('contribution_title',        'Title');
        $author = get_sorting_link('contribution_authors',      'Author');
        $level  = get_sorting_link('contribution_level_desc',   'Level');
        $type   = get_sorting_link('contribution_type_desc',    'Type');
        
        if ($print_simulations) {
            $sims = get_sorting_link('sim_name', 'Simulations');
        }
        else {
            $sims = '';
        }
        
        $date   = get_sorting_link('contribution_date_updated', 'Updated');
        
        $contributions = consolidate_identical_adjacent_titles($contributions);
        
        print <<<EOT

            <div id="browseresults" class="compact">
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
            contribution_print_summary3($contribution, $print_simulations);
        }

        print <<<EOT

                    </tbody>

                </table>
            </div>
EOT;
    }
    
    function print_content_with_header() {    
        global $order, $sort_by, $referrer;
        
        global $Simulations, $Types, $Levels;
        
        $sim_list   = build_sim_list($Simulations);
        $type_list  = build_type_list($Types);        
        $level_list = build_level_list($Levels);
                
        print <<<EOT
            <h1>Browse Contributions</h1>
            
            <script type="text/javascript">
                /* <![CDATA[ */
                    function browse_build_update_query(select_prefix) {                        
                        var select_id = select_prefix + '_uid';

                        var select_element = document.getElementById(select_id);
                        
                        var option_nodes = select_element.getElementsByTagName('option');
                        
                        var selected_options = '';
                        
                        for (var i = 0; i < option_nodes.length; i++) {
                            var option_node = option_nodes[i];
                            
                            if (option_node.selected) {
                                selected_options += '&' + select_prefix + '[]=';
                                
                                selected_options += encodeURI(option_node.value);
                            }
                        }
                        
                        return selected_options;
                    }
                    
                    function browse_update_browser_for_select_element() {   
                        var sims_query   = browse_build_update_query('Simulations');
                        var types_query  = browse_build_update_query('Types');
                        var levels_query = browse_build_update_query('Levels');
                                                
                        var url = 'browse.php?content_only=true&order=$order&sort_by=$sort_by' + sims_query + types_query + levels_query;
                        
                        HTTP.updateElementWithGet(url, null, 'browseresults');                        

                        return false;
                    }
                /* ]]> */
            </script>
            
            <form id="browsefilter" name="browseform" method="post" action="browse.php">
                <input type="hidden" name="order"    value="$order"     />
                <input type="hidden" name="sort_by"  value="$sort_by"   />
                <input type="hidden" name="referrer" value="$referrer"  />
                
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
                                <input type="submit" name="Filter" value="Filter" />
                            </td>
                        </tr>
                    </tbody>
                </table>
                
                <div class="separator">
                </div>
            </form>
EOT;

        print_content_only();
        
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
    
    global $sort_by, $order, $next_order, $Types, $Simulations, $Levels;
    
    if (isset($_REQUEST['sort_by'])) {
        $sort_by = $_REQUEST['sort_by'];
    }
    else {
        $sort_by = 'title';
    }
    
    if (isset($_REQUEST['order'])) {
        $order = strtolower($_REQUEST['order']);
    }
    else {
        $order = 'desc';
    }
    
    $next_order = 'asc';
    
    if ($order == 'asc') {
        $next_order = 'desc';
    }
    
    $Types = array( 'all' );
    
    if (isset($_REQUEST['Types'])) {
        $Types = $_REQUEST['Types'];
    }
    
    $Simulations = array( 'all' );
    
    if (isset($_REQUEST['cat'])) {
        $Simulations = array();
        
        $cat_encoding = $_REQUEST['cat'];
        
        $cat_id = sim_get_cat_id_by_cat_encoding($cat_encoding);
        
        $sims = sim_get_sims_by_cat_id($cat_id);
        
        foreach($sims as $sim) {
            $Simulations[] = $sim['sim_name'];
        }
    }
    else {
        if (isset($_REQUEST['Simulations'])) {
            $Simulations = $_REQUEST['Simulations'];
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
            
                $Simulations = array( $sim['sim_name'] );
            }
        }
    }
    
    $Levels = array( 'all' );
    
    if (isset($_REQUEST['Levels'])) {
        $Levels = $_REQUEST['Levels'];
    }
    
    if (isset($_REQUEST['content_only']) || isset($content_only)) {
        print_content_only(!isset($sim_id));
    }
    else {
        print_site_page('print_content_with_header', 3);
    }

?>