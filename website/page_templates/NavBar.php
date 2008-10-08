<?php

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
define("NAV_SIMULATIONS",  2);
define("NAV_TEACHER_IDEAS",  3);
define("NAV_GET_PHET",  4);
define("NAV_TECH_SUPPORT",  5);
define("NAV_CONTRIBUTE",  6);
define("NAV_RESEARCH",  7);
define("NAV_ABOUT_PHET",  8);
define("NAV_ADMIN",  9);
define("NAV_COUNT",  10);

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."admin/hierarchical-categories.php");

class NavBar {
    // Page navbar will be on
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
            <li $selected_status><a href="{$prefix}{$link}" accesskey="{$access_key}">{$desc}</a></li>

EOT;
    }

    function print_subnavigation_element($prefix, $link, $desc) {
        if (is_array($desc)) {
            $name = $desc[0];
            $extra_style = "style=\"padding-left: {$desc[1]}\"";
        }
        else {
            $name = $desc;
            $extra_style = "";
        }

        print <<<EOT
                        <li class="subnav" {$extra_style}><a href="{$prefix}{$link}" class="subnav">{$name}</a></li>

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
        // Setting this variable here is explicitly and intentionally to
        // get around good class encapsulation.  The "walk" function below
        // needs the user function to be specified with a function in
        // the global scope.
        $this->sim_categories = array();

        // Get the hierarchical categories
        $hier_cats = new HierarchicalCategories();

        // Traverse through the hierarchical categories
        $hier_cats->walk('get_sim_categories_for_navbar_callback', $this);

        // Add the translated sims
        $this->sim_categories["simulations/translations.php"] = "Translated Sims";

        // Return the categories to the local scope and eliminate the class var
        $categories = $this->sim_categories;
        unset($this->sim_categories);

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

        $this->print_navigation_element(
            $prefix,
            $selected_page,
            "simulations/index.php?cat=Top_Simulations",
            "Simulations",
            $this->get_sim_categories_for_navbar($prefix)
        );

        $teacher_ideas_subs = array();

        $teacher_ideas_subs['teacher_ideas/browse.php']                 = 'Browse';
        $teacher_ideas_subs['teacher_ideas/workshops.php']              = 'Workshops';
        $teacher_ideas_subs['teacher_ideas/contribute.php']             = 'Submit an Activity';
        $teacher_ideas_subs['teacher_ideas/manage-contributions.php']   = 'My Activities';
        $teacher_ideas_subs['teacher_ideas/user-edit-profile.php']      = 'My Profile';

        if ($this->page->authenticate_user_is_authorized()) {
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
                'simulations/index.php?cat=Top_Simulations' => 'On Line',
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
                'about/faq.php'         => 'FAQ',
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
                    'admin/new-sim.php'             => 'Add Simulation',
                    'admin/choose-sim.php'          => 'Edit Simulation',
                    'admin/list-sims.php'           => 'List Simulations',
                    'admin/organize-cats.php'       => 'Organize Categories',
                    'admin/organize-sims.php'       => 'Organize Simulations',
                    'admin/manage-contributors.php' => 'Manage Contributors',
                    'admin/manage-comments.php'     => 'Manage Comments',
                    'admin/manage-db.php'           => 'Manage Database',
                    'admin/compose-newsletter.php'  => 'Compose Newsletter',
                    'admin/view-statistics.php'     => 'View Statistics',
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

            <div id="sponsors">
                <h4>Principal Sponsors</h4>

                <div class="header">
                </div>
                <dl>
                    <dt>
                        <a href="http://www.hewlett.org/Default.htm">The William and Flora Hewlett Foundation</a>
                    </dt>

                    <dd>
                        <a href="http://www.hewlett.org/Default.htm">
                            <img src="{$prefix}images/hewlett-logo.jpg" alt="The Hewlett Logo"/>
                        </a>
                        <br />

                        <br />
                        Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.
                    </dd>

                    <dt>
                        <a href="http://www.nsf.gov/">
                            <img class="sponsors" src="{$prefix}images/nsf-logo.gif" alt="The NSF Logo"/>National Science Foundation</a>
                    </dt>

                    <dd>
                        <br />
                        An independent federal agency created by Congress in 1950 to promote the progress of science.<br />
                        <br />
                    </dd>

                    <dt>
                        <a href="http://www.ksu.edu.sa/">
                            <img class="sponsors" src="{$prefix}images/ksu-logo.gif" alt="The King Saud Logo" />King Saud University</a>
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

// This function needs to be outside the class so that it can be called
// from HierarchicalCategories::walk(), which is a different function in
// a different class in a different file.  Static class functions don't work.
function get_sim_categories_for_navbar_callback($user_var, $category, $depth, $has_children) {
    $cat_id   = $category['cat_id'];
    $cat_name = $category['cat_name'];

    $link = sim_get_category_url_by_cat_id($cat_id);

    $pad_left = 0 + (($depth - 1) * 20)."px";

    $user_var->sim_categories["$link"] = array($cat_name, $pad_left);
}

?>