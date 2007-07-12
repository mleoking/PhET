<?php

	define('FILE_SEPARATOR', PHP_OS == 'WINNT' ? '\\' : '/');

	function file_detect_encoding($contents) {
		$encoding = mb_detect_encoding($contents);
		
		if (!$encoding) {
			$encoding = 'UTF-16';
		}
		
		return $encoding;
	}

	function file_with_local_separator($filename) {
	    return str_replace('/', FILE_SEPARATOR, $filename);
	}

	function file_mkdirs($dir, $mode = 0777) {
	    if (is_null($dir) || $dir === "" ){
	        return false;
	    }
	    else if (is_dir($dir) || $dir === "/" ){
	        return true;
	    }
	    else if (file_mkdirs(dirname($dir), $mode) ){
	        return mkdir($dir, $mode);
	    }
	    else {
	        return false;
	    }
	}

	function file_create_parents_of_file($file, $mode = 0777) {
	    return file_mkdirs(dirname($file), $mode);
	}

	function file_put_contents_anywhere($file, $contents) {
	    file_create_parents_of_file($file);
    
	    return file_put_contents($file, $contents);
	}

	function file_get_real_path($address) {
	   $address = explode('/', $address);
	   $keys = array_keys($address, '..');

	   foreach($keys as $keypos => $key) {
	       array_splice($address, $key - ($keypos * 2 + 1), 2);
	   }

	   $address = implode('/', $address);
	   $address = str_replace('./', '', $address);
   
	   return $address;
	}

	function file_replace_macros_in_file($file, $macro_map) {
	    if (($contents = file_get_contents($file)) !== false) {
	        foreach ($macro_map as $macro_name => $value) {
	            $contents = preg_replace("(\\$".strtoupper($macro_name)."\\$)", $value, $contents);
	        }
        
	        if (file_put_contents($file, $contents) === false) {
	            return false;
	        }
	    }
    
	    return true;
	}

	function file_cleanup_local_filename($filename) {
	    $forwardslash = str_replace("\\", "/", $filename);
    
	    return preg_replace('(\/+)', '/', $forwardslash);
	}

	function file_lock($text) {
	    $file_source = ROOT_DIR.$text."_lock";
    
	    if (($wh = fopen($file_source, 'x+')) !== FALSE) {
	        fflush($wh);
	        fclose($wh);
        
	        return true;
	    }
    
	    return false;
	}

	function file_unlock($text) {
	    $file_source = ROOT_DIR.$text."_lock";
    
	    unlink($file_source);
	}

	function file_list_in_directory($directory, $all_patterns = "*") {
	    $results = array();
    
		foreach (preg_split('/ *, */', $all_patterns) as $pattern) {
		    if (is_dir($directory)) {
		        $glob_pattern = "$directory/$pattern";
        
		        // Grab all files matching pattern:
		        foreach (glob($glob_pattern) as $filename) {
		            $fullpath = "$filename";
            
		            if (!is_dir($filename)) {
		                $results[] = file_cleanup_local_filename($fullpath);
		            }
		        }
        
		        // Recursively peak into any subdirs:
		        foreach (glob("$directory/*", GLOB_ONLYDIR) as $dirname) {
		            $fullpath = "$dirname";
            
		            foreach (file_list_in_directory($fullpath, $pattern) as $nested_file) {
		                $results[] = $nested_file;
		            }
		        }        
		    }
		}
    
	    return $results;
	}

	function file_get_extension($filename) {
	    $path_info = pathinfo($filename);
    
	    return $path_info['extension'];
	}

	function file_remove_extension($strName) { 
	    $ext = strrchr($strName, '.'); 

	    if($ext !== false) { 
	        $strName = substr($strName, 0, -strlen($ext)); 
	    } 
    
	    return $strName; 
	}

?>
