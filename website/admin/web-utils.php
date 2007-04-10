<?php

function generate_check_status($item_num, $checked_item_num) {
    if ($checked_item_num == null && $item_num == "0" || $item_num == $checked_item_num) return "checked";
    
    return " ";
}

?>