<?php
    include_once("sys-utils.php");

	function web_get_real_path($path) {
		return preg_replace('/[^\/]+\/\.\.\//i', '', $path);
	}
    
    function web_create_random_password($length = 7) {
        $chars = "abcdefghijkmnopqrstuvwxyz023456789";
        
        srand((double)microtime()*1000000);
        
        $pass = '';

        for ($i = 0; $i < $length; $i++) {
            $num  = rand() % 33;
            $tmp  = substr($chars, $num, 1);
            $pass = $pass . $tmp;
        }

        return $pass;
    }

    function generate_check_status($item_num, $checked_item_num) {
        if ($checked_item_num == null && $item_num == "0" || $item_num == $checked_item_num) return "checked";
    
        return " ";
    }
    
    function force_redirect($url, $timeout = 0, $die = true) { 
        print "<meta http-equiv=\"Refresh\" content=\"$timeout;url=$url\">";
    }
    
    // function url_exists($url) {
    //     if (is_array(get_headers($url))) {			
    //         return true;
    //     }
    //     else {
    //         return false;
    //     }
    // }

	function file_or_url_exists($file) {
		if ($file == null || trim($file) == '') return false;
		
		if (file_exists($file)) return true;
		
		return url_exists($file);
	}

	/**
	 * From the header information, check that we haven't received a 404 (page not found) error
	 *
	 * @param string $file
	 * @return bool true of 404 is not in the header
	 */
    function url_exists($file) {
        $file_headers = get_headers($file);

        if (!$file_headers) return false;

        if (preg_match('/\b404\b/', $file_headers[0])) {
            $exists = false;
        }
        else {
            $exists = true;
        }
        
        return $exists;
	}

	/**
	 * Markup a string for HTML display using htmlentities(html_entity_decode()))
	 *
	 * @param string $string
	 * @return string - string formatted for HTML display
	 */
    function format_string_for_html($string) {
        return htmlentities(html_entity_decode($string));
    }
    
    /**
     * Markup a string or an array of strings in HTML.  Ultimately calls format_string_for_html(). 
     * 
     * @param string or string array $array string(s) to format for displaying in html
     * @return string or string array string(s) formatted in html 
     */
    function format_for_html($array) {
        if (is_array($array)) {
            $clean = array();
        
            foreach($array as $key => $value) {
                // Slight speed improvement
                if (is_array($value)) {
                    $clean["$key"] = format_for_html("$value");
                }
                else {
                    $clean["$key"] = format_string_for_html($value);
                }
            }
        
            return $clean;
        }
        else {
            return format_string_for_html($array);
        }
    }

    /**
     * Parse a name and return an array containing specific info, such as full_name, last name, etc.
     * 
     * @param string Name to parse
     * @return array Array with keys detailing information about the name
     */
    function parse_name($name) {
        $parsed = array();

        $parsed['full_name'] = $name;

        $matches = array();

        if (preg_match('/([a-zA-Z][a-zA-Z]+) +(([a-zA-Z ])+\.+ +)?([^.]+)$/i', $name, $matches) == 1) {    
            $parsed['first_name']   = trim($matches[1]);
            $parsed['last_name']    = trim($matches[4]);

            $exploded = preg_split('/ +/', $parsed['last_name']);

            if ($matches[3] !== '') {
                $parsed['middle_initial'] = $matches[3][0];
            }
            else {
                if (count($exploded) == 2) {
                    if (strlen($exploded[0]) >= 4) {
                        $parsed['middle_name']      = $exploded[0];
                        $parsed['middle_initial']   = strtoupper($parsed['middle_name'][0]);
                        $parsed['last_name']        = $exploded[1];
                    }
                }
                else if (count($exploded) > 2) {
                    $parsed['last_name'] = $exploded[count($exploded) - 1];
                }
            }
        }
        else {
            if (strlen(trim($name)) == 0) {
                $parsed['first_name'] = 'John';
                $parsed['last_name']  = 'Doe';
            }
            else {
                $parsed['first_name'] = '';
                $parsed['last_name']  = $name;
            }
        }

        $parsed['first_initial'] = strlen($parsed['first_name']) > 0 ? strtoupper($parsed['first_name'][0]) : '';
        $parsed['last_initial']  = strtoupper($parsed['last_name'][0]);

        return $parsed;
    }

	function array_remove($a_Input, $m_SearchValue) {
	    $a_Keys = array_keys($a_Input, $m_SearchValue);
	    foreach($a_Keys as $s_Key) {
	        unset($a_Input[$s_Key]);
	    }
	    return $a_Input;
	}
    
    function get_script_param($param_name, $default_value = "") {
        if (isset($_REQUEST['sim_id'])) {
            return $_REQUEST['sim_id'];
        }
        
        return $default_value;
    }

    function gather_globals_into_array($prefix = '') {
        $array = array();
        
        foreach($GLOBALS as $key => $value) {
            if ($prefix == '' || strstr("$key", $prefix) == "$key") {
                $array["$key"] = "$value";
            }
        }
        
        return $array;
    }
    
    function gather_script_params_into_array($prefix = '') {
        $array = array();
        
        foreach($_REQUEST as $key => $value) {
            if ($prefix == '' || strstr("$key", $prefix) == "$key") {
                $array["$key"] = "$value";
            }
        }
        
        return $array;
    }
    
    function gather_array_into_globals($array) {
        foreach($array as $key => $value) {
            $GLOBALS["$key"] = format_for_html("$value");
        }
    }
    
    function gather_script_params_into_globals() {     
        gather_array_into_globals($_REQUEST);
    }
    
    function get_code_to_create_variables_from_array($array) {
        $code = '';
        
        foreach($array as $key => $value) {
            $value = str_replace('\\', '',     $value);
            $value = str_replace("'",  "\\'",  $value);
            
            $code .= "\$$key = '$value'; ";
        }
        
        return $code;
    }
    
    function get_code_to_create_variables_from_script_params() {
        return get_code_to_create_variables_from_array($_REQUEST);
    }
    
    function convert_array_to_comma_list($array) {
        $list = '';
        
        $is_first = true;
        
        foreach($array as $element) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $list .= ', ';
            }
            
            $list .= "$element";
        }
        
        return $list;
    }

	function format_greek_symbols($input) {
		$letter_to_code = array(
			'$Alpha$' 		=> '&Alpha;',
			'$Beta$' 		=> '&Beta;',
			'$Gamma$' 		=> '&Gamma;',
			'$Delta$' 		=> '&Delta;',
			'$Epsilon$' 	=> '&Epsilon;',
			'$Zeta$' 		=> '&Zeta;',
			'$Eta$' 		=> '&Eta;',
			'$Theta$' 		=> '&Theta;',
			'$Iota$' 		=> '&Iota;',
			'$Kappa$' 		=> '&Kappa;',
			'$Lambda$' 		=> '&Lambda;',
			'$Mu$' 			=> '&Mu;',
			'$Nu$' 			=> '&Nu;',
			'$Xi$' 			=> '&Xi;',
			'$Omicron$' 	=> '&Omicron;',
			'$Pi$' 			=> '&Pi;',
			'$Rho$' 		=> '&Rho;',
			'$Sigma$' 		=> '&Sigma;',
			'$Tau$' 		=> '&Tau;',
			'$Upsilon$' 	=> '&Upsilon;',
			'$Phi$' 		=> '&Phi;',
			'$Chi$' 		=> '&Chi;',
			'$Psi$' 		=> '&Psi;',
			'$Omega$' 		=> '&Omega;',
			'$alpha$' 		=> '&alpha;',
			'$beta$' 		=> '&beta;',
			'$gamma$' 		=> '&gamma;',
			'$delta$' 		=> '&delta;',
			'$epsilon$' 	=> '&epsilon;',
			'$zeta$' 		=> '&zeta;',
			'$eta$' 		=> '&eta;',
			'$theta$' 		=> '&theta;',
			'$iota$' 		=> '&iota;',
			'$kappa$' 		=> '&kappa;',
			'$lambda$' 		=> '&lambda;',
			'$mu$' 			=> '&mu;',
			'$nu$' 			=> '&nu;',
			'$xi$' 			=> '&xi;',
			'$omicron$' 	=> '&omicron;',
			'$pi$'			=> '&pi;',
			'$rho$' 		=> '&rho;',
			'$sigmaf$' 		=> '&sigmaf;',
			'$sigma$' 		=> '&sigma;',
			'$tau$' 		=> '&tau;',
			'$upsilon$' 	=> '&upsilon;',
			'$phi$' 		=> '&phi;',
			'$chi$' 		=> '&chi;',
			'$psi$' 		=> '&psi;',
			'$omega$' 		=> '&omega;'
		);
		
		foreach($letter_to_code as $letter => $code) {
			if (strpos($input, $letter)) { // <= Fast test to avoid slowness of regexp
				$input = preg_replace('/\b'.$letter.'\b/', $code, $input);
			}
		}
		
		return $input;
	}
    
    function print_comma_list_as_bulleted_list($comma_list) {
		if (!is_string($comma_list) || strlen(trim($comma_list)) == 0) return;
		
        print "<ul>";
        
        if (strstr($comma_list, '*')) {
            $list = preg_split('/ *\\* */', $comma_list);
        }
        else {
            $list = preg_split('/ *, */', $comma_list);
        }
        
        foreach($list as $item) {    
            if (trim($item) != '') {
                print "<li>$item</li>";
            }
        }
        
        print "</ul>";
    }

    function print_editable_area($control_name, $contents, $rows = "20", $cols = "40") {
        print("<textarea name=\"$control_name\" rows=\"$rows\" cols=\"$cols\">$contents</textarea>");
    }
    
    function print_captioned_editable_area($caption, $control_name, $contents, $rows = "20", $cols = "40") {
        print("<p>$caption</p><p>");
            
        print_editable_area($control_name, $contents, $rows, $cols);
        
        print("</p>");
    }
    
    function print_text_input($control_name, $control_value, $width = 20) {
        print <<<EO_PRINT_TEXT_INPUT
            <input type="text" name="$control_name" value="$control_value" size="$width"/>
EO_PRINT_TEXT_INPUT;

    }
    
    function print_password_input($control_name, $control_value, $width = 20) {
        print <<<EO_PRINT_PASSWORD_INPUT
            <input type="password" name="$control_name" value="$control_value" size="$width"/>
EO_PRINT_PASSWORD_INPUT;

    }
    
    function print_hidden_input($control_name, $control_value) {
        print <<<EO_PRINT_HIDDEN_INPUT
            <input type="hidden" name="$control_name" value="$control_value"/>
EO_PRINT_HIDDEN_INPUT;
        
    }
    
    function get_upload_path_prefix_from_name($name) {
        $matches = array();
        
        preg_match('/^(.+?)((_url)?)$/', $name, $matches);
        
        $path_name = $matches[1];
        
        $path_prefix = preg_replace('/_/', '/', $path_name);
        
        //print "Path prefix = $path_prefix\n";
        
        return $path_prefix;
    }
    
    function print_captioned_url_upload_control($caption, $control_name, $contents, $rows = "20", $cols = "40") {
        print("<p>$caption<p/>");
        
        print_editable_area($control_name, $contents, $rows, $cols);
        print("<p>Or upload a file: <input name=\"${control_name}_file_upload\" type=\"file\" /></p>");
    }
    
    function process_url_upload_control($control_name, $value) {
        $files_key = "${control_name}_file_upload";
        
        if (isset($_FILES[$files_key])) {
            //print ("User uploading for $control_name");

            $upload_path_prefix = get_upload_path_prefix_from_name($control_name);
            
            $file_user_name = $_FILES[$files_key]['name'];
            $file_tmp_name  = $_FILES[$files_key]['tmp_name'];
            
            // If the user uploads a file, generate a URL relative to this directory:
            $target_name = basename($file_user_name);
            $target_dir  = dirname(__FILE__)."/uploads/$upload_path_prefix"; 
            $target_path = "${target_dir}/${target_name}"; 
             
            if ($target_name !== "" && $target_name !== null) {                
                mkdirs($target_dir);
                
                //print("\nTarget name = $target_name; target path = $target_path\n");
             
                if (move_uploaded_file($file_tmp_name, $target_path)) {                
                    return "$upload_path_prefix/$target_name";
                }
            }
        }
        
        return $value;
    }    
    
    function remove_script_param_from_url($param_name, $url) {
        $param_name = preg_quote($param_name);
        
        return preg_replace("/(&$param_name=[^&]+)|((?<=\\?)$param_name=[^?&]+&?)/i", '', $url);
    }

	function remove_all_script_params_from_url($url) {
		return preg_replace("/(&.+$)/i", '', $url);
	}
    
    function web_encode_string($string) {
        $string = str_replace(' ',              '_',    $string);
        $string = str_replace('&amp;',          'and',  $string);        
        $string = str_replace('&',              'and',  $string);
        $string = preg_replace('/[^\\w_\\d]+/',  '',     $string);
        
        return $string;
    } 
    
    function resolve_url_upload($url) {
		if ($url == null || trim($url) == '') return '';
		
        if (preg_match('/http.*/i', $url) == 1) {
            // URL is absolute:
            return $url;
        }
        else {
            // URL is relative to this directory:        
            
            // Can't allow user to access files outside /uploads/ directory:
            $url = preg_replace('/\.+/', '.', $url);
            
            $resolved_path = dirname(__FILE__)."/uploads/${url}";
            
            return $resolved_path;
        }
    }
    
    /**
     * This function displays a randomized slideshow.
     *
     */
    function display_slideshow($thumbnails, $width, $height, $prefix = "", $delay="5000") {
        /*

        Instead of using Flash to display random slideshow, our strategy is to use PHP 
        to generate a JavaScript script that randomly cycles through the images. This 
        way, the user does not need Flash in order to correctly view the home page.

        */ 

        print <<<EO_DISPLAY_SLIDESHOW_1
            <script type="text/javascript">
			/*<![CDATA[*/
            	var delay=$delay;
           		var curindex=0;

            var randomimages=new Array();

EO_DISPLAY_SLIDESHOW_1;

        $index = 0;

        print "\n";

        foreach($thumbnails as $thumbnail) {
            print "randomimages[$index] = \"$thumbnail\";\n";

            $index++;
        }

        print <<<EO_DISPLAY_SLIDESHOW_2
            	var preload=new Array();

            	for (n=0;n<randomimages.length;n++) {
            		preload[n]=new Image();
            		preload[n].src=randomimages[n];
            	}

            	document.write('<img name="defaultimage" width="$width" height="$height" src="'+randomimages[Math.floor(Math.random()*(randomimages.length))]+'">');

            	function rotateimage() {
                	curindex=Math.floor(Math.random()*(randomimages.length));

                	document.images.defaultimage.src=randomimages[curindex];
            	}

            	setInterval("rotateimage()", delay);
			/*]]>*/
            </script>
EO_DISPLAY_SLIDESHOW_2;
    }
    
    function cookie_var_clear($name) {
        setcookie("$name", '', time() - 60*60*24*365*10);
        unset($_SESSION[$name]);
    }
    
    function cookie_var_store($name, $var) {        
        cookie_var_clear($name);
        
        // setcookie("$name", $var);
        
        $_SESSION[$name] = $var;
    }
    
    function cookie_var_is_stored($name) {
        return cookie_var_get($name) !== '';
    }
    
    function cookie_var_get($name) {
        $value = '';
        
        // if (isset($_COOKIE["$name"])) {
        //             $value = $_COOKIE["$name"];
        //         }
        //         else 
        if (isset($_SESSION["$name"])) {
            $value = $_SESSION["$name"];
        }

        //cookie_var_store($name, $value);
        
        return $value;
    }

	function web_detect_browser() {
		$agent = isset($_SERVER['HTTP_USER_AGENT']) ? strtolower($_SERVER['HTTP_USER_AGENT']) : '';
		
		if (strpos($agent, 'msie')) {
			return "internet-explorer";
		}
		else if (strpos($agent, 'safari')) {
			return "safari";
		}
		else if (strpos($agent, 'gecko')) {
			return "mozilla";
		}
		else {
			return "internet-explorer";
		}
	}
    
    function string_starts_with($string, $prefix) {
        return strstr($string, $prefix) == $string;
    }
    
    function get_self_url() {
        return $_SERVER['REQUEST_URI'];
    }
    
    function is_email($email) {
        $p  = '/^[a-z0-9!#$%&*+-=?^_`{|}~]+(\.[a-z0-9!#$%&*+-=?^_`{|}~]+)*';
        $p .= '@([-a-z0-9]+\.)+([a-z]{2,3}';
        $p .= '|info|arpa|aero|coop|name|museum)$/ix';
        
        return preg_match($p, $email) == 1;
    }
    
    function convert_comma_list_into_linked_keyword_list($list, $print_commas = false) {  
        global $referrer;
          
        $xml = '<div class="keywordlist">';
        
        $comma_xml = '';
        
        if ($print_commas) {
            $comma_xml = ', ';
        }
        
        $is_first = true;
        
        foreach(preg_split('/( *, *)/i', $list) as $keyword) {
            $keyword = trim($keyword);
            
            if ($is_first) {
                $is_first = false;
            }
            else {
                $xml .= $comma_xml;
            }
            
            $xml .= '<a href="'.SITE_ROOT.'simulations/search.php?search_for='.urlencode($keyword).'&amp;referrer='.$referrer.'">'.$keyword.'</a>';
        }
        
        $xml .= '</div>';
        
        return $xml;
    }
    
    function php_array_to_javascript_array($array, $baseName) {
        $code = ($baseName . " = new Array(); \r\n ");    

        reset ($array);

        while (list($key, $value) = each($array)) {
            if (is_numeric($key)) {
                $outKey = "[" . $key . "]";
            }
            else {
                $outKey = "['" . $key . "']";
            }

            if (is_array($value)) {
                $code .= php_array_to_javascript_array($value, $baseName . $outKey);
            } 
            else { 
                $code .= ($baseName . $outKey . " = ");      

                if (is_string($value)) {
                    $code .= ("'" . $value . "'; \r\n ");
                }
                else if ($value === false) {
                    $code .= ("false; \r\n");
                }
                else if ($value === NULL) {
                    $code .= ("null; \r\n");
                }
                else if ($value === true) {
                    $code .= ("true; \r\n");
                } 
                else {
                    $code .= ($value . "; \r\n");
                }
            }
        }
       
       return $code;
    }
    
    function print_string_encoded_checkbox($group_prefix, $encoded_string, $read_only = false) {
        static $checkbox_id;
        static $last_group_prefix = null;
        
        if ($read_only) {
            $read_only_status = 'onclick="javascript:return false;" disabled="disabled" ';
        }
        else {
            $read_only_status = '';
        }
        
        if ($last_group_prefix !== $group_prefix) {
            $checkbox_id = 1;
        }
        
        $prefix = "checkbox_${group_prefix}_${checkbox_id}";
        
        $quoted_prefix = preg_quote($prefix);
        
        $pattern = '/.*\['.$quoted_prefix.'\].*$/i';
        
        $checked_status = '';
        
        if (preg_match($pattern, $encoded_string) == 1) {
            $checked_status .= 'checked="checked"';
        }
        
        print <<<EOT
        <input name="${prefix}" type="checkbox" id="${prefix}" value="${prefix}" $checked_status $read_only_status />
EOT;

        $checkbox_id++;        
        $last_group_prefix = $group_prefix;
    }   
    
    /**
     * This function creates an abbreviation of a phrase. It can be useful 
     * when summarizing information that is too large to fit in a cell.
     * 
     * @param string    The phrase to abbreviate.
     */
    function abbreviate($string) {
        include_once("spell.php");
        
        $words = preg_split('/([\s\-])/', $string, -1, PREG_SPLIT_NO_EMPTY | PREG_SPLIT_DELIM_CAPTURE);
        
        // print "Words: ";
        // print_r($words);
        // print("</br>");
        
        if (count($words) > 1 || strlen($string) >= 8) {
            $abbrev = '';
        
            $last_word_dash = false;
            
            foreach($words as $word) {
                if ($word == '-') {
                    $abbrev .= '-';
                    
                    $last_word_dash = true;
                }
                else {
                    if (preg_match('/^\s+$/', $word) == 0) {
                        $first_word  = $word;
                        $second_word = '';

                        for ($i = strlen($word) - 2; $i > 1; $i--) {
                            $check_first_word  = substr($word, 0, $i);
                            $check_second_word = substr($word, $i);

                            if (spell_is_valid_word($check_first_word) && spell_is_valid_word($check_second_word)) {
                                $first_word  = $check_first_word;
                                $second_word = $check_second_word;                    

                                break;
                            }
                        }

                        if ($first_word !== '' || !$last_word_dash) {
                            if ($first_word !== '') {
                                $abbrev .= strtoupper($first_word[0]);
                            }
                            if ($second_word !== '') {
                                $abbrev .= strtoupper($second_word[0]);
                            }
                        }
                        else {
                            $w = ucfirst($second_word);

                            $first_letter_vowel = preg_match('/^[AEIOU].*$/i', $w) == 1;
                            
                            $matches = array();
                            
                            if ($first_letter_vowel) {
                                preg_match('/^([AEIOU]+[^AEIOU]+).*$/i', $w, $matches);
                            }
                            else {
                                preg_match('/^([^AEIOU]+[AEIOU]+[^AEIOU]+).*$/i', $w, $matches);
                            }

                            $abbrev .= $matches[1];
                        }
                    }
                    
                    if ($word[strlen($word) - 1] == 's' && spell_is_valid_word(substr($word, 0, strlen($word) - 1))) {
                        $abbrev .= 's';
                    }
                    
                    $last_word_dash = false;
                }
            }
            
            return trim($abbrev);
        }
        else {
            return $string;
        }
    }
    
    function generate_encoded_checkbox_string($group_prefix) {
        $encoded_string = '';
        
        $escaped_group_prefix = preg_quote($group_prefix);
        
        foreach($_REQUEST as $key => $value) {
            $matches = array();
            
            if (preg_match("/(checkbox_${escaped_group_prefix}_[0-9]+)$/i", "$key", $matches) == 1) {
                $prefix = $matches[1];
                
                $encoded_string .= "[$prefix]";
            }
        }
        
        return $encoded_string;
    }  

    /**
     * Parses the string for the form: multiselect_([a-zA-Z0-9_]+)_id_([0-9]+)
     *
     * @param string $multiselect_control_name string to parse
     * @return false with no match, else an array with the table_name and row_id
     */
    function parse_multiselect_control($multiselect_control_name) {
        $matches = array();

        if (preg_match('/multiselect_([a-zA-Z0-9_]+)_id_([0-9]+)$/i', $multiselect_control_name, $matches) !== 1) {
            return false;
        }

        $parsed = array();

        $parsed['table_name'] = $matches[1];
        $parsed['row_id']	  = $matches[2];

        return $parsed;
    }

    function create_multiselect_control_name($table_name, $row_id) {
        return "multiselect_${table_name}_id_${row_id}";
    }
    
    function is_multiple_selection_control($control_name) {
		return parse_multiselect_control($control_name) !== false;
    }
    
    function create_deletable_item_control_name($name, $id) {
        return "keep_${name}_id_${id}";
    }

    /**
     * Parses the named control and returns the id section
     *
     * @param string $control_name name of control
     * @return int on successful parse, false on failure
     */
    function get_deletable_item_control_id($control_name) {
        $matches = array();
        
        if (preg_match('/keep_([a-zA-Z0-9_]+)_id_([0-9]+)$/i', $control_name, $matches) == 1) {
            return $matches[2];
        }
        else {
            return false;
        }
    }

    /**
     * Parse the name to determine if a control is deletable, form: keep_*_id_* means it IS deletable
     *
     * @param string $control_name name of the control
     * @return true if the form matches, false otherwise
     */
    function is_deletable_item_control($control_name) {
        if (preg_match('/keep_([a-zA-Z0-9_]+)_id_([0-9]+)$/i', $control_name) == 1) {
            return true;
        }
        else {
            return false;
        }
    }    

    function print_deletable_list($select_user_name, $selections_array) {
        print_multiple_selection($select_user_name, $selections_array, $selections_array, false, false, $name_prefix = "dl");
    }

    function print_multiple_selection($select_user_name, $options_array, $selections_array = array(), $should_validate = true, $print_select = true, $name_prefix = "ms") {
        static $has_printed_javascript = false;
        static $ms_id_num = 1;
        
        $name = "${name_prefix}_${ms_id_num}";
        
        ++$ms_id_num;
        
        $text_to_identifier = array();
        
        $options = '<option value="" selected="selected">Select '.$select_user_name.':</option>';
        
        foreach($options_array as $identifier => $text) {
            // TODO: On the "teacher_ideas/contribute.php" the identifiers
            // come in already formatted in HTML and don't need this.  Check to
            // see if others rely on this method.
            //$text = html_entity_decode($text);

            $options .= "<option value=\"$identifier\">$text</option>";

            $text_to_identifier["$text"] = "$identifier";
        }
        
        $selections = '';
        
        static $child_id_index = 1;
        
        $list_id = "${name}_list_uid";

		// Create some javascript code to add all the initial entries to the multiselection list:
		if (!$should_validate) {
			$script_creation_code = '';
		}
		else {
        	$script_creation_code = "ms_mark_as_invalid(document.getElementById('$list_id'));";
		}
		
		$should_invalidate_on_empty = $print_select ? 'true' : 'false';

        foreach($selections_array as $text) {
            
            $text = html_entity_decode($text);

            if (isset($text_to_identifier["$text"])) {
                $identifier = $text_to_identifier["$text"];
            }
            else {
                $identifier = "$text";
            }

            $script_creation_code .= "ms_add_li('$name', '$list_id', '$text', '$identifier', $should_invalidate_on_empty);";
        }
        
        $select_id   = "${name}_uid";
        $select_name = "${name}_select";
        
        if (!$has_printed_javascript) {
            $has_printed_javascript = true;

			// TODO: remove this when JS validation is done
			// Moving this to another file, but keeping here for reference
            print <<<EOT
            <script type="text/javascript">
                /* <![CDATA[ */

                var child_id_index = $child_id_index;

				function ms_get_parent_table_row(node) {
					while (node && node.tagName != 'TR') {
						node = node.parentNode;
					}
					
					return node;
				}
				
				function ms_get_first_td(node) {
					var parentNode = ms_get_parent_table_row(node);
					
					for (var i = 0; i < parentNode.childNodes.length; i++) {
						if (parentNode.childNodes[i].tagName == 'TD') {
							return parentNode.childNodes[i];
						}
					}
				}
				
				function ms_mark_as_valid(node) {
					var label_td = ms_get_first_td(node);
					
					if (label_td) {	
						label_td.className = 'valid';
					}
				}
				
				function ms_mark_as_invalid(node) {
					var label_td = ms_get_first_td(node);
					
					if (label_td) {
						label_td.className = 'invalid';
					}
				}
				
				function ms_has_any_li(node) {
					for (var i = 0; i < node.childNodes.length; i++) {
						if (node.childNodes[i].tagName == 'LI') {
							return true;
						}
					}
					
					return false;
				}

                function ms_remove_li(id, child_id, invalidate_on_empty) {					
                    var Parent = document.getElementById(id);
                    var Child  = document.getElementById(child_id);

                    Parent.removeChild(Child);

					if (invalidate_on_empty == true) {
						if (!ms_has_any_li(Parent)) {
							ms_mark_as_invalid(Parent);
						}
					}

                    return false;
                }

                function ms_add_li(basename, list_id, text, name, invalidate_on_empty) {
                    if (invalidate_on_empty == undefined) invalidate_on_empty = true;

                    var Parent = document.getElementById(list_id);

                    ms_mark_as_valid(Parent);

                    var li_children = Parent.getElementsByTagName("li");

                    for(var i = 0, li_child; li_child = li_children[i]; i++) {
                        var input_child = li_child.getElementsByTagName("input")[0];

                        if (input_child.name == name) {
                            return false;
                        }
                    }

                    var NewLI = document.createElement("li");

                    NewLI.id        = "child_" + basename + "_" + child_id_index;                    
                    NewLI.innerHTML = "[<a href=\"javascript:void(0)\" onclick=\"ms_remove_li('" + list_id + "','" + NewLI.id + "'," + invalidate_on_empty + ")\">remove</a>] " + text +
                                      "<input type=\"hidden\" name=\"" + name + "\" value=\"" + text + "\" />";

                    Parent.appendChild(NewLI);

                    child_id_index++;
                }

                function ms_on_change(basename, list_id, dropdown, invalidate_on_empty) {
                    var index  = dropdown.selectedIndex;

                    var text   = dropdown.options[index].text;
                    var value  = dropdown.options[index].value;

                    if (value && value != '') {
                        ms_add_li(basename, list_id, text, value, invalidate_on_empty);
                    }

                	return false;
                }

                /* ]]> */
            </script>
EOT;
        }        

        if ($print_select) {
            print <<<EOT
                  <select name="$select_name" id="$select_id" 
                      onchange="ms_on_change('$name', '$list_id', this.form.$select_name, $should_invalidate_on_empty);">
                      $options
                  </select>
EOT;
        }

        print <<<EOT
            <ul id="$list_id">
                $selections
            </ul>
EOT;

        print <<<EOT
            <script type="text/javascript">
                $script_creation_code
            </script>
EOT;
    }

    function get_global_opt($param, $opt = '') {
		if (isset($GLOBALS[$param])) return $GLOBALS[$param];
		
		return $opt;
	}

    function get_request_opt($param, $opt = '') {
		if (isset($_REQUEST[$param])) return $_REQUEST[$param];
		
		return $opt;
	}

    function print_single_selection($select_name, $value_to_text, $selected, $other_attributes = '') {
        print <<<EOT
            <select name="$select_name" id="${select_name}_uid" $other_attributes>
EOT;

        $natural_ordering = 0;

        foreach($value_to_text as $value => $text) {
            if ($natural_ordering++ == $value) $value = $text;

            if ($text == $selected || $value == $selected) {
                $is_selected = 'selected="selected"';
            }
            else {
                $is_selected = '';
            }

            $formatted_text = format_for_html($text);
            print <<<EOT
                <option value="$value" $is_selected>$formatted_text</option>
EOT;
        }

        print <<<EOT
            </select>
EOT;
    }

    function print_checkbox($checkbox_name, $checkbox_text, $checkbox_value) {
        $is_checked = $checkbox_value == "1" ? "checked=\"checked\"" : "";
        
        print <<<EOT
                <input type="hidden"   name="$checkbox_name" value="0" />
                <input type="checkbox" name="$checkbox_name" value="1" id="${checkbox_name}_uid" $is_checked>$checkbox_text</input>
EOT;
    }
    
    function web_close_unclosed_tags($html) {
        preg_match_all("#<([a-z]+)( .*)?(?!/)>#iU",$html,$result);
        
        $openedtags=$result[1];

        preg_match_all("#</([a-z]+)>#iU",$html,$result);
        
        $closedtags=$result[1];
        $len_opened = count($openedtags);

        if (count($closedtags) == $len_opened){
            return $html;
        }

        $openedtags = array_reverse($openedtags);

        for($i=0;$i < $len_opened;$i++) {
            if (!in_array($openedtags[$i],$closedtags)){
                $html .= '</'.$openedtags[$i].'>';
            } 
            else {
                unset($closedtags[array_search($openedtags[$i],$closedtags)]);
            }
        }
        return $html;
    }

    
    function web_utf16_decode( $str ) {
        if( strlen($str) < 2 ) return $str;
        $bom_be = true;
        $c0 = ord($str{0});
        $c1 = ord($str{1});
        if( $c0 == 0xfe && $c1 == 0xff ) { $str = substr($str,2); }
        elseif( $c0 == 0xff && $c1 == 0xfe ) { $str = substr($str,2); $bom_be = false; }
        $len = strlen($str);
        $newstr = '';
        for($i=0;$i<$len;$i+=2) {
            if( $bom_be ) { $val = ord($str{$i})   << 4; $val += ord($str{$i+1}); }
            else {        $val = ord($str{$i+1}) << 4; $val += ord($str{$i}); }
            $newstr .= ($val == 0x228) ? "\n" : chr($val);
        }
        return $newstr;
    }

?>
