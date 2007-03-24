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

?>
