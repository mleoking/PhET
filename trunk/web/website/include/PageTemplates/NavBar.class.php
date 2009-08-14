<?php

class NavBar {
    // Navigation enumerations for the selected page
    //
    // Initial conversation from numbers to something more meaningful
    //
    // TODO: I suspect that these just index an array wherever the
    // navigation bar is stored.  It should be changed to accept these
    // values, and use them in a sensical fashion.  Additionally, I
    // suspect that there is a JavaScript component that must manually
    // be kept in sync with the PHP code.  Would be best if they kept
    // in sync automatically.  Verify the above and fix.
    const NAV_NOT_SPECIFIED =  -1;
    const NAV_INVALID0 =  0;
    const NAV_INVALID1 =  1;
    const NAV_SIMULATIONS =  2;
    const NAV_TEACHER_IDEAS =  3;
    const NAV_GET_PHET =  4;
    const NAV_TECH_SUPPORT =  5;
    const NAV_CONTRIBUTE =  6;
    const NAV_RESEARCH =  7;
    const NAV_ABOUT_PHET =  8;
    const NAV_ADMIN =  9;
    const NAV_COUNT =  10;

    // TODO: this should be an abstract interface for authentication
    private $page;

    private $navigation_category;

    private $prefix;

    private $access_key;

    function __construct($page, $navigation_category, $prefix) {
        $this->page = $page;

        assert($this->selected_page_is_valid($navigation_category));
        $this->navigation_category = $navigation_category;

        $this->access_key = 1;

        $this->prefix = $prefix;

        $pos = strpos($_SERVER['REQUEST_URI'], 'simulations/index.php');
        $this->nav_uri = substr($_SERVER['REQUEST_URI'], $pos);

        $this->hierarchical_categories = new HierarchicalCategories();
    }

    function set_uri($uri) {
        $this->nav_uri = $uri;
    }

    function set_navigation_category($navigation_category) {
        assert($this->selected_page_is_valid($navigation_category));
        $this->navigation_category = $navigation_category;
    }

    function render() {
        $this->print_navigation_bar($this->navigation_category, $this->prefix);
    }

    /**
     * For debugging, make sure the selected nav page is valid
     *
     * @param $selected_page emun/int - enumeration of the page that should be active on the navbar
     * @return bool TRUE if the page is valid
     */
    function selected_page_is_valid($selected_page) {
        if ($selected_page < 0) {
            return ($selected_page == self::NAV_NOT_SPECIFIED);
        }

        return (($selected_page < self::NAV_COUNT) &&
            ($selected_page != self::NAV_INVALID0) &&
            ($selected_page != self::NAV_INVALID1));
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
            <li $selected_status><a href="{$prefix}{$link}" accesskey="{$access_key}">{$desc}</a></li>

EOT;
    }

    function print_subnavigation_element($prefix, $link, $desc) {
        $selected = false;
        if (is_array($desc)) {
            $name = WebUtils::inst()->toHtml($desc[0]);
            $extra_style = "style=\"padding-left: {$desc[1]}\"";
            $depth = $desc[2];
            $has_children = $desc[3];
            $selected = $desc[5];
        }
        else {
            $name = WebUtils::inst()->toHtml($desc);
            $extra_style = "";
            $depth = -1;
            $has_children = false;
        }

        $subnav_selected = "";
        if ($selected || is_int(strpos($_SERVER['REQUEST_URI'], $link))) {
            $subnav_selected = " subnav-selected";
        }

        $class = "subnav{$subnav_selected}";

        print <<<EOT
                        <li class="{$class}" {$extra_style}><a href="{$prefix}{$link}" class="{$class}">{$name}</a></li>

EOT;
    }

    function print_navigation_element($prefix, $selected_page, $link, $desc, $submenus = array()) {
        $this_element_is_selected = "$this->access_key" == "$selected_page";

        if ($this_element_is_selected) {
            $selected_status = 'class="topnav-selected"';
        }
        else {
            $selected_status = 'class="topnav"';
        }

        print <<<EOT
                <li $selected_status>
                    <a href="{$prefix}{$link}" accesskey="{$this->access_key}" {$selected_status}>{$desc}</a>

EOT;

        if ($this_element_is_selected) {
            if (count($submenus) > 0) {
                print '                    <ul class="subnav">'."\n";

                foreach($submenus as $key => $value) {
                    $this->print_subnavigation_element($prefix, $key, $value);
                }

                print "                    </ul>\n";
            }
        }

        $this->access_key = $this->access_key + 1;

        print "                </li>\n";
    }

