<?php       
    /*
    
    A very simple, one-file password protection mechanism that uses cookies.
    If the user hasn't logged in, the script will prompt the user to login.
    
    The script expects the following to be defined in login-info.php:
    
        ADMIN_USERNAME      The username of the administrator.
        ADMIN_PASSWORD      The password of the administrator.
    
    */
    session_start(); 
    
    include_once("login-info.php");
    
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

        if (cookie_var_get("username") !== "") {
            $username      = cookie_var_get("username");
            $password_hash = cookie_var_get("password_hash");
        }
        if (isset($_POST["username"])) { 
            $username      = $_POST["username"];
            $password      = $_POST["password"];
            $password_hash = md5($password);
        }
        
        if ($username == ADMIN_USERNAME && $password_hash == md5(ADMIN_PASSWORD)) {
            cookie_var_store("username",      $username);
            cookie_var_store("password_hash", $password_hash);
        }
        else {
            ?>
            <html>
            <title>Phet Admin Control Panel Login Form</title>
            <body>
            In order to access these pages, you must enter the proper username and password.<BR>
            <form method="post" action="index.php">
            Username: <input type="text" name="username" size="20"><BR>
            Password: <input type="password" name="password" size="15"><BR> 
            <input type="Submit" value="Log In">
            </form>
            </body>
            </html>
            <?php
            exit;
        }
    }
    
    do_authentication();
?>