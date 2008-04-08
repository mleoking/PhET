<?php

    include_once("../admin/global.php");

    define("UP_ARROW",       SITE_ROOT."images/sorting-uarrow.gif");
    define("DOWN_ARROW",     SITE_ROOT."images/sorting-darrow.gif");

    function browse_sort_contributions($contributions, $sort_by, $order) {
        $keyed_contributions = array();
        $id_to_contribution  = array();

        foreach($contributions as $contribution_id => $all_contrib_info) {
//            $contribution_id = $contribution['contribution_id'];

            $id_to_contribution[$contribution_id] = $all_contrib_info;

            $key = "ZZZZZZZZZ";

            if (isset($all_contrib_info["contribution"]["$sort_by"])) {
                $key = $all_contrib_info["contribution"]["$sort_by"];

                if ($sort_by == 'contribution_authors') {
                    $names = explode(',', $key);

                    $parsed_name = parse_name($names[0]);

                    $key = $parsed_name['last_name'];
                }
            }

            $keyed_contributions[serialize($all_contrib_info)] = strtolower("$key");
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

    function orig_browse_sort_contributions($contributions, $sort_by, $order) {
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

    function browse_get_contributions($Simulations, $Types, $Levels, $sort_by, $order) {
        $contributions = newer_contribution_get_specific_contributions($Simulations, $Types, $Levels);

        $contributions = /*orig_*/browse_sort_contributions($contributions, $sort_by, $order);

        return $contributions;
    }

    function browse_url_encode_list($name, $list) {
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

    function browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, $link_sort_by, $desc, $referrer) {
        $arrow_xml = '';

        if ($sort_by == $link_sort_by) {
            $link_order = $next_order;

            if ($order == 'asc') {
                $arrow_xml = '<img src="'.UP_ARROW.'" alt="Ascending Sort (Z-A)" />';
            }
            else {
                $arrow_xml = '<img src="'.DOWN_ARROW.'" alt="Descending Sort (A-Z)" />';
            }
        }
        else {
            $link_order = $order;
        }

        $filter_options =
            browse_url_encode_list('Simulations[]', $Simulations).
            browse_url_encode_list('Types[]',       $Types).
            browse_url_encode_list('Levels[]',      $Levels);

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

        $script = "${php_self}${prefix}order=${link_order}&amp;sort_by={$link_sort_by}{$filter_options}&amp;referrer={$referrer}";

        // Cleanup excess question marks:
        $script = preg_replace('/\?+/', '?', $script);

        $link = "<a href=\"${script}\">${arrow_xml}${desc}</a>";

        return <<<EOT
            <td>${link}</td>
EOT;
    }

    function browse_print_content_only($Simulations, $Types, $Levels,
                                        $sort_by, $order, $next_order, $print_simulations = true, $referrer = '') {
        // Create an id that uniquely identifies the browse parameters:
        $browse_id = md5(implode('', $Simulations).implode('', $Types).implode('', $Levels).$sort_by.$order );

        $browse_resource = "${browse_id}.cache";

        $cached_browse = cache_get(BROWSE_CACHE, $browse_resource);

        if ($cached_browse) {
            print $cached_browse;

            return true;
        }

        $contributions = browse_get_contributions($Simulations, $Types, $Levels, $sort_by, $order);

        $title  = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_title',        'Title', $referrer);
        $author = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_authors',      'Author', $referrer);
        $level  = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_level_desc',   'Level', $referrer);
        $type   = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_type_desc',    'Type', $referrer);

        if ($print_simulations) {
            $sims = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'sim_name', 'Simulations', $referrer);
        }
        else {
            $sims = '';
        }

        $date = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_date_updated', 'Updated', $referrer);

        //$contributions = consolidate_identical_adjacent_titles($contributions);

        $num_contributions = count($contributions);
        if ($num_contributions == 0) {
            return false;
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
            $html .= /*orig_*/contribution_get_contribution_summary_as_html($contribution, $print_simulations, true);
        }

        $html .= <<<EOT

                    </tbody>

                </table>
            </div>

EOT;

        cache_put(BROWSE_CACHE, $browse_resource, preg_replace('/\s\s+/', ' ', trim($html)));

        print $html;

        return true;
    }

?>