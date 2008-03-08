<?php
    if (!defined('SITE_ROOT')) {
        include_once("../admin/global.php");
    }

    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/referring-page-tracker.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/authentication.php");
    include_once(SITE_ROOT."admin/cache-utils.php");

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

        $categories["simulations/translations.php"] = "Translated Sims";

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

        $teacher_ideas_subs['teacher_ideas/contribute.php']             = 'Submit an Activity';
        $teacher_ideas_subs['teacher_ideas/manage-contributions.php']   = 'My Activities';
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
                'simulations/index.php?cat=Top_Simulations' => 'On Line',
                'get_phet/full_install.php' => 'Full Install',
                'get_phet/simlauncher.php'  => 'One at a Time'
            )
        );

        print_navigation_element(
            $prefix,
            $selected_page,
            "tech_support/index.php",
            "Troubleshooting",
            array(
                'tech_support/support-java.php'     => 'Java',
                'tech_support/support-flash.php'    => 'Flash'
            )
        );

        print_navigation_element(
            $prefix,
            $selected_page,
            "contribute/index.php",
            "Contribute",
            array(
                'contribute/translate.php'    => 'PhET Translation Utility'
            )
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
                'about/faq.php'         => 'FAQ',
                'about/news.php'        => 'News',
                'about/contact.php'     => 'Contact',
                'about/who-we-are.php'  => 'Who We Are',
                'about/licensing.php'   => 'Licensing'// ,
                //                 'sponsors/index.php'    => 'Sponsors'
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
                    'admin/compose-newsletter.php'  => 'Compose Newsletter',
                    'admin/view-statistics.php'     => 'View Statistics',
                    'admin/manage-db.php'           => 'Manage Database'
                )
            );
        }

        if (isset($GLOBALS['contributor_authenticated']) && $GLOBALS['contributor_authenticated'] == 1) {
            print_navigation_element(
                $prefix,
                $selected_page,
                "teacher_ideas/user-edit-profile.php",
                "My Profile",
                array()
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
        $php_self = $_SERVER['REQUEST_URI'];

        // Don't require authentication, but do it if the cookies are available:
        do_authentication(false);

        global $contributor_authenticated;

        if (!$contributor_authenticated) {
            $cooked_php_self = htmlspecialchars($php_self);
            $utility_panel_html = <<<EOT
                <a href="$prefix/teacher_ideas/login-and-redirect.php?url=$cooked_php_self">Login / Register</a>
EOT;
        }
        else {
            $contributor_name = $GLOBALS['contributor_name'];

            $utility_panel_html = <<<EOT
                Welcome <a href="$prefix/teacher_ideas/user-edit-profile.php">$contributor_name</a> - <a href="$prefix/teacher_ideas/user-logout.php?url=$php_self">Logout</a>
EOT;
        }

        return $utility_panel_html;
    }

    function print_site_page($content_printer, $selected_page = null, $redirection_site = null, $timeout = 0) {
        if (isset($GLOBALS['g_cache_current_page'])) {
            cache_auto_start();
        }

        do_authentication(false);

        $request_uri = $_SERVER['REQUEST_URI'];

        $php_self = $_SERVER['PHP_SELF'];

        global $referrer;

        $prefix = "..";

        //expire_page_immediately();

        if ($redirection_site != null) {
            $meta_redirect = "<meta http-equiv=\"Refresh\" content=\"$timeout;url=$redirection_site\" />";
        }
        else {
            $meta_redirect = '';
        }

        $utility_panel_html = get_sitewide_utility_html($prefix);

        //ob_start('ob_gzhandler');

/*

    TODO:

        Reinsert <?xml version="1.0" encoding="UTF-8"?>
            after IE6 is dead

                <meta http-equiv="Expires"      content="-1" />
                <meta http-equiv="Pragma"       content="no-cache" />
*/
        print <<<EOT
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>PhET :: Physics Education Technology at CU Boulder</title>

                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                $meta_redirect

                <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />

                <style type="text/css">
                    /*<![CDATA[*/
                        @import url($prefix/css/main.css);
                    /*]]>*/
                </style>

                <!-- compliance patch for microsoft browsers -->
                <!--[if lt IE 7]><script src="$prefix/js/ie7/ie7-standard-p.js" type="text/javascript"></script><![endif]-->
                <script src="$prefix/js/jquery.pack.js"         type="text/javascript"></script>
                <script src="$prefix/js/jquery.MultiFile.js"    type="text/javascript"></script>
                <script src="$prefix/js/jquery_std.js"          type="text/javascript"></script>
                <script src="$prefix/js/jquery.autocomplete.js" type="text/javascript"></script>
                <script src="$prefix/js/http.js"                type="text/javascript"></script>
                <script src="$prefix/js/form-validation.js"     type="text/javascript"></script>
                <script src="$prefix/js/multi-select.js"     type="text/javascript"></script>
                <script src="$prefix/js/phet-scripts.js"        type="text/javascript"></script>
                <script type="text/javascript">
                    //<![CDATA[

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

                            // $('#contributor_name_uid').autocomplete('$prefix/admin/get-contributor-names.php',
                            //     {
                            //         onItemSelect: function(li, v) {
                            //             on_name_change(v);
                            //         }
                            //     }
                            // );

                            select_current_navbar_category();

                            // Old method used a lot of $() to find inputs and place a regex
                            // validation pattern in it, which really bogged down on big pages.
                            // Replaced with a method to just look for inputs and moved it to
                            // form-validation.js to clean up a little.
                            // On my computer, improvement on big pages is from ~5s to 0.005s,
                            // small pages from about 0.200s to 0.0s (time too small to measure)
                            setup_input_validation_patterns();

                            // This could be improved, but at the moment it isn't taking enough
                            // time to worry about.
                            $('input, button, textarea, select').each(
                                function() {
                                    if (this.pattern) {
                                        // Perform immediate validation:
                                        validate_form_element(this, this.pattern);

                                        // Validate on key up:
                                        this.onkeyup = function() {
                                            validate_form_element(this, this.pattern);

                                            return true;
                                        }

                                        // Validate on change (for autofill & such):
                                        this.onchange = function() {
                                            validate_form_element(this, this.pattern);

                                            return true;
                                        }

                                        // Validate on blur (for Firefox autofill):
                                        this.onblur = function() {
                                            validate_form_element(this, this.pattern);

                                            return true;
                                        }

                                        // Validate on click (for Firefox autofill):
                                        this.onclick = function() {
                                            validate_form_element(this, this.pattern);

                                            return true;
                                        }

                                        // IE6 workaround (it doesn't fire onchange for autofill):
                                        this.onpropertychange = function() {
                                            validate_form_element(this, this.pattern);

                                            return true;
                                        }
                                    }
                                }
                            );

                            // This could be improved, but at the moment it isn't taking enough
                            // time to worry about
                            $('input').each(
                                function() {
                                    if (this.getAttribute('type') == 'submit') {
                                        this.onclick = function() {
                                            return validate_entire_form(this.form);
                                        }
                                    }
                                }
                            );
                        }
                    );
                    //]]>
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
        if (isset($GLOBALS['g_cache_current_page'])) {
            cache_auto_end();
        }
    }

    function print_blank_site_page($content_printer, $prefix = "..") {
        // Don't require authentication, but do it if the cookies are available:
        do_authentication(false);

        // expire_page_immediately();
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

?>