<?php
    /*
    
        This script handles user login. It's designed to be included in 
        another script (the 'including script'), to restrict access to that
        script to individuals who have a contributor account on the PhET 
        website.
        
        If this script is successfully included, it will define a global
        variable 'contributor_id', corresponding to the id of the contributor.
    
    */
    
    define("SITE_ROOT", dirname(__FILE__)."/../");

    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
        
    function print_first_time_login_form() {      
        print_login_form(null, 
            "<p>Please enter your email and password.</p>
            <p>If you don't have an account on the PhET website, please enter your email and desired password.</p>");
    }
    
    function print_retry_login_form() {        
        print_login_form(null, 
            "<p>The password you entered is incorrect. If you entered the correct email address, please check your email now for a password reminder.</p>
            <p>If you don't have an account on the PhET website, please enter your email and desired password.</p>",
             $GLOBALS['username']);
    }
    
    function print_not_an_email_login_form() {        
        print_login_form(null, 
            "<p>The email address you entered is not a valid email address.</p>
            <p>If you don't have an account on the PhET website, please enter your email and desired password.</p>");
    }    
    
    function print_empty_password_login_form() {
        print_login_form(null, 
                         "<p>You forgot to specify a password for your new account.</p>
                         <p>Please specify a password now.</p>",
                         $GLOBALS['username']);
    }
    
    gather_script_params_into_globals();
    
    if (!isset($prefix)) $prefix = "..";
 
    if (!isset($username) || !isset($password)) {   
        if (cookie_var_is_stored("username")) {
            $username      = cookie_var_get("username");
            $password_hash = cookie_var_get("password_hash");

            if (!contributor_is_valid_login($username, $password_hash)) {
                cookie_var_clear("username");
                cookie_var_clear("password_hash");

                force_redirect(get_self_url(), 0);

                exit;
            }
        }
        else {            
            print_site_page('print_first_time_login_form', 3, $prefix);
        
            exit;
        }
    }
    else {
        if (contributor_is_contributor($username)) {
            $password_hash = md5($password);
            
            if (!contributor_is_valid_login($username, $password_hash)) {
                print_site_page('print_retry_login_form', 3, $prefix);
    
                contributor_send_password_reminder($username);
            
                exit;
            }
            else {
                cookie_var_store("username",      $username);
                cookie_var_store("password_hash", $password_hash);
            }
        }
        else if (is_email($username)) {
            if ($password == '') {
                print_site_page('print_empty_password_login_form', 3, $prefix);
                
                exit;
            }
            else {
                // Create new user account:
                contributor_add_new_contributor($username, $password);
                
                // Store the information in a cookie:
                cookie_var_store("username",      $username);
                cookie_var_store("password_hash", md5($password));
            }
        }
        else {
            print_site_page('print_not_an_email_login_form', 3, $prefix);

            exit;
        }
    }
    
    // Store the contributor id globally so the including script can use it:
    $contributor_id = contributor_get_id_from_username($username);
    
    // Stuff all the contributor fields into global variables:
    gather_array_into_globals(contributor_get_contributor_from_id($contributor_id));
?>