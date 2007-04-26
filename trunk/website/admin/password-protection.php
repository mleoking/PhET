<?php       
    /*
    
    A very simple, one-file password protection mechanism that uses cookies.
    If the user hasn't logged in, the script will prompt the user to login.
    
    */
    session_start(); 
    
    include_once("contrib-utils.php");
    
    function logout() {
        setcookie("username");
        setcookie("password_hash");        
    }
    
    function cookie_var_store($name, $var) {        
        setcookie("$name");
        setcookie("$name", $var);
    }
    
    function cookie_var_get($name) {
        if (!isset($_COOKIE["$name"])) {            
            return "";
        } 
        
        return $_COOKIE["$name"];
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
        
        if (contributor_is_admin($username, $password_hash)) {
            cookie_var_store("username",      $username);
            cookie_var_store("password_hash", $password_hash);
        }
        else {
            ?>
            
            <table align="center" width="400">
            <tr>
                <td colspan="2">
                    <p>In order to access these pages, you must enter the proper username and password.<br></p>
                </td>
            </tr>
            
            <?php
                if ($user_entered_password) {
                    print <<<EOT
                    <tr>
                        <td colspan="2">
                            <p>
                            The username and/or password you entered is incorrect. If you entered the correct username,
                            please check your e-mail now for a password reminder.
                            </p>
                        </td>
                    </tr>
EOT;

                    contributor_send_password_reminder($username);
                }
            ?>
            <form method="post" action="index.php">
                <tr>
                    <td colspan="2" />
                </tr>
                <tr>
                    <td>Username</td>   <td><input type="text"     name="username" size="20"></td>
                </tr>
                <tr>
                    <td>Password</td>   <td><input type="password" name="password" size="20"></td>
                </tr>
                <tr>
                    <td colspan="2" />
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <input type="Submit" value="Log In">
                    </td>
                </tr>
            </form>
            
            <?php
            exit;
        }
    }
    
    do_authentication();
?>