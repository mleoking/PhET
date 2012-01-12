<?php

	function web_is_absolute_url($url) {
	    return preg_match('/http:\/\/.+/',$url);
	}

	function web_extract_attributes($element, $desired_attr_name) {
	    $attribs = array();
    
	    foreach ($element->attributes() as $name => $value) {
	        if ($name == $desired_attr_name) {
	            $attribs["$value"] = $element;
	        }
	    }
    
	    return $attribs;
	}

	function web_extract_all_attributes($node, $desired_attr_name, $include_root_attributes = false) {
	    $attribs = array();
    
	    if ($include_root_attributes !== false) {
	        // Rip all attributes of this node:
	        foreach (web_extract_attributes($node, $desired_attr_name) as $key => $value) {
	            $attribs[$key] = $value;
	        }
	    }
    
	    // Rip attributes of  all children:
	    foreach ($node->children() as $child) {
	        foreach (web_extract_all_attributes($child, $desired_attr_name, true) as $key => $value) {
	            $attribs[$key] = $value;
	        }
	    }
    
	    return $attribs;
	}

?>
