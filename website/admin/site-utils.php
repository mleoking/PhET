<?php
	if (!defined('SITE_ROOT')) {
    	include_once("../admin/global.php");
	}
    
    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");    
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/authentication.php");
    
    include_once(SITE_ROOT."teacher_ideas/referrer.php");
    
    function print_header_navigation_element($prefix, $selected_page, $link, $desc, $access_key) {
        $this_element_is_selected = "$access_key" == "$selected_page";

        if ($this_element_is_selected) {
            $selected_status = "class=\"selected\"";
        }
        else {
            $selected_status = '';
        }

        print <<<EOT
            <li $selected_status><a href="$prefix/$link" accesskey="$access_key">$desc</a></li>
EOT;
    }
    
    function print_subnavigation_element($prefix, $link, $desc) {
        print <<<EOT
        <li class="subnav"><a href="$prefix/$link" class="subnav">$desc</a></li>
EOT;
    }
    
    function print_navigation_element($prefix, $selected_page, $link, $desc, $submenus = array()) {
        static $access_key = 1;
        
        $this_element_is_selected = "$access_key" == "$selected_page";
        
        if ($this_element_is_selected) {
            $selected_status = 'class="topnav-selected"';
        }
        else {
            $selected_status = 'class="topnav"';
        }
        
        print <<<EOT
            <li $selected_status><a href="$prefix/$link" accesskey="$access_key" $selected_status>$desc</a>
EOT;

        if ($this_element_is_selected) {
            if (count($submenus) > 0) {
                print '<ul class="subnav">';
            
                foreach($submenus as $key => $value) {
                    print_subnavigation_element($prefix, $key, $value);
                }
            
                print "</ul>";
            }
        }

        $access_key = $access_key + 1;
        
        print "</li>";
    }
    
    function get_sim_categories_for_navbar($prefix) {
        $categories = array();

        foreach (sim_get_visible_categories() as $category) {
            $cat_id   = $category['cat_id'];
            $cat_name = $category['cat_name'];

            $link = sim_get_category_url_by_cat_id($cat_id);

            $categories["$link"] = $cat_name;
        } 
        
        return $categories;
    }
    
    function print_navigation_bar($selected_page = null, $prefix = "..") {    
        print <<<EOT
            <div id="localNav">
                <ul class="topnav">
EOT;

        print_navigation_element(
            $prefix, 
            $selected_page, 
            "index.php", 
            "Home"
        );
        
        print_navigation_element(
            $prefix, 
            $selected_page, 
            "simulations/index.php?cat=Top_Simulations",
            "Simulations",
            get_sim_categories_for_navbar($prefix)
        );

        $teacher_ideas_subs = array();

        $teacher_ideas_subs['teacher_ideas/browse.php']                 = 'Browse';
        
        // if (!isset($GLOBALS['contributor_authenticated']) || $GLOBALS['contributor_authenticated'] == false) {
        //     $teacher_ideas_subs['teacher_ideas/login.php'] = 'Login';
        // }
        
        $teacher_ideas_subs['teacher_ideas/contribute.php']             = 'Contribute';
        $teacher_ideas_subs['teacher_ideas/manage-contributions.php']   = 'My Contributions';
        $teacher_ideas_subs['teacher_ideas/user-edit-profile.php']      = 'My Profile';
        
        if (isset($GLOBALS['contributor_authenticated']) && $GLOBALS['contributor_authenticated'] == true) {
            $teacher_ideas_subs['teacher_ideas/user-logout.php'] = 'Logout';
        }
        
        print_navigation_element(
            $prefix, 
            $selected_page,
            "teacher_ideas/index.php",
            "Teacher Ideas &amp; Activities",
            $teacher_ideas_subs
        );
        
        print_navigation_element(
            $prefix, $selected_page, 
            "get_phet/index.php",        
            "Run our Simulations",
            array(
                'get_phet/full_install.php' => 'Full Install',
                'get_phet/simlauncher.php'  => 'One at a Time'
            )
        );
        
        print_navigation_element(
            $prefix, 
            $selected_page, 
            "tech_support/index.php",    
            "Technical Support",
            array(
                'tech_support/support-java.php'     => 'Java',
                'tech_support/support-flash.php'    => 'Flash'
            )
        );
        
        print_navigation_element(
            $prefix, 
            $selected_page, 
            "contribute/index.php", 
            "Contribute"
        );
        
        print_navigation_element(
            $prefix, 
            $selected_page, 
            "research/index.php", 
            "Research"
        );
        
        print_navigation_element(
            $prefix, 
            $selected_page, 
            "about/index.php",           
            "About PhET",
            array(
                'about/contact.php'     => 'Contact',
                'about/licensing.php'   => 'Licensing',
				'about/legend.php'      => 'Legend'
            )
        );
        
        if (isset($GLOBALS['contributor_is_team_member']) && $GLOBALS['contributor_is_team_member'] == 1) {        
            print_navigation_element(
                $prefix, 
                $selected_page, 
                "admin/index.php",           
                "Administrative",
                array(
                    'admin/new-sim.php'             => 'Add Simulation', 
                    'admin/choose-sim.php'          => 'Edit Simulation',            
                    'admin/list-sims.php'           => 'List Simulations',            
                    'admin/organize-cats.php'       => 'Organize Categories',                   
                    'admin/organize-sims.php'       => 'Organize Simulations',            
                    'admin/manage-contributors.php' => 'Manage Contributors',                 
                    'admin/compose-newsletter.php'  => 'Compose Newsletter'
                )
            );
        }
                    
        print <<<EOT
                </ul>

                <div id="sponsors">
                    <h4>Principle Sponsors</h4>
                    
                    <div class="header">
                    </div>
                    <dl>
                        <dt><a href="http://www.hewlett.org/Default.htm">The William and Flora Hewlett Foundation</a></dt>

                        <dd><a href="http://www.hewlett.org/Default.htm">
                        <img src="$prefix/images/hewlett-logo.jpg" alt="The Hewlett Logo"/></a><br />
                    
                        <br />
                        Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.</dd>

                        <dt><a href="http://www.nsf.gov/">
                        <img class="sponsors" src="$prefix/images/nsf-logo.gif" alt="The NSF Logo"/>National Science Foundation</a></dt>

                        <dd><br />
                        An independent federal agency created by Congress in 1950 to promote the progress of science.<br />
                        <br />
                        <a href="../sponsors/index.php">
                        <img src="$prefix/images/other-sponsors.gif" alt="Other Sponsors Logo"/></a></dd>
                    </dl>
                </div>
            </div>
EOT;
    }

	function get_sitewide_utility_html($prefix = "..") {
		$php_self = $_SERVER['PHP_SELF'];
		
		// Don't require authentication, but do it if the cookies are available:
        do_authentication(false);
        
        global $contributor_authenticated;
		
		if (!$contributor_authenticated) {
			$utility_panel_html = <<<EOT
				<a href="$prefix/teacher_ideas/login-and-redirect.php?url=$php_self">Login</a> | <a href="$prefix/teacher_ideas/login-and-redirect.php?url=$php_self">Register</a>
EOT;
		}
		else {
			$contributor_name = $GLOBALS['contributor_name'];

			$utility_panel_html = <<<EOT
				Welcome $contributor_name - <a href="$prefix/teacher_ideas/user-logout.php?url=$php_self">Logout</a>
EOT;
		}
		
		return $utility_panel_html;
	}
    
    function print_site_page($content_printer, $selected_page = null, $redirection_site = null, $timeout = 0) {
		do_authentication(false);
		
        $request_uri = $_SERVER['REQUEST_URI'];

		$php_self = $_SERVER['PHP_SELF'];
        
        global $referrer;
        
        $prefix = "..";
        
        expire_page_immediately();

		if ($redirection_site != null) {
			$meta_redirect = "<meta http-equiv=\"Refresh\" content=\"$timeout;url=$redirection_site\" />";
		}
		else {
			$meta_redirect = '';
		}
		
		$utility_panel_html = get_sitewide_utility_html($prefix);
/*

    TODO: 
    
        Reinsert <?xml version="1.0" encoding="UTF-8"?>    
            after IE6 is dead

*/
        print <<<EOT
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>PhET :: Physics Education Technology at CU Boulder</title>
                
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <meta http-equiv="Pragma"  content="no-cache" />
                <meta http-equiv="Expires" content="-1" />
				$meta_redirect
                
                <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />
                
                <style type="text/css">
                /*<![CDATA[*/
                        @import url($prefix/css/main.css);
                /*]]>*/
                </style>    
                
                <script src="$prefix/js/jquery.pack.js"         type="text/javascript"></script>
                <script src="$prefix/js/jquery.MultiFile.js"    type="text/javascript"></script>
                <script src="$prefix/js/jquery_std.js"          type="text/javascript"></script>
                <script src="$prefix/js/jquery.autocomplete.js" type="text/javascript"></script>              
                <script src="$prefix/js/http.js"                type="text/javascript"></script>   
                
                <script type="text/javascript">
                    // AJAX login stuff:
                    /*<![CDATA[*/
                    
                    // This array stores all the 'contributor organizations' that we generated:
                    var generated_contributor_orgs = [];
                    
                    function post_required_info_displayed() {                    
                        on_email_change();
                        
                        // Place focus on password if it's empty':
                        var password_element = document.getElementById('contributor_password_uid');
                        
                        if (password_element) {
                            password_element.focus();
                        }
                    }

                	function select_text_in_input(field, start, end){
                		if( field.createTextRange ){
                			var selRange = field.createTextRange();
                			selRange.collapse(true);
                			selRange.moveStart("character", start);
                			selRange.moveEnd("character", end);
                			selRange.select();
                		} 
                		else if( field.setSelectionRange ){
                			field.setSelectionRange(start, end);
                		} 
                		else {
                			if( field.selectionStart ){
                				field.selectionStart = start;
                				field.selectionEnd = end;
                			}
                		}
                		field.focus();
                	};
                    
                    function select_question_marks_in_input(id) {
                        var element = document.getElementById(id);
                        
                        if (element) {
                            var value      = element.value;
                            var firstIndex = value.indexOf('?');
                            var lastIndex  = value.lastIndexOf('?') + 1;
                            
                            if (firstIndex != -1) {                            
                                select_text_in_input(element, firstIndex, lastIndex);
                            }
                        }
                    }
                    
                    function on_remind_me() {
                        var email_element = document.getElementById('contributor_email_uid');

                        var email = email_element.value;
                        
                        var password_element = document.getElementById('ajax_password_comment_uid');

                        HTTP.updateElementWithGet('$prefix/admin/remind-password.php?contributor_email=' + 
                            encodeURI(email), null, 'ajax_password_comment_uid');
                    }

                    function on_email_entered() {
                        var email_element    = document.getElementById('contributor_email_uid');
                        var password_element = document.getElementById('contributor_password_uid');
                        
                        var email    = email_element.value;
                        var password = password_element.value;
                            
                        HTTP.updateElementWithGet('$prefix/admin/do-ajax-login.php?contributor_email=' + 
                            encodeURI(email) + '&contributor_password=' + encodeURI(password), 
                            null, 'required_login_info_uid', 'post_required_info_displayed();');
                    }
                    
                    function deduce_author_organization() {
                        var email_element = document.getElementById('contributor_email_uid');

                        var email = email_element.value;
                        
                        HTTP.updateElementValueWithGet('$prefix/admin/get-contributor-org.php?contributor_email=' +
                            encodeURI(email), null, 'contribution_authors_organization_uid'); 
                    }
                        
                    function on_email_change() {
                        var email_element = document.getElementById('contributor_email_uid');

                        var email = email_element.value;
                        
                        var contact_element = document.getElementById('contribution_contact_email_uid');
                            
                        if (contact_element) {
                            contact_element.value = email;
                        }      
                        
                        // The email has changed. Now we would like to update the contributor organization
                        // based on the email domain:
                        var contributor_org_element = document.getElementById('contributor_organization_uid');
                        var contribution_org_element = document.getElementById('contribution_authors_organization_uid');

                        if (contributor_org_element) {
                            // We can update contributor organization if it's blank:
                            var can_overwrite_contrib = contributor_org_element.value == '';
                            
                            // Otherwise, the only reason we can update it is if it's holding a value
                            // that we generated ourselves:
                            if (!can_overwrite_contrib) {
                                for (var i = 0; i < generated_contributor_orgs.length; i++) {
                                    if (generated_contributor_orgs[i] == contributor_org_element.value) {
                                        can_overwrite_contrib = true;
                                        
                                        break;
                                    }
                                }
                            }
                            
                            if (can_overwrite_contrib) {
                                var email_pattern = /^s*\w+@(\w+)(\.([\w\.]+))?\s*$/;
                                
                                var result;
                                
                                if ((result = email_pattern.exec(email)) != null) {
                                    var domain = result[1];
                                    var ext    = result[3];
                                    
                                    domain = domain.substring(0, 1).toUpperCase() + domain.substring(1, domain.length);
                                    
                                    if (ext == 'edu') {
                                        contributor_org_element.value = 'University of ' + domain;
                                    }
                                    else {
                                        contributor_org_element.value = domain + ', Inc.';
                                    }
                                    
                                    // Remember that we generated this value, so we know we can overwrite
                                    // it later:
                                    generated_contributor_orgs.push(contributor_org_element.value);
                                    
                                    var contribution_org_element = document.getElementById('contribution_authors_organization_uid');

                                    if (contribution_org_element) {
                                        contribution_org_element.value = contributor_org_element.value;
                                    }
                                }
                            }
                        }

                        HTTP.updateElementWithGet('$prefix/admin/check-email.php?contributor_email=' + 
                            encodeURI(email), null, 'ajax_email_comment_uid', 'on_password_change();');
                    }
                    
                    function on_contributor_organization_change() {
                        var contributor_org_element = document.getElementById('contributor_organization_uid');
                        var contribution_org_element = document.getElementById('contribution_authors_organization_uid');
                        
                        if (contributor_org_element && contribution_org_element) {
                            if (contribution_org_element.value == '') {
                                contribution_org_element.value = contributor_org_element.value;
                            }
                        }
                    }
                    
                    function on_password_change() {
                        var email_element    = document.getElementById('contributor_email_uid');
                        var password_element = document.getElementById('contributor_password_uid');
                        
                        var email    = email_element.value;
                        var password = password_element.value;

                        HTTP.updateElementWithGet('$prefix/admin/check-password.php?contributor_email=' + 
                            encodeURI(email) + '&contributor_password=' + 
                            encodeURI(password), null, 'ajax_password_comment_uid', 'deduce_author_organization();');
                    }
                    
                    function on_name_change(n) {
                        var name;
                        
                        // When the name changes, update the authors:
                        if (n) {
                            name = n;                            
                        }
                        else {
                            var name_element = document.getElementById('contributor_name_uid');
                        
                            if (name_element) {
                                name = name_element.value;
                            }
                        }
                         
                        if (name) {   
                            var authors_element = document.getElementById('contribution_authors_uid');
                            
                            if (authors_element) {
                                authors_element.value = name;
                            }
                            
                            var password_element = document.getElementById('contributor_password_uid');
                            
                            var password_url = '';
                            
                            if (password_element) {
                                var password = password_element.value;
                                
                                password_url = '&contributor_password=' + encodeURI(password);
                            }
                            
                            HTTP.updateElementWithGet('$prefix/admin/do-ajax-login.php?contributor_name=' + 
                                encodeURI(name) + password_url, 
                                null, 'required_login_info_uid', 'on_email_change();');
                        }
                    }

                    function login_create_account() {
                        var name     = document.getElementById('contributor_name_uid').value;
                        var email    = document.getElementById('contributor_email_uid').value;
                        var password = document.getElementById('contributor_password_uid').value;
                        var org      = document.getElementById('contributor_organization_uid').value;
                        
                        HTTP.updateElementWithGet('$prefix/admin/do-ajax-login.php' + 
                            '?contributor_name='            + encodeURI(name)       + 
                            '&contributor_email='           + encodeURI(email)      +
                            '&contributor_password='        + encodeURI(password)   +
                            '&contributor_organization='    + encodeURI(org)        +
                            '&action=create', 
                            null, 'required_login_info_uid');
                    }
                    
                    function login_login() {
                        var email    = document.getElementById('contributor_email_uid').value;
                        var password = document.getElementById('contributor_password_uid').value;
                        
                        HTTP.updateElementWithGet('$prefix/admin/do-ajax-login.php' + 
                            '?contributor_email='           + encodeURI(email)      +
                            '&contributor_password='        + encodeURI(password)   +
                            '&action=login', 
                            null, 'required_login_info_uid');                        
                    }
                    
                    function string_starts_with(this_string, that_string) {
                        var index = this_string.lastIndexOf(that_string);
                        
                        if (index == -1) {
                            return false;
                        }
                        
                        return index == 0;
                    }

					function open_limited_window(url, title) {
						window.open(url, title, 'status=0,toolbar=0,location=0,menubar=0,directories=0,resizable=1,scrollbars=0,width=640,height=480');
					}
                    
                    function select_current_navbar_category() {
                        $("li.subnav a").each(function(i) {
							var re = /^.+(\.com|\.edu|\.net|\.org|(localhost:\d+))(\/.+)$/i;
							
							var result = re.exec(this.href);
							
							var relative_url = this.href;
							
							if (result) {								
								relative_url = result[3];
							}
							
                            if (string_starts_with('$request_uri', relative_url)) {
                                this.className            = 'subnav-selected';
                                this.parentNode.className = 'subnav-selected';
                            }
                        });                        
                    }
                    
                    $(document).ready(
                        function() {
                            $('#contributor_name_uid').autocomplete('$prefix/admin/get-contributor-names.php',
                                {
                                    onItemSelect: function(li, v) {
                                        on_name_change(v);
                                    }
                                }
                            );
                            
                            select_current_navbar_category();
                        }
                    );
                    
                    /*]]>*/
                </script>             
            </head>
            

            <body id="top">
                <div id="skipNav">
                    <a href="#content" accesskey="0">Skip to Main Content</a>
                </div>

                <div id="header">
                    <div id="headerContainer">
                        <div class="images">
                            <div class="logo">
								<a href="../index.php"><img src="$prefix/images/phet-logo.gif" alt="PhET Logo" title="Click here to go to the home page" /></a>
                            </div>

                            <div class="title">
                                <img src="$prefix/images/logo-title.jpg" alt="Physics Education Technology - University of Colorado, Boulder" title="Physics Education Technology - University of Colorado, Boulder" />
                            
								<div id="quicksearch">
		                            <form method="post" action="../simulations/search.php">
		                                <fieldset>
		                                    <span>Search</span>
		                                    <input type="text" size="15" name="search_for" title="Enter the text to search for" class="always-enabled" />
		                                    <input type="submit" value="Go" title="Click here to search the PhET website" class="always-enabled" />
		                                    <input type="hidden" name="referrer" value="$referrer"  class="always-enabled" />
		                                </fieldset>
		                            </form>
		                        </div>
							</div>
                        </div>

                        <div class="clear"></div>
                    </div>
                </div>

                <div id="container">
EOT;

        print_navigation_bar($selected_page, $prefix);

        print <<<EOT
                    <div id="content">  
                        <div class="main">
                    
EOT;

        call_user_func($content_printer);

        print <<<EOT
                        </div>
                        
                        <div id="footer">
                            <p>&copy; 2007 University of Colorado. All rights reserved.</p>
                        </div>
                    </div>
                </div>

				<div id="utility-panel">
					$utility_panel_html
				</div>
            </body>        
            </html>
EOT;
    }
    
 function print_blank_site_page($content_printer, $prefix = "..") {
     // Don't require authentication, but do it if the cookies are available:
     do_authentication(false);
     
     expire_page_immediately();
     /*

         TODO: 

             Reinsert <?xml version="1.0" encoding="UTF-8"?>    
                 after IE6 is dead

     */     
        print <<<EOT
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>PhET :: Physics Education Technology at CU Boulder</title>

                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

                <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />

                <style type="text/css">
                /*<![CDATA[*/
                        @import url($prefix/css/main.css);
                /*]]>*/
                </style>    

                <script src="$prefix/js/jquery.pack.js"         type="text/javascript"></script>
                <script src="$prefix/js/jquery.MultiFile.js"    type="text/javascript"></script>
                <script src="$prefix/js/jquery.autocomplete.js" type="text/javascript"></script>
                <script src="$prefix/js/http.js"                type="text/javascript"></script>
                
                <script type="text/javascript">
                    /* <![CDATA[ */
                    
                        $('.showhide').click(
                            function() {
                                alert("This is an alert.");
                                
                                $(this).parent().next().toggle('slow');
                            
                                return false;
                            }
                        );
                    
                    /* ]]> */
                </script>
            </head>

            <body>
EOT;

        call_user_func($content_printer);

        print <<<EOT
            </body>
        </html>
EOT;
    }    