<?php

    function generate_check_status($item_num, $checked_item_num) {
        if ($checked_item_num == null && $item_num == "0" || $item_num == $checked_item_num) return "checked";
    
        return " ";
    }
    
    function force_redirect($url, $timeout = 0, $die = true) { 
        print "<META http-equiv=\"Refresh\" content=\"$timeout;url=$url\">";
    }
    
    // function url_exists($url) {
    //     if (is_array(get_headers($url))) {
    //         return true;
    //     }
    //     else {
    //         return false;
    //     }
    // }
    
    function url_exists($url) {
        return true;
    }
    
    function format_for_html($string) {
        return preg_replace('/&(?!amp;)/', '&amp;', $string);
    }
    
    function get_script_param($param_name, $default_value = "") {
        if (isset($_REQUEST['sim_id'])) {
            return $_REQUEST['sim_id'];
        }
        
        return $default_value;
    }
    
    function gather_script_params_into_globals() {        
        foreach($_REQUEST as $key => $value) {
            $GLOBALS["$key"] = "$value";
        }
    }
    
    function print_comma_list_as_bulleted_list($comma_list) {
        print "<ul>";
        
        foreach(explode(',', $comma_list) as $item) {
            $trimmed_item = trim($item);
            
            print "<li>$trimmed_item</li>";
        }
        
        print "</ul>";
    }

    function print_editable_area($name, $contents, $rows = "20", $cols = "80") {
        print("<textarea name=\"$name\" rows=\"$rows\" cols=\"$cols\">$contents</textarea>");
    }
    
    function print_captioned_editable_area($caption, $name, $contents, $rows = "20", $cols = "80") {
        print("<p align=\"left\" class=\"style16\">$caption<br/>");
            
        print_editable_area($name, $contents, $rows, $cols);
        
        print("</p>");
    }

?>