    function get_sim_categories_for_navbar($prefix) {
        $this_url = $this->nav_uri;

        // Go through all categories
        $cats = $this->hierarchical_categories->getDescendants();
        $path_to_current = array();
        foreach ($cats as $id => $hier) {
            if ($hier['cat_id'] == 0) {
                // Skip the root
                continue;
            }

            // Get URL
            $cats[$id]['cat_url'] = CategoryUtils::inst()->getUrlByCatId($hier['cat_id']);
            if (is_int(strpos($this_url, $cats[$id]['cat_url']))) {
                // If we're on this category page, remember for later
                $path_to_current = $this->hierarchical_categories->getPath($hier['cat_id'], true);
            }
        }

        $categories = array();
        $skip_children = false;
        $parent_skip = -1;
        foreach ($cats as $level) {
            if ($level['cat_id'] == 0) {
                // Skip the root
                continue;
            }

            if (!$level['cat_is_visible']) {
                // Skip invisible categories
                continue;
            }

            // Determine if we're in skip children mode
            if ($skip_children) {
                if ($this->hierarchical_categories->isDescendantOf($level['cat_id'], $parent_skip)) {
                    // This is a subcat of the cat being skipped, keep skipping
                    continue;
                }

                // Turn off skip children mode
                $skip_children = false;
                $parent_skip = -1;
            }

            // Get info about cat
            $cat_id = $level['cat_id'];
            $cat_name = $level['cat_name'];
            $depth = count($this->hierarchical_categories->getPath($cat_id));
            $has_children = count($this->hierarchical_categories->getDescendants($cat_id, false, true)) > 0;
            $link = CategoryUtils::inst()->getUrlByCatId($cat_id);
            $pad_left = 0 + (($depth) * 20)."px";
            
            $selected = false;
            $stripped_this_url = substr($this_url, strrpos($this_url, 'simulations/'));
            if (is_int(strpos($stripped_this_url, $link))) {
                $selected = true;
            }

            // Record info for submenu
            $categories["$link"] = array($cat_name, $pad_left, $depth, $has_children, false, $selected);

            // If this cat has children, and they are not part of the hiearchy of this page, skip them
            if ($this->hierarchical_categories->numChildren($level['cat_id']) > 0) {
                if (!in_array($level['cat_id'], array_keys($path_to_current))) {
                    $skip_children = true;
                    $parent_skip = $level['cat_id'];
                }
            }
        }

        // Add the translated sims
        $categories["simulations/translations.php"] = "Translated Sims";

        // Return the result
        return $categories;
    }

