<?php
    //========================================================================
    // This file contains functions for locating and manipulating JNLP files.
    //========================================================================

    require_once("file-util.php");
    require_once("string-util.php");

    define('JNLP_CODEBASE_PATTERN', '/codebase *= *"([^"]+)"/i');

    function jnlp_get_all_in_directory($directory) {
        $jnlp_files = file_list_in_directory($directory, "*.jnlp");

        $jnlp_xmls = array();

        foreach ($jnlp_files as $filename) {
            $contents = file_get_contents($filename);
            $jnlp_xmls[$filename] = $contents;
        }

        return $jnlp_xmls;
    }

    function jnlp_get_codebase($jnlp_file) {
        $matches = array();
 
        if (preg_match(JNLP_CODEBASE_PATTERN, $jnlp_file, $matches)) {
            $codebase = $matches[1];
 
            if (!string_ends_with($codebase, '/')) {
                $codebase .= '/';
            }
 
            return $codebase;
        }
        else {
            return false;
        }
    }
 

    //-------------------------------------------------------------------------
    // This function replaces the "codebase" directive in the JNLP file with
    // the specified pattern.  This is done to allow the JNLP files to be
    // updated when installed on the user's system to point to the correct
    // location, which isn't known until install time.
    //-------------------------------------------------------------------------
    function jnlp_replace_codebase_with_macro($jnlp_file, $codebase_pattern, $macro_name) {
        return preg_replace('/codebase *= *"'.$codebase_pattern.'/', 'codebase="'.$macro_name, $jnlp_file);
    }

    function jnlp_add_permissions_request($jnlp_file) {
        $pattern = '/<\/jnlp>/';
        $replacement = "    <security>\n        <all-permissions/>\n    </security>\n\n</jnlp>";
        return preg_replace($pattern, $replacement, $jnlp_file);
    }

    function jnlp_replace_absolute_links_with_local_file_macro($jnlp_file, $absolute_link_pattern, $macro_name) {
        return preg_replace('/href *= *"'.$absolute_link_pattern.'/', 'href="file:///'.$macro_name, $jnlp_file);
    }

    function jnlp_get_all_resource_links($jnlp_file) {
        $resources = array();

        $all_matches = array();

        if (preg_match_all('/\bhref *= *"([^"]+)"/', $jnlp_file, $all_matches)) {
            foreach($all_matches[1] as $link) {
                $resources[] = $link;
            }
        }

        return $resources;
    }

    function jnlp_remove_resource_link($jnlp_file, $link) {
        return preg_replace('/< *\S+ +([^>\/]+ )? *href *= *"'.preg_quote($link, '/').'" *[^>\/]* *\/ *>/', '', $jnlp_file);
    }

    function mb_ereg_replace_callback($pattern, $callback, $string, $limit = null, $option = null) {
        $compat_preg_replace_callback = false;

        if (is_null($option)) {
            $option = mb_regex_set_options();
        }

        if (!is_null($limit)) {
            $limit = intval($limit);
            if ($limit < 0) {
                if ($compat_preg_replace_callback) {
                    $limit = ($limit == -1) ? null : 0;
                } else {
                    $back = $limit;
                    $limit = null;
                }
            }
        }

        mb_ereg_search_init($string, $pattern, $option);
        $m = array();
        $i = 0;
        while (($p = mb_ereg_search_pos()) !== false && (is_null($limit) || $i < $limit)) {
            $r = array(
                'offset' => $p[0],
                'length' => $p[1],
                'regs'   => mb_ereg_search_getregs()
            );
            array_push($m, $r);
            $i++;
        }

        if (isset($back) && $i > abs($back)) {
            array_splice($m, 0, $back);
        }

        $result = '';
        while (($r = array_pop($m)) !== null) {
            $result = call_user_func($callback, $r['regs'])
                    . mb_strcut($string, $r['offset'] + $r['length'])
                    . $result;
            $string = mb_strcut($string, 0, $r['offset']);
        }

        return $string . $result;
    }

    /**
     * Reparses all href elements of XML files in the specified directory,
     * using a user-supplied function.
     *
     * @param $directory          The directory to search.
     *
     * @param $reparser_func      A function that will be supplied with the
     *                            href value, and which is expected to return
     *                            the new href value.
     *
     * @param $extension          The extension of files to search for.
     *                            Defaults to 'xml'.
     *
     */
    function reparse_all_hrefs($directory, $reparser_func, $extension = 'xml') {
        $files = list_files($directory, "*.$extension");

        foreach ($files as $filename) {
            $contents = file_get_contents($filename);

            $encoding = mb_detect_encoding($contents);

            if (!$encoding) $encoding = 'UTF-16';

            mb_internal_encoding($encoding);
            mb_regex_encoding($encoding);

            $pattern = mb_convert_encoding('/href *= *"([^"]+)"/i', $encoding, 'UTF-8');

            $new_contents = mb_ereg_replace_callback(
                $pattern,
                create_function(
                    '$matches',
                    "return call_user_func('$reparser_func', \$matches[1]);"
                ),
                $contents
            );

            file_put_contents($filename, $new_contents);
        }
    }

?>
