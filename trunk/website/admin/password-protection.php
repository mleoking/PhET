<?php
    session_start();
        
    include_once("login-info.php");
    
    function cookie_var_store($name, $var) {        
        setcookie("$name");
        setcookie("$name", $var);
    }
    
    function cookie_var_get($name) {
        if (!isset($_COOKIE["$name"])) {            
            return null;
        } 
        
        return $_COOKIE["$name"];
    }

    if (cookie_var_get("username") !== null) {
        $username      = cookie_var_get("username");
        $password_hash = cookie_var_get("password_hash");
        
        if ($username !== ADMIN_USERNAME && $password_hash !== md5(ADMIN_PASSWORD)) {
            print "The username or password has expired.";
            
            exit;
        }
    }
    else if ($_POST["username"]=="") { 
?>
        <html>
        <title>Phet Admin Control Panel Login Form</title>
        <body>
        In order to access these pages, you must enter the proper username and password.<BR>
        <form method="post" action="index.php">
        Username: <input type="text" name="username" size="20"><BR>
        Password: <input type="password" name="password" size="15"><BR> 
        <input type="Submit" value="Submit">
        </form>
        </body>
        </html>

<?php
        exit;
    }
    else { 
        $username=$_POST["username"];
        $password=$_POST["password"];
        
        if ($username==ADMIN_USERNAME AND $password==ADMIN_PASSWORD) {
            $permission="yes";
        }

        if ($permission !== "yes"){
            print "The username or password you entered are invalid.";
            
            exit;
        }

        $username=$_POST["username"];
        
        session_register("permission");   
        session_register("username");
        
        cookie_var_store("username",      $username);
        cookie_var_store("password_hash", md5($password));
    }
?>