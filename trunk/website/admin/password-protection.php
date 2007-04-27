<?php       
    /*
    
    A very simple, one-file password protection mechanism that uses cookies.
    If the user hasn't logged in, the script will prompt the user to login.
    
    */
    session_start(); 
    
    include_once("contrib-utils.php");
    include_once("web-utils.php");
    
    function logout() {
        setcookie("username");
        setcookie("password_hash");        
    }
    
    function do_authentication() {    
        $username      = "";
        $password      = "";
        $password_hash = "";
        
        $user_entered_password = false;

        if (cookie_var_get("username") !== "") {
            $username      = cookie_var_get("username");
            $password_hash = cookie_var_get("password_hash");
            
            $user_entered_password = true;
        }
        if (isset($_POST["username"])) { 
            $username      = $_POST["username"];
            $password      = $_POST["password"];
            $password_hash = md5($password);
            
            $user_entered_password = true;
        }
        
        if (contributor_is_valid_admin_login($username, $password_hash)) {
            cookie_var_store("username",      $username);
            cookie_var_store("password_hash", $password_hash);
        }
        else {  
            $optional_message = null;
                      
            if ($user_entered_password) {
                $optional_message = "<p>The email and/or password you entered is incorrect. If you entered 
                                    the correct email address, please check your email now for a password reminder.</p>";
            }
            
            print_login_form($optional_message);
            
            contributor_send_password_reminder($username);
                        
            exit;
        }
    }
    
    do_authentication();
?>