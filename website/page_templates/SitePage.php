<?php

// BasePage.php is getting  a little more crowded than I would like,
// so enter SitePage, whith should have been here from the beginning.
// Since I'm working on something else, this will be mostly a stud until
// I can separate some things out.

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/BasePage.php");

// Required athentication levels to view the page
// The numbers must increase so that they may be compared
define("SP_AUTHLEVEL_NONE", 0);
define("SP_AUTHLEVEL_USER", 1);
define("SP_AUTHLEVEL_TEAM", 2);

// Value types, used to verify that desired query string info is presentt
define("VT_NONE", 0);       // Do not check this value
define("VT_ISSET", 1);      // Only check if it is set, all further checks include this check
define("VT_VALID_CONTRIBUTION_ID", 2);      // Only check if it is set, all further checks include this check
define("VT_VALID_COMMENT_ID", 3);      // Only check if it is set, all further checks include this check

class SitePage extends BasePage {
    protected $required_authentication_level;
    protected $authentication_level;

    // bool - true if all needed information is specified and valid
    protected $valid_info;

    // contributor_array - The user who is logged in (NULL if no user is logged on)
    protected $user;

    // Default is to cache the page, this allows for an override
    private $cache_page;

    function __construct($page_title,
                         $nav_selected_page,
                         $referrer,
                         $authentication_level = SP_AUTHLEVEL_NONE,
                         $cache_page = true,
                         $base_title = "") {
        assert(($authentication_level == SP_AUTHLEVEL_NONE) ||
            ($authentication_level == SP_AUTHLEVEL_USER) ||
            ($authentication_level == SP_AUTHLEVEL_TEAM));

        // Assume the info needed is all correct
        $this->valid_info = true;

        $this->cache_page = $cache_page;

        // Authenticate the user
        $this->user = null;
        $this->required_authentication_level = $authentication_level;
        $this->authenticate_user();

        // Set up the parent
        parent::__construct($page_title, $nav_selected_page, $referrer, $base_title);

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
            $this->authentication_level = SP_AUTHLEVEL_NONE;
        }
        else {
            // If someone is logged in, check if they are a team member
            $this->user = contributor_get_contributor_by_username(auth_get_username());
            if ($this->user["contributor_is_team_member"]) {
                $this->authentication_level = SP_AUTHLEVEL_TEAM;
            }
            else {
                $this->authentication_level = SP_AUTHLEVEL_USER;
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

    function print_javascript_error() {
        print <<<EOT
            <h2>Error in submission</h2>

            <p>Sorry, there was as error with your submission..</p>

            <p>JavaScript is not enabled.</p>
            <p>You must have JavaScript enabled to submit information to PhET.  For directions on how to check this, <a href="../tech_support/support-javascript.php  ">go here</a>.</p>

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
     *
     * @param $vars array key => value_type
     */
    function validate_array($check_array, $validation) {
        if (is_null($check_array) || (count($check_array) ==0)) {
            // Nothing to check
            return true;
        }

        foreach ($validation as $key => $type) {
            if ($type == VT_NONE) {
                // Skip checking this element
                continue;
            }
            else if (!isset($check_array[$key])) {
                $this->valid_info = false;
                return false;

                if ($type == VT_ISSET) {
                    // Only check if it is set, no more
                    continue;
                }
            }

            switch ($type) {
                case VT_VALID_CONTRIBUTION_ID:
                    if (!contribution_get_contribution_by_id($check_array[$key])) {
                        $this->valid_info = false;
                        return false;
                    }
                    break;

                case VT_VALID_COMMENT_ID:
                    if (!comment_id_is_valid($check_array[$key])) {
                        $this->valid_info = false;
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
        if ($this->authentication_level == SP_AUTHLEVEL_NONE) {
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

}

?>