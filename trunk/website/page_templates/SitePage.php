<?php

// BasePage.php is getting  a little more crowded than I would like,
// so enter SitePage, whith should have been here from the beginning.
// Since I'm working on something else, this will be mostly a stud until
// I can separate some things out.

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/BasePage.php");
include_once(SITE_ROOT."page_templates/NavBar.php");
include_once(SITE_ROOT."admin/authentication.php");

// Required athentication levels to view the page
// The numbers must increase so that they may be compared
define("AUTHLEVEL_NONE", 0);
define("AUTHLEVEL_USER", 1);
define("AUTHLEVEL_TEAM", 2);

// Value types, used to verify that desired query string info is presentt
define("VT_NONE", 0);       // Do not check this value
define("VT_ISSET", 1);      // Only check if it is set, all further checks include this check
define("VT_VALID_CONTRIBUTION_ID", 2);      // Check that the contribution_id is valid
define("VT_VALID_COMMENT_ID", 3);      // Check that the comment_id is valid
define("VT_VALID_EMAIL", 4);      // Check that the email address is valid

class SitePage extends BasePage {
    protected $required_authentication_level;
    protected $authentication_level;

    // bool - true if all needed information is specified and valid
    protected $valid_info;

    // contributor_array - The user who is logged in (NULL if no user is logged on)
    protected $user;

    // Default is to cache the page, this allows for an override
    private $cache_page;

    private $navigation_bar;

    function __construct($page_title,
                         $nav_selected_page,
                         $referrer,
                         $authentication_level = AUTHLEVEL_NONE,
                         $cache_page = true,
                         $base_title = "") {
        assert(($authentication_level == AUTHLEVEL_NONE) ||
            ($authentication_level == AUTHLEVEL_USER) ||
            ($authentication_level == AUTHLEVEL_TEAM));

        // Assume the info needed is all correct
        $this->valid_info = true;

        $this->cache_page = $cache_page;

        // Authenticate the user
        $this->user = null;
        $this->required_authentication_level = $authentication_level;

        session_start();
        $this->authenticate_user();
        cache_setup_page_name();
        session_write_close();

        // Set up the nav bar
        $this->navigation_bar = new NavBar($this, $nav_selected_page, SITE_ROOT);

        // Set up the parent
        parent::__construct($page_title, $referrer, $base_title);

        // Set default site settings

        // The main stylesheet
        $this->add_stylesheet("css/main.css");

        // Setup java scripts
        // NOTE: order is important
         $this->javascript_files = $this->add_javascript_file(
             array(
                "js/jquery.pack.js",
                "js/jquery.MultiFile.js",
                "js/jquery_std.js",
                "js/jquery.autocomplete.js",
                "js/http.js",
                "js/form-validation.js",
                "js/multi-select.js",
                "js/phet-scripts.js",
                "js/navigation.js",
                "js/submit.js"
                )
             );

        $this->add_javascript_header_script(
            array(
                "                    select_current_navbar_category('{$_SERVER["REQUEST_URI"]}');",
                // Old method used a lot of $() to find inputs and place a regex
                // validation pattern in it, which really bogged down on big pages.
                // Replaced with a method to just look for inputs and moved it to
                // form-validation.js to clean up a little.
                // On my computer, improvement on big pages is from ~5s to 0.005s,
                // small pages from about 0.200s to 0.0s (time too small to measure)
                "                    setup_input_validation_patterns();",
                "                    validate_and_setup_validation_triggers();",
                "                    setup_submit_form_validation();"
                )
            );

    }

    function authenticate_user() {
        $user_authenticated = auth_do_validation();
        if ($user_authenticated == false) {
            $this->authentication_level = AUTHLEVEL_NONE;
        }
        else {
            // If someone is logged in, check if they are a team member
            $this->user = contributor_get_contributor_by_username(auth_get_username());
            if ($this->user["contributor_is_team_member"]) {
                $this->authentication_level = AUTHLEVEL_TEAM;
            }
            else {
                $this->authentication_level = AUTHLEVEL_USER;
            }
        }

        return $this->authentication_level;
    }

    function authenticate_user_is_authorized() {
        // Make sure the authentication level is at least what is required to see this page
        if ($this->authentication_level >= $this->required_authentication_level) {
            return true;
        }
        else {
            return false;
        }
    }

    function authenticate_get_user() {
        return $this->user;
    }

    function authenticate_get_level() {
        return $this->authentication_level;
    }

    function print_javascript_error() {
        print <<<EOT
            <h2>Error in submission</h2>

            <p>Sorry, there was as error with your submission.</p>

            <p>JavaScript is not enabled.</p>
            <p>You must have JavaScript enabled to submit information to PhET.  For directions on how to check this, <a href="{$this->prefix}tech_support/support-javascript.php  ">go here</a>.</p>

EOT;
    }

