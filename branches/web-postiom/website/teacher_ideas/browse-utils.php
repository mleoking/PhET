<?php

    // See comment in browse.php for a terse explaination about what is going on here.

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

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

        $sorted_contributions = /*orig_*/browse_sort_contributions($contributions, $sort_by, $order);

        return $sorted_contributions;
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

    function browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, $link_sort_by, $desc) {
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

        // Decide whether to postfix URI with a question mark or an ampersand, depending
        // on whether or parameters are already being passed to the script.
        $prefix = '?';

        if (preg_match('/.+\\?.+=.+/i', $php_self) == 1) {
            $prefix = '&amp;';
        }

        $script = "${php_self}${prefix}order=${link_order}&amp;sort_by={$link_sort_by}{$filter_options}";

        // Cleanup excess question marks:
        $script = preg_replace('/\?+/', '?', $script);

        $link = "<a href=\"${script}\">${arrow_xml}${desc}</a>";

        return <<<EOT
            <td>${link}</td>
EOT;
    }

    function browse_print_content_only($Simulations, $Types, $Levels,
                                        $sort_by, $order, $next_order, $print_simulations = true) {
        // Create an id that uniquely identifies the browse parameters:
        $browse_id = md5(implode('', $Simulations).implode('', $Types).implode('', $Levels).$sort_by.$order );

        $browse_resource = "${browse_id}.cache";

        $cached_browse = cache_get(BROWSE_CACHE, $browse_resource);

        if ($cached_browse) {
            print $cached_browse;

            return true;
        }

        $contributions = browse_get_contributions($Simulations, $Types, $Levels, $sort_by, $order);

        $title  = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_title',        'Title');
        $author = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_authors',      'Author');
        $level  = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_level_desc',   'Level');
        $type   = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_type_desc',    'Type');

        if ($print_simulations) {
            $sims = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'sim_name', 'Simulations');
        }
        else {
            $sims = '';
        }

        $date = browse_get_sorting_link($Simulations, $Types, $Levels, $sort_by, $order, $next_order, 'contribution_date_updated', 'Updated');

        //$contributions = consolidate_identical_adjacent_titles($contributions);

        $num_contributions = count($contributions);
        if ($num_contributions == 0) {
            return false;
        }

        if ($num_contributions == 1) {
            $contributions_text = "is 1 contribution";
        }
        else {
            $contributions_text = "are {$num_contributions} contributions";
        }

        $html = '';

        // TODO: refactor: below is the fastest "get it done NOW" method, and is ugly and kinda hacky
        // If it sorted by a multiple key, it totally ignores the sorting that has already happened
        $jump_list = '';
        $html_table_rows = '';
        $sort_key_to_section = array("sim_name" => "simulations",
            "contribution_type_desc" => "types",
            "contribution_level_desc" => "levels");
        $sort_key_filter = array("sim_name" => $Simulations,
            "contribution_type_desc" => $Types,
            "contribution_level_desc" => $Levels);
        if (array_key_exists($sort_by, $sort_key_filter)) {
            // In this if clause, we want to sort by things that have more than 1 association,
            // such as simulations or levels associated with a contribution
            $section = $sort_key_to_section[$sort_by];

            // First, create an array with keys => value pairs of
            // "filtered item" => contributions with that element
            // ex for $sort_by==sim_name: "Arithmetic" => array(<contributions associated with simulation Arithmetic>)
            // ex for $sort_by==contrbitution_level_desc: "High School" => array(<contributions associated with level High School>)
            // etc
            $contr = array();
            foreach($contributions as $contribution) {
                foreach ($contribution[$section] as $idx => $sim) {
                    if ((in_array('all', $sort_key_filter["$sort_by"])) ||
                        in_array($sim[$sort_by], $sort_key_filter["$sort_by"])) {
                        $contr[$sim[$sort_by]][] = $contribution;
                    }
                }
            }

            // Now grab the keys from the previous loop and sort them
            $keys = array_keys($contr);
            $sorted_keys = sort($keys);
            if ($order == 'desc') {
                $sorted_keys = array_reverse($keys);
                $keys = $sorted_keys;
            }

            // Custom function for case insenstive sorting of the contribution title
            function mycmp($a, $b) {
                return strcasecmp($a, $b);
            }

            // Custom function for case insenstive reverse sorting of the contribution title
            function myrevcmp($a, $b) {
                return strcasecmp($b, $a);
            }

            // Now create a section for each key, and put all associated elements in it (sorted)
            // This will build an anchor and link to have a sort of "table of contents"
            // as well as the actual table rows
            foreach ($keys as $key) {
                // Format the key for display in HTML
                $formatted_key = format_string_for_html($key);

                // and have a version to use in the anchors
                $encoded_key = web_encode_string($key);

                // Get all contributions associated with the key
                $con = $contr[$key];

                // Create the TOC
                $jump_list .= '<li><a href="#key_'.$encoded_key.'">'.$formatted_key.'</a></li>'."\n";

                // Create the anchor and title for this section
                $html_table_rows .= '<tr><td colspan="6" style="background-color:#cccccc;">'.
                    '<a name="key_'.$encoded_key.'"></a>'.
                    "<strong>{$formatted_key}</strong>".'</td></tr>'."\n";

                // Sort the section
                $con2 = array();
                foreach ($con as $conn) {
                    $con2[$conn['contribution']['contribution_title']] = $conn;
                }
                $sorted_kkeys = uksort($con2, (($order == "asc") ? "mycmp" : "myrevcmp"));

                // Now make a row for each sorted contribution in this section
                foreach ($con2 as $ignored_key => $conn) {
                    $html_table_rows .= contribution_get_contribution_summary_as_html($conn, $print_simulations, true);
                }
            }
        }
        else {
            // Sorting by something with only 1 association, like the contribution title
            foreach($contributions as $contribution) {
                $html_table_rows .= contribution_get_contribution_summary_as_html($contribution, $print_simulations, true);
            }
        }

        $html_jump_list = '';
        if (!empty($jump_list)) {
            $html_jump_list = "<p><strong>Quick jump list:</strong></p>\n";
            $html_jump_list .= "<ul>\n".$jump_list."</ul>\n";
        }

        $html .= <<<EOT
            <div id="browseresults" class="compact">
            <p>There {$contributions_text} listed.</p>
            {$html_jump_list}
            <table>
                    <thead>
                        <tr>

                        {$title}

                        {$author}

                        {$level}

                        {$type}

                        {$sims}

                        {$date}

                        </tr>
                    </thead>

                    <tbody>

                    {$html_table_rows}

                    </tbody>

                </table>
            </div>

EOT;

        cache_put(BROWSE_CACHE, $browse_resource, preg_replace('/\s\s+/', ' ', trim($html)));

        print $html;

        return true;
    }

?>