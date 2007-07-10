<?php

require_once("file-util.php");

function get_all_jnlp($directory) {
    $jnlp_files = list_files($directory, "*.jnlp");
    
    $jnlp_xmls = array();
    
    foreach ($jnlp_files as $filename) {
        $jnlp_xmls[$filename] = simplexml_load_file($filename);
    }
    
    return $jnlp_xmls;
}

function convert_to_utf8($contents) {
    return $contents;
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
