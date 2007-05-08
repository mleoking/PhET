<?php
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("sim-utils.php");
    include_once("db-utils.php");    
    
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
    
    function print_navigation_element($prefix, $selected_page, $link, $desc, $submenu_text) {
        static $access_key = 1;
        
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

        if ($this_element_is_selected) {
            print $submenu_text;
        }

        $access_key = $access_key + 1;
    }
    
    function get_sim_categories_html($prefix) {
        global $connection;
        
        $categories = '';

        // List all the categories:

        // start selecting SIMULATION CATEGORIES from database table
        $category_table = mysql_query(SQL_SELECT_ALL_VISIBLE_CATEGORIES, $connection);

        while ($category = mysql_fetch_assoc($category_table)) {
            $cat_id   = $category['cat_id'];
            $cat_name = format_for_html($category['cat_name']);

            $categories .= "<li class=\"sub\"><span class=\"sub-nav\"><a href=\"${prefix}/simulations/index.php?cat=$cat_id\">&rarr; $cat_name</a></span></li>";          
        } 
        
        return $categories;
    }
    
    function print_navigation_bar($selected_page = null, $prefix = "..") {    
        print <<<EOT
            <div id="localNav">
                <ul>
EOT;

        print_navigation_element($prefix, $selected_page, "index.php",                 "Home",
            ''
        );
        
        print_navigation_element($prefix, $selected_page, "simulations/index.php",      "Simulations",
            get_sim_categories_html($prefix)
        );
        
        print_navigation_element($prefix, $selected_page, "$prefix/teacher_ideas/index.php",   "Teacher Ideas &amp; Activities",
            <<<EOT
                <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/search.php">→ Search</a></span></li>

                <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/contribute.php">→ Contribute</a></span></li>
EOT
        );
        
        print_navigation_element($prefix, $selected_page, "$prefix/get_phet/index.php",        "Download PhET",
            <<<EOT
                <li class="sub"><span class="sub-nav"><a href="$prefix/get_phet/full_install.php">→ Full Install</a></span></li>

                <li class="sub"><span class="sub-nav"><a href="$prefix/get_phet/simlauncher.php">→ Partial Install</a></span></li>
EOT
        );
        
        print_navigation_element($prefix, $selected_page, "$prefix/tech_support/index.php",    "Technical Support",
            <<<EOT
                <li class="sub"><span class="sub-nav"><a href="$prefix/tech_support/support-java.php">→ Java</a></span></li>

                <li class="sub"><span class="sub-nav"><a href="$prefix/tech_support/support-flash.php">→ Flash</a></span></li>
EOT
        );
        
        print_navigation_element($prefix, $selected_page, "$prefix/contribute/index.php",      "Contribute",
            <<<EOT
                <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/index.php">→ Ideas and Activities</a></span></li>
                
                <li class="sub"><span class="sub-nav"><a href="$prefix/contribute/user-edit-profile.php">→ Edit Profile</a></span></li>
EOT
        );
        
        print_navigation_element($prefix, $selected_page, "$prefix/research/index.php",        "Research",
            ''
        );
        
        print_navigation_element($prefix, $selected_page, "$prefix/about/index.php",           "About PhET",
            <<<EOT
                <li class="sub"><span class="sub-nav"><a href="$prefix/about/contact.php">→ Contact</a></span></li>

                <li class="sub"><span class="sub-nav"><a href="$prefix/about/licensing.php">→ Licensing</a></span></li>
EOT
        );
                    
        print <<<EOT
                </ul>

                <h4><br />
                Principle Sponsors</h4>

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
                    <a href="../sponsors/index.php"><
                    img src="$prefix/images/other-sponsors.gif" alt="Other Sponsors Logo"/></a></dd>
                </dl>
            </div>
EOT;
    }
    
    function print_site_page($content_printer, $selected_page = null, $prefix = "..") {
        print <<<EOT
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

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
            </head>

            <body>
                <div id="skipNav">
                    <a href="#content" accesskey="0">Skip to Main Content</a>
                </div>

                <div id="header">
                    <div id="headerContainer">
                        <div class="images">
                            <span class="logo">
                                <img src="$prefix/images/phet-logo.gif" alt="The PhET Logo" title="" />
                            </span>
                
                            <span class="title">
                                <img src="$prefix/images/logo-title.jpg" alt="The PhET Title" title="" />
                            </span>
                        </div>

                        <div class="clear"></div>

                        <div class="mainNav">
                            <ul>
EOT;

        print_header_navigation_element($prefix, $selected_page, "$prefix/index.php",              "Home",          1);
        print_header_navigation_element($prefix, $selected_page, "$prefix/simulations/index.php",  "Simulations",   2);
        print_header_navigation_element($prefix, $selected_page, "$prefix/research/index.php",     "Research",      7);
        print_header_navigation_element($prefix, $selected_page, "$prefix/about/index.php",        "About PhET",    8);
                                
       print <<<EOT
                            </ul>
                        </div>
                    </div>
                </div>

                <div id="container">
EOT;

        print_navigation_bar($selected_page, $prefix);

        print <<<EOT
                    <div id="content">  
                        <div class="productList">
EOT;

        call_user_func($content_printer);

        print <<<EOT
                        </div>
                        
                        <p class="footer">© 2007 PhET. All rights reserved.<br />
                    </div>
                </div>
            </body>
            </html>
EOT;
    }