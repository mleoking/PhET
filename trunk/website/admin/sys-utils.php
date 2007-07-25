<?php

    if (!function_exists('file_put_contents') && !defined('FILE_APPEND')) {
        define('FILE_APPEND', 1);
        
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
    
    if (!function_exists('get_headers')) {
        function get_headers($url,$format=0, $user='', $pass='', $referer='') {
            if (!empty($user)) {
                $authentification = base64_encode($user.':'.$pass);
                $authline = "Authorization: Basic $authentification\r\n";
            }
			else {
				$authline = '';
			}

            if (!empty($referer)) {
                $refererline = "Referer: $referer\r\n";
            }
			else {
				$refererline = '';
			}

            $url_info=parse_url($url);
            $port = isset($url_info['port']) ? $url_info['port'] : 80;
            $fp=fsockopen($url_info['host'], $port, $errno, $errstr, 30);
            if($fp) {
                $head = "GET ".@$url_info['path']."?".@$url_info['query']." HTTP/1.0\r\n";
                if (!empty($url_info['port'])) {
                    $head .= "Host: ".@$url_info['host'].":".$url_info['port']."\r\n";
                } else {
                    $head .= "Host: ".@$url_info['host']."\r\n";
                }
                $head .= "Connection: Close\r\n";
                $head .= "Accept: */*\r\n";
                $head .= $refererline;
                $head .= $authline;
                $head .= "\r\n";

                fputs($fp, $head);       
                while(!feof($fp) or ($eoheader==true)) {
                    if($header=fgets($fp, 1024)) {
                        if ($header == "\r\n") {
                            $eoheader = true;
                            break;
                        } else {
                            $header = trim($header);
                        }

                        if($format == 1) {
                        $key = array_shift(explode(':',$header));
                            if($key == $header) {
                                $headers[] = $header;
                            } else {
                                $headers[$key]=substr($header,strlen($key)+2);
                            }
                        unset($key);
                        } else {
                            $headers[] = $header;
                        }
                    } 
                }
                return $headers;

            } else {
                return false;
            }
        }
    }
    
    function mkdirs($dirName, $rights=0777){
        $dirs = explode('/', $dirName);
        $dir  = '';
        
        foreach ($dirs as $part) {
            $dir .= $part;
            
            if (!is_dir($dir) && strlen($dir) > 0) {                
                mkdir($dir);
            }
            
            $dir .= '/';
        }
    }
    
    function urlsize($url) {
		$parsed_url = parse_url($url);

		$sch = $parsed_url['scheme'];
		
        if (($sch != "http") && ($sch != "https") && ($sch != "ftp") && ($sch != "ftps")) {
            return false;
        }
        if (($sch == "http") || ($sch == "https")) {
            $headers = get_headers($url, 1);
            if ((!array_key_exists("Content-Length", $headers))) { return false; }
            return $headers["Content-Length"];
        }
        if (($sch == "ftp") || ($sch == "ftps")) {
            $server = parse_url($url, PHP_URL_HOST);
            $port = parse_url($url, PHP_URL_PORT);
            $path = parse_url($url, PHP_URL_PATH);
            $user = parse_url($url, PHP_URL_USER);
            $pass = parse_url($url, PHP_URL_PASS);
            if ((!$server) || (!$path)) { return false; }
            if (!$port) { $port = 21; }
            if (!$user) { $user = "anonymous"; }
            if (!$pass) { $pass = "phpos@"; }
            switch ($sch) {
                case "ftp":
                    $ftpid = ftp_connect($server, $port);
                    break;
                case "ftps":
                    $ftpid = ftp_ssl_connect($server, $port);
                    break;
            }
            if (!$ftpid) { return false; }
            $login = ftp_login($ftpid, $user, $pass);
            if (!$login) { return false; }
            $ftpsize = ftp_size($ftpid, $path);
            ftp_close($ftpid);
            if ($ftpsize == -1) { return false; }
            return $ftpsize;
        }
    }
    
    function url_or_file_size($name) {
        if (file_exists($name)) {
            return filesize($name);
        }
        else {
            return urlsize($name);
        }
    }

    /**
     * This rather complicated function is designed to retrieve the mime-type 
     * of the specified file. The file may be local or a URL to a network-
     * accessible file or a byte array containing the file contents. The 
     * function operates by running the Linux command 'file' on the file
     * contents, to extract the mime-type.
     *
     * @param $file     The path to the file or a file array.
     */
    function auto_detect_mime_type($file, $file_is_contents = true) {
        $tmpfname = tempnam("/tmp", "mime_type_file");

        $handle = fopen($tmpfname, "w");
        
        if (!$file_is_contents) {
            fwrite($handle, file_get_contents($file));
        }
        else {
            fwrite($handle, $file);
        }        
        
        fflush($handle);
        fclose($handle);
        
        $mime_type = exec("file -i -b $tmpfname");
        
        unlink($tmpfname);
        
        return $mime_type;
    }
    
    function expire_page_immediately() {
        if (isset($_SERVER["HTTPS"])) {
            /**
             * We need to set the following headers to make downloads work using IE in HTTPS mode.
             */
            header("Pragma: ");
            header("Cache-Control: ");
            header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
            header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
            header("Cache-Control: no-store, no-cache, must-revalidate"); // HTTP/1.1
            header("Cache-Control: post-check=0, pre-check=0", false);
        }
        else {
            header("Cache-Control: no-cache, must-revalidate");
            header("Pragma: no-cache");
        }
        
    }
    
    function send_file_to_browser($file_path, $file_contents = null, $opt_mime_type = null, $send_mode = "inline") {
        ini_set("zlib.output_compression", "Off");
        
        if ($file_contents == null) {
            $file_contents = file_get_contents($file_path);
        }

		$mime_type = $opt_mime_type;

        if ($opt_mime_type == null) {

			$matches = array();
			
			if (preg_match('/^.+\.([^.]+)$/', $file_path, $matches)) {	
				$ext = strtolower($matches[1]);
						
	            // Hand-coding for some file types:
	            if ($ext == 'jnlp') {
	                $mime_type = "application/x-java-jnlp-file";
	            }
	            else if ($ext == 'gif') {
	                $mime_type = "image/gif";
	            }
				else if ($ext == 'jar') {
					$mime_type = "application/java-archive";
				}
				else if ($ext == 'png') {
					$mime_type = "image/png";
				}
				else if ($ext == 'jpg' || $ext == 'jpeg') {
					$mime_type = "image/jpeg";
				}
	        }
	
	 		if ($mime_type == null){
                // Auto-detection of mime-type:
               $mime_type = auto_detect_mime_type($file_contents);
            }
        }
        
        if (strpos($file_path, '/')) {
            $name = basename($file_path);
        }
        else {
            $name = $file_path;
        }
        
        $file_size = strlen($file_contents);

        expire_page_immediately();
        
        header("Content-Type: $mime_type");
        header("Content-Disposition: $send_mode; filename=\"".trim(htmlentities($name))."\"");
        header("Content-Description: ".trim(htmlentities($name)));
        header("Content-Length: $file_size");
        header("Content-Transfer-Encoding: binary");
        header("Connection: close");
        
        print($file_contents);
        
        flush();
    }
    
    function get_file_extension($thefile) {
        if (strpos($thefile, '.') === false) {
            return '';
        }
        else {
            return strtolower(substr($thefile, strrpos($thefile, '.') + 1));
        }
    }
    
    function remove_file_extension($thefile) {
        if (strpos($thefile, '.') === false) {
            return $thefile;
        }
        else {
            return substr($thefile, 0, strrpos($thefile, '.'));
        }
    }

?>