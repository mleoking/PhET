<?php

    function generate_check_status($item_num, $checked_item_num) {
        if ($checked_item_num == null && $item_num == "0" || $item_num == $checked_item_num) return "checked";
    
        return " ";
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

    function print_editable_area($name, $contents, $rows = "20", $cols = "80") {
            print <<<EOT
                <textarea name="$name" rows="$rows" cols="$cols">$contents</textarea>
EOT;
        }
    

?>