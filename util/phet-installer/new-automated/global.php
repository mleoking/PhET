<?php

	function flushing_echo($text) {
	    echo "$text\n<br/>";
	    flush();
	}

	function is_cmd_line_option_enabled($attr) {
	    $args = $_SERVER['argv'];
    
	    foreach ($args as $arg) {
	        if (preg_match('/\-+'.preg_quote($attr).'\b/', $arg)) {
	            return true;
	        }
	    }
    
	    return false;
	}

?>
