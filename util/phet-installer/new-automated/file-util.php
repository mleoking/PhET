<?php

	define('FILE_SEPARATOR_WINNT',  '\\');
	define('FILE_SEPARATOR_Linux',  '/');
	define('FILE_SEPARATOR_Darwin', '/');
	
	define('FILE_SEPARATOR', PHP_OS == 'WINNT' ? FILE_SEPARATOR_WINNT : FILE_SEPARATOR_Linux);

	if (!function_exists('file_put_contents')) {
		function file_put_contents($n, $d, $flag = false) {
		    $mode = ($flag == FILE_APPEND || strtoupper($flag) == 'FILE_APPEND') ? 'a' : 'w';
		    $f = @fopen($n, $mode);
		    if ($f === false) {
		        return 0;
		    } 
			else {
		        if (is_array($d)) $d = implode($d);
		        $bytes_written = fwrite($f, $d);
		        fclose($f);
		        return $bytes_written;
		    }
		}
	}

	function file_detect_encoding($contents) {
		$encoding = mb_detect_encoding($contents);
		
		if (!$encoding) {
			$encoding = 'UTF-16';
		}
		
		return $encoding;
	}

	function file_with_separator($filename, $separator) {
	    return str_replace('/', $separator, $filename);
	}

	function file_with_local_separator($filename) {
	    return file_with_separator($filename, FILE_SEPARATOR);
	}

	/**
	 * Executes a 'file processing' command, with a specified source/destination
	 * file, a specified file input (or inputs), the program that will perform
	 * the file processing, and arguments to be passed to the program. All of the
	 * file inputs must be located in the same directory, or the method will fail.
	 *
	 */
	function file_exec_file_processor($file_input, $dest_file, $program, $args) {
		$files_list      = '';
		$new_working_dir = null;
		
		if (is_array($file_input)) {
			foreach ($file_input as $file) {
				if ($new_working_dir == null) {
					$new_working_dir = ''.dirname($file).'';
				}
				
				$file = substr($file, strlen($new_working_dir) + 1);
				
				$files_list .= "\"$file\" ";
			}
		}
		else {
			$new_working_dir = dirname($file_input);
			$file_input      = basename($file_input);
			
			$files_list .= "\"$file_input\"";
		}
		
		$full_exec = "$program $args \"$dest_file\" $files_list";
		
		chdir($new_working_dir);
		
		return exec($full_exec);
	}
	
	function file_native_zip($file_input, $dest_file) {
		return file_exec_file_processor($file_input, $dest_file, "zip", "-vr9");
	}
	
	function file_native_tar($file_input, $dest_file) {
		return file_exec_file_processor($file_input, $dest_file, "tar", "--create --file");
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
            chmod($file_source, 664);
	        return true;
	    }
    
	    return false;
	}

	function file_unlock($text) {
	    $file_source = ROOT_DIR.$text."_lock";
    
        if (file_exists($file_source)){
	        unlink($file_source);
        }
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
	
	function file_remove_all($dirname) {
		if (!is_dir($dirname)) {
			unlink($dirname);
		}
	    else if ($dirHandle = opendir($dirname)) {
			$old_cwd = getcwd();
			chdir($dirname);

			while ($file = readdir($dirHandle)) {
				if ($file == '.' || $file == '..') continue;

				if (is_dir($file)) {
					if (!file_remove_all($file)) 
						return false;
				}
				else {
					if (!unlink($file)) 
						return false;
				}
			}

			closedir($dirHandle);
			chdir($old_cwd);
		
			if (!rmdir($dirname)) 
				return false;

			return true;
	    }
	    else {
	        return false;
        }
    }

	function file_copy($srcdir, $dstdir, $verbose = false) {
		if (is_file($srcdir)) {
			copy($srcdir, $dstdir);
			
			return 1;
		}
		else {
			$num = 0;
			
			if (!is_dir($dstdir)) {
				file_mkdirs($dstdir);
			}
			if ($curdir = opendir($srcdir)) {
				while($file = readdir($curdir)) {
					if($file != '.' && $file != '..') {
						$srcfile = $srcdir.FILE_SEPARATOR.$file;
						$dstfile = $dstdir.FILE_SEPARATOR.$file;
						
						if (is_file($srcfile)) {
							if(is_file($dstfile)) {
								$ow = filemtime($srcfile) - filemtime($dstfile);
							} 
							else {
								$ow = 1;
							}
							
							if ($ow > 0) {
								if ($verbose) {
									echo "Copying '$srcfile' to '$dstfile'...";
								}
								if (copy($srcfile, $dstfile)) {
                                    // Commenting out this touch operation for
                                    // now, since it seems to cause issues
                                    // when other users try to run this
                                    // routine.  Remove permanently if this
                                    // doesn't introduce any other problems.
                                    // JP Blanco, May 5 2009.
									// touch($dstfile, filemtime($srcfile)); 
									
									$num++;
									
									if ($verbose) {
										echo "OK\n";
									}
								}
								else {
									echo "Error: File '$srcfile' could not be copied!\n";
								}
							}                   
						}
						else if (is_dir($srcfile)) {
							$num += dircopy($srcfile, $dstfile, $verbose);
						}
					}
				}
				
				closedir($curdir);
			}
			
			return $num;
		}
	}

    function file_chmod_recursive($path, $filePerm=0644, $dirPerm=0755) {

        // Check if the path exists
        if(!file_exists($path)) {
             return(FALSE);
        }      

        if(is_file($path))
        {
            // This is a file - chmod the file with the given permissions
            chmod($path, $filePerm);
        } elseif(is_dir($path)) {
            // This is a directory...
            // Get an array of the contents
            $foldersAndFiles = scandir($path);
            // Remove "." and ".." from the list
            $entries = array_slice($foldersAndFiles, 2);
            // Parse every result...
            foreach($entries as $entry)
            {
               if ( ( $entry != '.' ) && ( $entry != '..' ) ){
                   // And call this function again recursively, with the same permissions
                   file_chmod_recursive($path."/".$entry, $filePerm, $dirPerm);
               }
            }
            // When we are done with the contents of the directory, we chmod the directory itself
            chmod($path, $dirPerm);
        }
        // Everything seemed to work out well, return TRUE
        return(TRUE);
    }

?>