    function print_navigation_bar($selected_page, $prefix) {
        print <<<EOT
        <div id="localNav">
            <ul class="topnav">

EOT;

        $this->print_navigation_element(
            $prefix,
            $selected_page,
            "index.php",
            "Home"
        );

        $default_category = CategoryUtils::inst()->getDefaultCategory();
        $def_name = $default_category['cat_name'];
        $encoded_default_cat_name = WebUtils::inst()->encodeString($def_name);
        $this->print_navigation_element(
            $prefix,
            $selected_page,
            "simulations/index.php?cat=".$encoded_default_cat_name,
            "Simulations",
            $this->get_sim_categories_for_navbar($prefix)
        );

        $teacher_ideas_subs = array();

        $teacher_ideas_subs['teacher_ideas/browse.php']                 = 'Browse';
        $teacher_ideas_subs['teacher_ideas/workshops.php']              = 'Workshops';
        $teacher_ideas_subs['teacher_ideas/contribute.php']             = 'Submit an Activity';
        $teacher_ideas_subs['teacher_ideas/manage-contributions.php']   = 'My Activities';
        $teacher_ideas_subs['teacher_ideas/user-edit-profile.php']      = 'My Profile';

        if ($this->page->authenticate_get_level() > SitePage::AUTHLEVEL_NONE) {
            $teacher_ideas_subs['teacher_ideas/user-logout.php'] = 'Logout';
        }

        $this->print_navigation_element(
            $prefix,
            $selected_page,
            "teacher_ideas/index.php",
            "Teacher Ideas &amp; Activities",
            $teacher_ideas_subs
        );

        $this->print_navigation_element(
            $prefix, $selected_page,
            "get_phet/index.php",
            "Run our Simulations",
            array(
                'simulations/index.php?cat='.$encoded_default_cat_name => 'On Line',
                'get_phet/full_install.php' => 'Full Install',
                'get_phet/simlauncher.php'  => 'One at a Time'
            )
        );

        $this->print_navigation_element(
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

        $this->print_navigation_element(
            $prefix,
            $selected_page,
            "contribute/index.php",
            "Contribute",
            array(
                'contribute/translation-utility.php'    => 'PhET Translation Utility'
            )
        );

        $this->print_navigation_element(
            $prefix,
            $selected_page,
            "research/index.php",
            "Research"
        );

        $this->print_navigation_element(
            $prefix,
            $selected_page,
            "about/index.php",
            "About PhET",
            array(
                'about/source-code.php'         => 'Source Code',
                'about/news.php'        => 'News',
                'about/contact.php'     => 'Contact',
                'about/who-we-are.php'  => 'Who We Are',
                'about/licensing.php'   => 'Licensing'// ,
                //                 'sponsors/index.php'    => 'Sponsors'
            )
        );

        $contributor_is_team_member = 0;
        if ($this->page->authenticate_user_is_authorized()) {
            $contributor = $this->page->authenticate_get_user();
            $contributor_is_team_member = $contributor['contributor_is_team_member'];
        }

        if (($contributor_is_team_member == 1)) {
            $this->print_navigation_element(
                $prefix,
                $selected_page,
                "admin/index.php",
                "Administrative",
                array(
                    'admin/new-sim.php'                         => 'Add Simulation',
                    'admin/choose-sim.php'                      => 'Edit Simulation',
                    'admin/list-sims.php'                       => 'List Simulations',
                    'admin/organize-cats.php'                   => 'Organize Categories',
                    'admin/organize-sims.php'                   => 'Organize Simulations',
                    'admin/manage-contributors.php'             => 'Manage Contributors',
                    'admin/export-contributors-page.php'        => 'Export Contributors',
                    'admin/manage-comments.php'                 => 'Manage Comments',
                    'admin/view-gold-star-nominations.php'      => 'View Gold Star Nominations',
                    'admin/update-settings.php'                 => 'Settings for Updates',
                    'admin/manage-db.php'                       => 'Manage Database',
                    'admin/cache-clear.php?cache=sims'          => 'Clear the simulation cache',
                    'admin/cache-clear.php?cache=teacher_ideas' => 'Clear the activities cache',
                    'admin/cache-clear.php?cache=admin'         => 'Clear the admin directory cache',
                    'admin/cache-clear.php?cache=all'           => 'Clear all the caches'
                    )
            );
        }

        if (($this->page->authenticate_get_level())) {
            $this->print_navigation_element(
                $prefix,
                $selected_page,
                "teacher_ideas/user-edit-profile.php",
                "My Profile",
                array()
            );
        }

        print <<<EOT
            </ul>

            <div id="spons">
                <h4>Principal Sponsors</h4>

                <div class="header">
                </div>
                <dl>
                    <dt>
                        <a href="http://www.hewlett.org/">The William and Flora Hewlett Foundation</a>
                    </dt>

                    <dd>
                        <a href="http://www.hewlett.org/">
                            <img src="{$prefix}images/hewlett-logo.jpg" alt="The Hewlett Logo"/>
                        </a>
                        <br />

                        <br />
                        Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.
                    </dd>

                    <dt>
                        <a href="http://www.nsf.gov/">
                            <img class="spons" src="{$prefix}images/nsf-logo.gif" alt="The NSF Logo"/>National Science Foundation</a>
                    </dt>

                    <dd>
                        <br />
                        An independent federal agency created by Congress in 1950 to promote the progress of science.<br />
                        <br />
                    </dd>

                    <dt>
                        <a href="http://ecsme.ksu.edu.sa/en/index.cfm" title="Click here to visit the Excellence Center of Science and Mathematics Education at King Saud University">
                            <img src="{$prefix}images/ECSME-combined-logo-small.jpg" height="38" alt="Excellence Center of Science and Mathematics Education at King Saud University Logo"/>Excellence Center of Science and Mathematics Education at King Saud University</a>
                    </dt>

                    <dd>
                        <br />
                        King Saud University seeks to become a leader in educational and technological innovation, scientific discovery and creativity through fostering an atmosphere of intellectual inspiration and partnership for the prosperity of society.<br />
                        <br />
                    </dd>
                    <dt>
                        <a href="{$prefix}sponsors/index.php">
                        <img src="{$prefix}images/other-sponsors.gif" alt="Other Sponsors Logo"/></a>
                    </dt>

                </dl>
            </div>
        </div>

EOT;
    }

}

?>
