<?php

define('FILE_SEPARATOR', PHP_OS == 'WINNT' ? '\\' : '/');

function with_local_separator($filename) {
    return str_replace('/', FILE_SEPARATOR, $filename);
}

function mkdirs($dir, $mode = 0777) {
    if (is_null($dir) || $dir === "" ){
        return false;
    }
    else if (is_dir($dir) || $dir === "/" ){
        return true;
    }
    else if (mkdirs(dirname($dir), $mode) ){
        return mkdir($dir, $mode);
    }
    else {
        return false;
    }
}

function create_parents_dirs_of_file($file, $mode = 0777) {
    return mkdirs(dirname($file), $mode);
}

function any_file_put_contents($file, $contents) {
    create_parents_dirs_of_file($file);
    
    return file_put_contents($file, $contents);
}

function get_real_path($address) {
   $address = explode('/', $address);
   $keys = array_keys($address, '..');

   foreach($keys AS $keypos => $key)
   {
       array_splice($address, $key - ($keypos * 2 + 1), 2);
   }

   $address = implode('/', $address);
   $address = str_replace('./', '', $address);
   
   return $address;
}

function replace_macros_in_file($file, $macro_map) {
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

function cleanup_local_filename($filename) {
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

function list_files($directory, $pattern = "*") {
    $results = array();
    
    if (is_dir($directory)) {
        $glob_pattern = "$directory/$pattern";
        
        // Grab all files matching pattern:
        foreach (glob($glob_pattern) as $filename) {
            $fullpath = "$filename";
            
            if (!is_dir($filename)) {
                $results[] = cleanup_local_filename($fullpath);
            }
        }
        
        // Recursively peak into any subdirs:
        foreach (glob("$directory/*", GLOB_ONLYDIR) as $dirname) {
            $fullpath = "$dirname";
            
            foreach (list_files($fullpath, $pattern) as $nested_file) {
                $results[] = $nested_file;
            }
        }        
    }
    
    return $results;
}

function get_file_extension($filename) {
    $path_info = pathinfo($filename);
    
    return $path_info['extension'];
}

function remove_file_extension($strName) { 
    $ext = strrchr($strName, '.'); 

    if($ext !== false) { 
        $strName = substr($strName, 0, -strlen($ext)); 
    } 
    
    return $strName; 
}

?>
