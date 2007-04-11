<?php

    function generate_check_status($item_num, $checked_item_num) {
        if ($checked_item_num == null && $item_num == "0" || $item_num == $checked_item_num) return "checked";
    
        return " ";
    }
    
    function url_exists($url) {
        if (is_array(get_headers($url))) {
            return true;
        }
        else {
            return false;
        }
    }

?>