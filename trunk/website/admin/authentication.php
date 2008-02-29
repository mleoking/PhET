<?php

    if (!defined('SITE_ROOT')) {
        include_once('../admin/global.php');
    }
    
    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");

	$script = get_self_url();
	
	$title = isset($GLOBALS['custom_title']) ? urldecode($GLOBALS['custom_title']) : "Login/Register";
	$body  = isset($GLOBALS['custom_body'])  ? urldecode($GLOBALS['custom_body'])  : "<p>Please login with your existing PhET account, or create a new one.</p>";
    
    function print_first_time_login_form() { 
	    global $title, $body, $script;
	
        print "<h1>$title</h1>";

		print_contribute_login_form($script, null, $script, "$body");
    }

    function print_retry_login_form() {
		global $title, $body, $script;
	     
        print "<h1>$title - Login Incorrect</h1>";
		print_contribute_login_form($script, null, $script,
            "<p><strong>The password you entered is incorrect.</strong> If you entered the correct email address, please check your email now for a password reminder.</p>
            $body");
    }

	function print_not_an_existing_account() {
		global $title, $body, $script;
		
	    print "<h1>$title - Unknown Account</h1>";
		
		print_contribute_login_form($script, null, $script,
            "<p><strong>The email address you entered does not have an existing PhET account.</strong></p>
             $body");
    }

    function print_not_an_email_login_form() {
		global $title, $body, $script;
		
	    print "<h1>$title - Invalid Email</h1>";
		
		print_contribute_login_form($script, null, $script,
            "<p><strong>The email address you entered is not a valid email address.</strong></p>
             $body");
    }    

    function print_empty_password_login_form() {
		global $title, $body, $script;
		
        print "<h1>$title - No Password Specified</h1>";
		
		print_contribute_login_form($script, null, $script,
                         "<p><strong>You forgot to specify a password for your new account.</strong></p>
                         $body");
    }    

    function do_authentication($login_required = true) {
        static $already_tried_login_required = null;
        global $contributor_authenticated;

		if (isset($_REQUEST['login_required']) && $_REQUEST['login_required'] == "true") {
			$login_required = true;
		}

        if ($login_required === $already_tried_login_required) {
            return $contributor_authenticated;
        }

        $already_tried_login_required = $login_required;
        
        $contributor_authenticated = false;
        
        // Look for cookie variables:
        if (cookie_var_is_stored("contributor_email")) {
            $contributor_email         = cookie_var_get("contributor_email");
            $contributor_password_hash = cookie_var_get("contributor_password_hash");
            
            // Don't trust the cookie; validate it:
            if (!contributor_is_valid_login($contributor_email, $contributor_password_hash)) {
                // Cookie is invalid. Clear it:
                cookie_var_clear("contributor_email");
                cookie_var_clear("contributor_password_hash");

                if ($login_required) {
                    force_redirect(get_self_url(), 0);

                    exit;
                }
            }
            else {
                $contributor_authenticated = true;
            }
        }
        else {
            // No cookie variables
            if (isset($_REQUEST['contributor_email'])) {
                $contributor_email = $_REQUEST['contributor_email'];
            }
			if (isset($_REQUEST['contributor_name'])) {
				$contributor_name = $_REQUEST['contributor_name'];
			}
        
            if ((!isset($contributor_email) || trim($contributor_email) == '') && 
                isset($contributor_name) && trim($contributor_name) != '') {
                // Username not present, but contributor name is. 
                // Deduce email from contributor name:
                $contributor = contributor_get_contributor_by_name($contributor_name);
            
                if ($contributor) {
                    $contributor_email = $contributor['contributor_email'];
                }
            }
        
            if (isset($_REQUEST['contributor_password'])) {
                $contributor_password = $_REQUEST['contributor_password'];
            }

            if (!isset($contributor_email) || !isset($contributor_password)) {   
                // No contributor_email/contributor_password specified, and no cookie variables.
                // Print the first-time login form:      
                if ($login_required) {
                    print_site_page('print_first_time_login_form', 3);

                    exit;
                }
            }
            else {
                // Both contributor_email and contributor_password were specified.
                if (contributor_is_contributor($contributor_email)) {
                    // The contributor_email already exists and denotes a contributor. Check 
                    // the contributor_password to make sure it is correct.
        
                    $contributor_password_hash = md5($contributor_password);
        
                    if (!contributor_is_valid_login($contributor_email, $contributor_password_hash)) {
                        contributor_send_password_reminder($contributor_email);

                        if ($login_required) {
                            print_site_page('print_retry_login_form', 3);        

                            exit;
                        }
                    }
                    else {
                        cookie_var_store("contributor_email",         $contributor_email);
                        cookie_var_store("contributor_password_hash", $contributor_password_hash);
            
                        $contributor_authenticated = true;
                    }
                }
                else if (is_email($contributor_email)) {
                    // The contributor_email does not exist, and is a valid e-mail address.
                    if (trim($contributor_password) == '') {
                        if ($login_required) {
                            print_site_page('print_empty_password_login_form', 3);
            
                            exit;
                        }
                    }
                    else if (isset($_REQUEST['create_new_account']) && $_REQUEST['create_new_account'] == '1') {
                        // Create new user account:
                        $contributor_id = contributor_add_new_contributor($contributor_email, $contributor_password);
            
                        // Check for optional fields that may have been passed along:
                        if (isset($_REQUEST['contributor_name'])) {
                            contributor_update_contributor(
                                $contributor_id,
                                array(
                                    'contributor_name' => $_REQUEST['contributor_name']
                                )
                            );
                        }
            
                        if (isset($_REQUEST['contributor_organization'])) {
                            contributor_update_contributor(
                                $contributor_id,
                                array(
                                    'contributor_organization' => $_REQUEST['contributor_organization']
                                )
                            );
                        }

						if (isset($_REQUEST['contributor_desc'])) {
							contributor_update_contributor(
								$contributor_id,
								array(
									'contributor_desc' => $_REQUEST['contributor_desc']
								)
							);
						}
            
                        // Store the information in a cookie so user won't have to re-login:
                        cookie_var_store("contributor_email",         $contributor_email);
                        cookie_var_store("contributor_password_hash", md5($contributor_password));
            
                        $contributor_authenticated = true;
                    }
					else {
						if ($login_required) {
							print_site_page('print_not_an_existing_account', 3);
							
							exit;
						}
					}
                }
                else {
                    // The contributor_email does not exist, nor is it a valid email address.
                    if ($login_required) {
                        print_site_page('print_not_an_email_login_form', 3);

                        exit;
                    }
                }
            }
        }

        if ($contributor_authenticated) {
            // Store the contributor id globally so the including script can use it:
            $contributor_id = contributor_get_id_from_contributor_email($contributor_email);

            // Stuff all the contributor fields into global variables:
            gather_array_into_globals(contributor_get_contributor_by_id($contributor_id));
        }
        
        return $contributor_authenticated;
    }
?>