    function javascript_submit_is_valid() {
        // Make sure that the page has the special tag that suggests JavaScript
        // is enabled.  If JavaScript is not enabled, this special hidden input will
        // not be created.  Of course, this is circumventable, but this eliminates
        // most of the non-malicious people.
        //
        // This function should only be used on a page where you go after hitting submit
        // (and may not yet be consistent everywhere
        if (!isset($_REQUEST['submition']) || ($_REQUEST['submition'][0] != 'x')) {
            return false;
        }

        // else assume OK
        return true;
    }

    /**
     * Check for the existance (and type) of the specified keys in the array
     * TODO: redo this function, the $required option is silly and a kludge for one situation
     *
     * @param $vars array key => value_type
     */
    function validate_array($check_array, $validation, $required = true) {
        if (is_null($check_array) || (count($check_array) == 0)) {
            // Nothing to check
            return true;
        }

        foreach ($validation as $key => $type) {
            if ($type == VT_NONE) {
                // Skip checking this element
                continue;
            }
            else if (!isset($check_array[$key])) {
                $this->valid_info = !$required;
                return false;

                if ($type == VT_ISSET) {
                    // Only check if it is set, no more
                    continue;
                }
            }

            switch ($type) {
                case VT_VALID_CONTRIBUTION_ID:
                    if (!contribution_get_contribution_by_id($check_array[$key])) {
                        $this->valid_info = !$required;
                        return false;
                    }
                    break;

                case VT_VALID_COMMENT_ID:
                    if (!comment_id_is_valid($check_array[$key])) {
                        $this->valid_info = !$required;
                        return false;
                    }
                    break;

                case VT_VALID_EMAIL:
                    $regex_result = preg_match("/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/", $check_array[$key]);
                    if (!$regex_result) {
                        $this->valid_info = !$required;
                        return false;
                    }
                    break;

                default:
                    assert(false);
                    break;
            }
        }

        // Everything evaluated correctly
        return true;
    }

    /*
     * Do some checks on the page.  Will return false if (in order):
     *    BasePage::update() return false
     *    user does not have proper authorization
     *    cache is active and does not have a valid page
     * 
     * @return bool true if page should be updated (this is a suggestion)
     */
    function update() {
        // First, give the parent a change to update, if it fails, pass it down
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        //
        // But if it succeeds, do some more tests

        // Make sure the authentication level is at least what is required to see this page
        if (!$this->authenticate_user_is_authorized()) {
            return false;
        }

        // Check if this page has something valid in the cache
        if ($this->cache_page && cache_has_valid_page()) {
            return false;
        }

        return true;
    }

    function print_invalid_info() {
        print <<<EOT
        <p>The information needed was either invalid or not specified.  Please go back and try again.</p>

EOT;
    }

    function open_xhtml_body() {
        parent::open_xhtml_body();

        print <<<EOT
        <div id="content">
            <div class="main">

EOT;
    }

    function render_navigation_bar() {
        $this->navigation_bar->render();
    }

    function render_content() {
        // Make sure the authentication level is at least what is required to see this page
        if ($this->authenticate_user_is_authorized()) {
            // Authentication level is OK

            // Last render a message if the info is invalid
            if (!$this->valid_info) {
                $this->print_invalid_info();
                return FALSE;
            }

            return parent::render_content();
        }

        // Handle mismatch between the required and actual authentication level
        if ($this->authentication_level == AUTHLEVEL_NONE) {
            // Need to login
            print_login_and_new_account_form("", "", $this->referrer);
            return FALSE;
        }
        else {
            // Here if the page needs a team member level access, and user only has user level
            print "<h2>Authorization Required</h2>";
            print "<p>You need to be a PhET team member to view this page.</p>\n";
            return FALSE;
        }

        // Last render a message if the info is invalid
        if (!$this->valid_info) {
            $this->print_invalid_info();
            return FALSE;
        }

        return TRUE;
    }

    function render() {
        if ($this->cache_page) {
            cache_auto_start();
        }

        parent::render();

        if ($this->cache_page) {
            cache_auto_end();
        }
    }

    function get_login_panel() {
        $php_self = $_SERVER['REQUEST_URI'];

        // Don't require authentication, but do it if the cookies are available:
        $contributor_authenticated = $this->authenticate_get_level() != AUTHLEVEL_NONE;

        if (!$contributor_authenticated) {
            $cooked_php_self = htmlspecialchars($php_self);
            $utility_panel_html = <<<EOT
            <a href="{$this->prefix}teacher_ideas/login-and-redirect.php?url=$cooked_php_self">Login / Register</a>

EOT;
        }
        else {
            $contributor = $this->user;
            $contributor_name = $contributor['contributor_name'];

            $formatted_php_self = format_string_for_html($php_self);

            $utility_panel_html = <<<EOT
            Welcome <a href="{$this->prefix}teacher_ideas/user-edit-profile.php">{$contributor_name}</a> - <a href="{$this->prefix}teacher_ideas/user-logout.php?url={$formatted_php_self}">Logout</a>

EOT;
        }

        return $utility_panel_html;
    }

}

?>