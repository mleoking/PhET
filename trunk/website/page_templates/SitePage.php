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

class SitePage extends BasePage {
    protected $required_authentication_level;
    protected $authentication_level;

    function __construct($page_title, $nav_selected_page, $referrer, $authentication_level = SP_AUTHLEVEL_NONE, $base_title = "") {
        assert(($authentication_level == SP_AUTHLEVEL_NONE) ||
            ($authentication_level == SP_AUTHLEVEL_USER) ||
            ($authentication_level == SP_AUTHLEVEL_TEAM));

        // Authenticate the user
        $this->required_authentication_level = $authentication_level;
        $this->authenticate_user();

        parent::__construct($page_title, $nav_selected_page, $referrer, $base_title);
    }

    function authenticate_user() {
        $user_authenticated = auth_do_validation();
        if ($user_authenticated == false) {
            if ($this->authentication_level == SP_AUTHLEVEL_NONE) {
            }
            return;

            $this->authentication_level = SP_AUTHLEVEL_NONE;
            return $this->authentication_level;
        }

        // If someone is logged in, check if they are a team member
        $contributor = contributor_get_contributor_by_username(auth_get_username());
        if ($contributor["contributor_is_team_member"]) {
            $this->authentication_level = SP_AUTHLEVEL_TEAM;
        }
        else {
            $this->authentication_level = SP_AUTHLEVEL_USER;
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

    function update() {
        return parent::update();
    }

    function render_content() {
        // Make sure the authentication level is at least what is required to see this page
        if ($this->authentication_level >= $this->required_authentication_level) {
            return parent::render_content();
        }

        // Handle mismatche between the required and actual authentication level
        if ($this->authentication_level == SP_AUTHLEVEL_NONE) {
            print_login_and_new_account_form("", "", $this->referrer);
        }
        // Only will be here if the required level is team and acutal is user
        else {
            print "<h2>Authorization Required</h2>";
            print "<p>You need to be a PhET team member to view this page.</p>\n";
        }

        return FALSE;
    }

}

?>