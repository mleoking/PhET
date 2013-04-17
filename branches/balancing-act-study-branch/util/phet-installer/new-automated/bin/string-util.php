<?php

	function string_ends_with( $str, $sub ) {
	   return ( substr( $str, strlen( $str ) - strlen( $sub ) ) === $sub );
	}
	
	function make_pattern(&$pat, $key) {
	   $pat = '/'.preg_quote($pat, '/').'/i';
	}
	
	if (!function_exists('str_ireplace')){
	    function str_ireplace($search, $replace, $subject){
	        if (is_array($search)){
	            array_walk($search, 'make_pattern');
	        }
	        else{
	            $search = '/'.preg_quote($search, '/').'/i';
	        }
	
	        return preg_replace($search, $replace, $subject);
	    }
	}

?>