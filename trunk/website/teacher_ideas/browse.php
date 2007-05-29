<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");    
    include_once(SITE_ROOT."teacher_ideas/referrer.php");
    
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
            if (in_array($contrib[$field_name], $selected_values)) {
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
        global $sort_by, $order, $next_order;
        
        $link_order = null;
        
        if ($sort_by == $link_sort_by) {
            $link_order = $next_order;
        }
        else {
            $link_order = $order;
        }
        
        global $Simulations, $Types, $Levels;
        
        $filter_options = 
            url_encode_list('Simulations[]', $Simulations).'&amp;'.
            url_encode_list('Types[]',       $Types).'&amp;'.
            url_encode_list('Levels[]',      $Levels);
                
        $script = SITE_ROOT."teacher_ideas/browse.php?order=${link_order}&amp;sort_by=${link_sort_by}&amp;${filter_options}";
        
        $link = "<a href=\"${script}\">${desc}</a>";
        
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
        
        $on_change = 'onchange="javascript:update_browser_for_select_element(\''.$all_filter_name.'\');"';
        
        $list .= '<select name="'.$all_filter_name.'[]" '.$on_change.' multiple="multiple" size="'.$size.'" id="'.$all_filter_name.'_uid">';
        
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
        
        $encoded = urlencode($name).'=';

        $is_first = true;

        foreach($list as $item) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $encoded .= '&amp;';
            }
            
            $encoded .= urlencode($item);
        }
        
        return $encoded;
    }    
    
    function print_content_only() {
        $contributions = get_contributions();
        
        $title  = get_sorting_link('contribution_title',        'Title');
        $author = get_sorting_link('contribution_authors',      'Author');
        $level  = get_sorting_link('contribution_level_desc',   'Level');
        $type   = get_sorting_link('contribution_type_desc',    'Type');
        $sims   = get_sorting_link('sim_name',                  'Simulations');
        $date   = get_sorting_link('contribution_date_updated', 'Updated');
        
        print <<<EOT

            <div id="browseresults">
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
            contribution_print_summary3($contribution);
        }

        print <<<EOT

                    </tbody>

                </table>
            </div>

            <br/>
            <br/>
EOT;
    }
    
    function print_content_with_header() {    
        global $order, $sort_by, $referrer;
        
        global $Simulations, $Types, $Levels;
        
        $sim_list   = build_sim_list($Simulations);
        $type_list  = build_type_list($Types);        
        $level_list = build_level_list($Levels);
                
        print <<<EOT
            <script type="text/javascript">
                /* <![CDATA[ */
                    function update_browser_for_select_element(select_prefix) {                        
                        var select_id = select_prefix + '_uid';

                        var select_element = document.getElementById(select_id);
                        
                        var option_nodes = select_element.getElementsByTagName('option');
                        
                        var selected_options = '', is_first = true;
                        
                        for (var i = 0; i < option_nodes.length; i++) {
                            var option_node = option_nodes[i];
                            
                            if (option_node.selected) {
                                selected_options += '&';
                                
                                if (is_first) {
                                    is_first = false;
                                    
                                    selected_options += select_prefix + '[]=';
                                }
                                
                                selected_options += encodeURI(option_node.value);
                            }
                        }
                        
                        var url = 'browse.php?content_only=true' + selected_options;
                        
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
                            <td class="Simulations">
                                $sim_list
                            </td>
                        
                            <td class="Types">
                                $type_list                        
                            </td>
                        
                            <td class="Levels">
                                $level_list                        
                            </td>
                        </tr>
                    </tbody>
                </table>
                
                <div class="separator">
                </div>
            </form>
EOT;

        print_content_only();
    }
    
    if (isset($_REQUEST['sort_by'])) {
        $sort_by = $_REQUEST['sort_by'];
    }
    else {
        $sort_by = 'title';
    }
    
    if (isset($_REQUEST['order'])) {
        $order = $_REQUEST['order'];
    }
    else {
        $order = 'desc';
    }
    
    $next_order = 'desc';
    
    if ($order == 'desc') {
        $next_order = 'asc';
    }
    
    $Types = array( 'all' );
    
    if (isset($_REQUEST['Types'])) {
        $Types = $_REQUEST['Types'];
    }
    
    $Simulations = array( 'all' );
    
    if (isset($_REQUEST['Simulations'])) {
        $Simulations = $_REQUEST['Simulations'];
    }
    
    $Levels = array( 'all' );
    
    if (isset($_REQUEST['Levels'])) {
        $Levels = $_REQUEST['Levels'];
    }
    
    if (isset($_REQUEST['content_only'])) {
        print_content_only();
    }
    else {
        print_site_page('print_content_with_header', 3);
    }

?>