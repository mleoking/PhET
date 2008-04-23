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

        // Authenticate the user
        $this->user = null;
        $this->required_authentication_level = $authentication_level;
        $this->authenticate_user();

        parent::__construct($page_title, $nav_selected_page, $referrer, $cache_page, $base_title);
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

    function update() {
        return parent::update();
    }

    function print_invalid_info() {
        print <<<EOT
        <p>The information needed was either invalid or not specified.  Please go back and try again.</p>

EOT;

    }

    function render_content() {
        // Make sure the authentication level is at least what is required to see this page
        if ($this->authentication_level >= $this->required_authentication_level) {
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

}

?>