<?php
    include_once("sys-utils.php");
    include_once("contrib-utils.php");
    
    function web_create_random_password() {
        $chars = "abcdefghijkmnopqrstuvwxyz023456789";
        
        srand((double)microtime()*1000000);
        
        $i    = 0;
        $pass = '';

        while ($i <= 7) {
            $num  = rand() % 33;
            $tmp  = substr($chars, $num, 1);
            $pass = $pass . $tmp;
            $i++;
        }

        return $pass;
    }

    function generate_check_status($item_num, $checked_item_num) {
        if ($checked_item_num == null && $item_num == "0" || $item_num == $checked_item_num) return "checked";
    
        return " ";
    }
    
    function force_redirect($url, $timeout = 0, $die = true) { 
        print "<META http-equiv=\"Refresh\" content=\"$timeout;url=$url\">";
    }
    
    // function url_exists($url) {
    //     if (is_array(get_headers($url))) {
    //         return true;
    //     }
    //     else {
    //         return false;
    //     }
    // }
    
    function url_exists($url) {
        return true;
    }
    
    function format_for_html($string) {
        return preg_replace('/&(?!amp;)/', '&amp;', $string);
    }
    
    function get_script_param($param_name, $default_value = "") {
        if (isset($_REQUEST['sim_id'])) {
            return $_REQUEST['sim_id'];
        }
        
        return $default_value;
    }
    
    function gather_array_into_globals($array) {
        foreach($array as $key => $value) {
            $GLOBALS["$key"] = format_for_html("$value");
        }
    }
    
    function gather_script_params_into_globals() {     
        gather_array_into_globals($_REQUEST);
    }
    
    function print_comma_list_as_bulleted_list($comma_list) {
        print "<ul>";
        
        foreach(explode(',', $comma_list) as $item) {
            $trimmed_item = trim($item);
            
            print "<li>$trimmed_item</li>";
        }
        
        print "</ul>";
    }

    function print_editable_area($control_name, $contents, $rows = "20", $cols = "80") {
        print("<textarea name=\"$control_name\" rows=\"$rows\" cols=\"$cols\">$contents</textarea>");
    }
    
    function print_captioned_editable_area($caption, $control_name, $contents, $rows = "20", $cols = "80") {
        print("<p align=\"left\" class=\"style16\">$caption<br/>");
            
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
        
        print "Path prefix = $path_prefix\n";
        
        return $path_prefix;
    }
    
    function print_captioned_url_upload_control($caption, $control_name, $contents, $rows = "20", $cols = "80") {
        print("<p align=\"left\" class=\"style16\">$caption<br/>");
        
        print_editable_area($control_name, $contents, $rows, $cols);
        print("<p align=\"left\" class=\"style16\">Or upload a file: <input name=\"${control_name}_file_upload\" type=\"file\" /></p>");
        
        print("</p>");
    }
    
    function process_url_upload_control($control_name, $value) {
        $files_key = "${control_name}_file_upload";
        
        if (isset($_FILES[$files_key])) {
            print ("User uploading for $control_name");

            $upload_path_prefix = get_upload_path_prefix_from_name($control_name);
            
            $file_user_name = $_FILES[$files_key]['name'];
            $file_tmp_name  = $_FILES[$files_key]['tmp_name'];
            
            // If the user uploads a file, generate a URL relative to this directory:
            $target_name = basename($file_user_name);
            $target_dir  = dirname(__FILE__)."/uploads/$upload_path_prefix"; 
            $target_path = "${target_dir}/${target_name}"; 
             
            if ($target_name !== "" && $target_name !== null) {                
                mkdirs_r($target_dir);
                
                print("\nTarget name = $target_name; target path = $target_path\n");
             
                if (move_uploaded_file($file_tmp_name, $target_path)) {                
                    return "$upload_path_prefix/$target_name";
                }
            }
        }
        
        return $value;
    }
    
    function resolve_url_upload($url) {
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
            <script language="javascript">

            var delay=$delay
            var curindex=0

            var randomimages=new Array()

EO_DISPLAY_SLIDESHOW_1;

        $index = 0;

        print "\n";

        foreach($thumbnails as $thumbnail) {
            print "randomimages[$index] = \"${prefix}admin/get-upload.php?url=$thumbnail\"\n";

            $index++;
        }

        print <<<EO_DISPLAY_SLIDESHOW_2
            var preload=new Array()

            for (n=0;n<randomimages.length;n++) {
            	preload[n]=new Image()
            	preload[n].src=randomimages[n]
            }

            document.write('<img name="defaultimage" width="$width" height="$height" src="'+randomimages[Math.floor(Math.random()*(randomimages.length))]+'">')

            function rotateimage() {
                curindex=Math.floor(Math.random()*(randomimages.length))

                document.images.defaultimage.src=randomimages[curindex]
            }

            setInterval("rotateimage()", delay)

            </script>
EO_DISPLAY_SLIDESHOW_2;
    }
    
    function print_login_form($optional_message = null, $standard_message = "<p>Please enter your email and password.</p>", $username = '') {
        $script = get_self_url();
        
        print <<<EOT
            <form id="loginform" method="post" action="$script">
                <fieldset>
                    <legend>Log in</legend>
EOT;

        if ($optional_message !== null) {
            print "$optional_message";
        }

        print <<<EOT
                    $standard_message
                
                    <label for="email">
                        <input type="text"     name="username" tabindex="1" id="username" size="25" value="$username" />your email:
                    </label>
                
                    <label for="password">
                        <input type="password" name="password" tabindex="2" id="password" size="25"/>your password:
                    </label>
            
                    <label for="submit">
                        <input name="Submit" type="submit" id="submit" tabindex="4" value="Log in" />
                    </label>
                 </fieldset>
            </form>
EOT;
    }
    
    function cookie_var_clear($name) {
        setcookie("$name", '', time() - 60*60*24*365*10);
    }
    
    function cookie_var_store($name, $var) {        
        cookie_var_clear($name);
        
        setcookie("$name", $var);
    }
    
    function cookie_var_is_stored($name) {
        return isset($_COOKIE["$name"]);
    }
    
    function cookie_var_get($name) {
        if (!isset($_COOKIE["$name"])) {            
            return "";
        } 
        
        return $_COOKIE["$name"];
    }
    
    function get_self_url() {
        $url = basename($_SERVER['SCRIPT_NAME']);
        
        if (isset($_SERVER['QUERY_STRING'])) {
            $query_string = str_replace(array('&amp;', '&'), array('&', '&amp;'), $_SERVER['QUERY_STRING']);
            
            $url .= "?$query_string";
        }
        
        return $url;
    }
    
    function is_email($email) {
        $p  = '/^[a-z0-9!#$%&*+-=?^_`{|}~]+(\.[a-z0-9!#$%&*+-=?^_`{|}~]+)*';
        $p .= '@([-a-z0-9]+\.)+([a-z]{2,3}';
        $p .= '|info|arpa|aero|coop|name|museum)$/ix';
        
        return preg_match($p, $email) == 1;
    }

?>