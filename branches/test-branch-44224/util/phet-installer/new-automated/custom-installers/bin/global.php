<?php

	function flushing_echo($text) {
        // This function was originally designed to include a <br/> tag so that
        // the output was easily readable as a web page, but as of April 2009
        // it has been over a year since the installer has been hooked up to
        // the web page (if it ever was), so the tag is being removed.  Put it
        // back if needed.

	    //echo "$text\n<br/>";
	    echo "$text\n";

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
