<?php

    include_once("../admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/referring-page-tracker.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/authentication.php");
    include_once(SITE_ROOT."admin/cache-utils.php");

    // TODO: move this into admin
    include_once(SITE_ROOT."teacher_ideas/referrer.php");

    // Navigation enumerations for the selected page
    // Initial conversation from numbers to something more meaningful
    // TODO: I suspect that these just indext an array wherever the navigation bar
    // is stored.  It should be changed to accept these values, and use them in a
    // sensical fashion.  Additionally, I suspect that there is a JavaScript component
    // that must manually be kept in sync with the PHP code.  Would be best if they kept
    // in sync automatically.
    define("NAV_NOT_SPECIFIED",  -1);
    define("NAV_INVALID0",  0);
    define("NAV_INVALID1",  1);
    define("NAV_SIMUALTIONS",  2);
    define("NAV_TEACHER_IDEAS",  3);
    define("NAV_GET_PHET",  4);
    define("NAV_TECH_SUPPORT",  5);
    define("NAV_CONTRIBUTE",  6);
    define("NAV_RESEARCH",  7);
    define("NAV_ABOUT_PHET",  8);
    define("NAV_ADMIN",  9);
    define("NAV_COUNT",  10);

    /**
     * For debugging, make sure the selected nav page is valid
     *
     * @param $selected_page emun/int - enumeration of the page that should be active on the navbar
     * @return bool TRUE if the page is valid
     */
    function selected_page_is_valid($selected_page) {
        if ($selected_page < 0) {
            return ($selected_page == NAV_NOT_SPECIFIED);
        }

        return (($selected_page < NAV_COUNT) &&
            ($selected_page != NAV_INVALID0) &&
            ($selected_page != NAV_INVALID1));
    }

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
        assert(selected_page_is_valid($selected_page));

        static $access_key = 1;

        $this_element_is_selected = "$access_key" == "$selected_page";

        if ($this_element_is_selected) {
            $selected_status = 'class="topnav-selected"';
        }
        else {
            $selected_status = 'class="topnav"';
        }

        print <<<EOT
                <li $selected_status>
                    <a href="$prefix/$link" accesskey="$access_key" $selected_status>$desc</a>

EOT;

        if ($this_element_is_selected) {
            if (count($submenus) > 0) {
                print '                    <ul class="subnav">'."\n";

                foreach($submenus as $key => $value) {
                    print_subnavigation_element($prefix, $key, $value);
                }

                print "                    </ul>\n";
            }
        }

        $access_key = $access_key + 1;

        print "                </li>\n";
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
        assert(selected_page_is_valid($selected_page));

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

        // In between authentication schemes
        if ((auth_complete() && auth_user_validated()) ||
            (isset($GLOBALS['contributor_authenticated']) && $GLOBALS['contributor_authenticated'] == true)) {
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
                'tech_support/support-flash.php'    => 'Flash',
                'tech_support/support-javascript.php'    => 'JavaScript'
            
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

        $contributor_is_team_member = 0;
        $contributor_authenticated = auth_user_validated();
        if ($contributor_authenticated) {
            $contributor = contributor_get_contributor_by_username(auth_get_username());
            $contributor_is_team_member = $contributor['contributor_is_team_member'];
        }

        if (($contributor_is_team_member == 1) ||
            (isset($GLOBALS['contributor_is_team_member']) && $GLOBALS['contributor_is_team_member'] == 1)) {
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

        if (($contributor_authenticated == 1) || 
           (isset($GLOBALS['contributor_authenticated']) && $GLOBALS['contributor_authenticated'] == 1)) {
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
        $contributor_authenticated = auth_user_validated();

        //global $contributor_authenticated;

        if (!$contributor_authenticated) {
            $cooked_php_self = htmlspecialchars($php_self);
            $utility_panel_html = <<<EOT
            <a href="$prefix/teacher_ideas/login-and-redirect.php?url=$cooked_php_self">Login / Register</a>

EOT;
        }
        else {
            $contributor = contributor_get_from_contributor_email(auth_get_username());
            $contributor_name = $contributor['contributor_name'];
//            $contributor_name = $GLOBALS['contributor_name'];

            $formatted_php_self = format_string_for_html($php_self);

            $utility_panel_html = <<<EOT
        <!-- .. -->\n
            Welcome <a href="$prefix/teacher_ideas/user-edit-profile.php">$contributor_name</a> - <a href="$prefix/teacher_ideas/user-logout.php?url=$formatted_php_self">Logout</a>

EOT;
        }

        return $utility_panel_html;
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
