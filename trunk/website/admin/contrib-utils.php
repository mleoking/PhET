<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/authentication.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/nominate-utils.php");

    define("BROWSE_CACHE",                 'browse-pages');
    define('DEFAULT_CONTRIBUTOR_DESC',  'I am a teacher who uses PhET in my classes');

    function contribution_search_for_contributions($search_for) {
        return db_search_for(
            'contribution',
            $search_for,
            array('contribution_title', 'contribution_desc', 'contribution_keywords', 'contribution_authors')
        );
    }

    function contribution_get_comments($contribution_id) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $comments = array();

        $result = db_exec_query("SELECT * FROM `contribution_comment`, `contributor` WHERE `contribution_comment`.`contributor_id` = `contributor`.`contributor_id` AND `contribution_comment`.`contribution_id`='$contribution_id'  ");

        while ($comment = mysql_fetch_assoc($result)) {
            $comments[] = $comment;
        }

        stripslashes_deep($comments);

        return $comments;
    }

    function contribution_add_comment($contribution_id, $contributor_id, $contribution_comment_text) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        // Have to do a homegrown because it doesn't support passing a MYSQL function
        $sql = "INSERT INTO `contribution_comment` ".
            "(`contribution_comment_text`, ".
            "`contribution_comment_created`, ".
            "`contribution_comment_updated`, ".
            "`contribution_id`, ".
            "`contributor_id`) ".
            "VALUES (".
            "'".mysql_real_escape_string($contribution_comment_text, $connection)."',".
            "NOW(),".
            "NOW(),".
            "{$contribution_id},".
            "{$contributor_id}".
            ")";
        $result = db_exec_query($sql);
        return mysql_insert_id($connection);
    }

    function contribution_update_comment($comment_id, $contribution_comment_text) {
        return db_update_table("contribution_comment", array("contribution_comment_text" => $contribution_comment_text), "contribution_comment_id", $comment_id);
    }

    function contribution_delete_comment($contribution_comment_id) {
        return db_delete_row('contribution_comment', array( 'contribution_comment_id' => $contribution_comment_id ) );
    }

    function contribution_get_files_listing_html($contribution_id) {
        $prefix = SITE_ROOT;

        $files_html = '<p>No files</p>';

        $files = contribution_get_contribution_file_infos($contribution_id);

        if (count($files) > 0) {
            $files_html = '<ul>';

            foreach($files as $file) {
                $contribution_file_id   = format_string_for_html($file['contribution_file_id']);
                $contribution_file_name = format_string_for_html($file['contribution_file_name']);
                $contribution_file_size = $file['contribution_file_size'];

                $kb = format_string_for_html(ceil($contribution_file_size / 1024));

                $files_html .= "<li><a href=\"{$prefix}admin/get-contribution-file.php?contribution_file_id=$contribution_file_id\">".
                               "$contribution_file_name</a>".
                               " - $kb KB</li>";
            }

            $files_html .= "</ul>";
        }

        return $files_html;
    }

    /**
     * This function is unused
     *
     * @param unknown_type $contribution_id
     * @return unknown
     */
    function contribution_get_simulations_listing_html($contribution_id) {
        $simulations_html = "<ul>";

        $simulation_listings = contribution_get_associated_simulation_listings($contribution_id);

        foreach($simulation_listings as $simulation_listing) {
            //eval(get_code_to_create_variables_from_array($simulation_listing));
            $sim_id = $simulation_listing["sim_id"];
            $simulation = sim_get_sim_by_id($sim_id);

            //eval(get_code_to_create_variables_from_array($simulation));
            $sim_name = $simulation["sim_name"];
            $simulation_contribution_id = $simulation["simulation_contribution_id"]; 
            $delete = "<input name=\"delete_simulation_contribution_id_{$simulation_contribution_id}\" type=\"submit\" value=\"Delete\" />";

            $sim_url = sim_get_url_to_sim_page($sim_id);

            $simulations_html .= "<li>$delete <a href=\"$sim_url\">".format_string_for_html($sim_name)."</a></li>";
        }

        $simulations_html .= "</ul>";

        return $simulations_html;
    }

    /**
     * Determine if a contribution is a gold start contribution
     *
     * @param array $contributionC(assoc array from a db query)
     * @return bool - true of the contribution is a gold star contribution
     */
    function contribution_is_gold_star_contribution($contribution) {
        return $contribution['contribution_is_gold_star'] == '1';
    }

    /**
     * Return HTML to display a gold star image
     *
     * @param integer $image_width - width to display the image (current actual size is 112px wide)
     * @return HTML <img ...>  code to display the image
     */
    function contribution_get_gold_star_html($image_width = 37) {
        // TODO: add height element
        return "<a href=\"".SITE_ROOT."about/legend.php\"><img src=\"".SITE_ROOT."images/gold-star.jpg\" width=\"$image_width\" alt=\"Image of Gold Star\" title=\"Gold Star Contribution: This contribution has received a Gold Star for its quality and usefulness to many teachers.\" /></a>";
    }

    /**
     * Returns the HTML code to display the gold star if the contribution is a gold star contribution
     *
     * @param array $contribution assoc array from a db query
     * @param int $image_width width of the gold star image
     * @return string <img ...> HTML code if it is a gold star, else blank
     */
    function contribution_get_gold_star_html_for_contribution($contribution, $image_width = 37) {
        if (contribution_is_gold_star_contribution($contribution)) {
            return contribution_get_gold_star_html($image_width);
        }
        else {
            return "";
        }
    }

    function contribution_print_standards_checkbox($encoded_string, $count = 1, $read_only = false) {
        for ($i = 0; $i < $count; $i++) {
            print '<td align="center">';
            print_string_encoded_checkbox('standards', $encoded_string, $read_only);
            print '</td>';
        }
    }

    function contribution_print_standards_compliance($contribution_standards_compliance, $read_only = false) {
        print <<<EOT
        <div  id="nationalstandards">
        <table>
            <thead>
                <tr>
                    <td>&nbsp;</td>

                    <td colspan="3">Content Level</td>
                </tr>

                <tr>
                    <td>Content Standard</td>

                    <td>K-4</td>

                    <td>5-8</td>

                    <td>9-12</td>
                </tr>
            </thead>

            <tbody>

                <tr>
                    <td>Science as Inquiry - A</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Physical Science - B</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Life Science - C</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Earth &amp; Space Science - D</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Science &amp; Technology - E</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>Science in Personal and Social Perspective - F</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>

                <tr>
                    <td>History and Nature of Science - G</td>

EOT;

                    contribution_print_standards_checkbox($contribution_standards_compliance, 3, $read_only);

                    print <<<EOT
                </tr>
            </tbody>
        </table>
        </div>

EOT;
    }

    function contribution_contribution_form_has_files() {
        if ((isset($_FILES['contribution_file_url'])) && 
            ($_FILES['contribution_file_url'] != 4)) {
            return true;
        }
        else if ((isset($_FILES['MF__F_0_0'])) &&
                ($_FILES['MF__F_0_0']['error'] != 4)) { 
            return true;
        }
        else {
            return false;
        }
    }

    // TODO: return value too overloaded, refactor
    function contribution_add_all_form_files_to_contribution($contribution_id) {
        // Errors, format: "filename" => "Error reason"
        $errors = array();

        // First check if there is too much data
        if (!post_size_ok()) {
            $errors["GENERAL"] = "Size of file(s) exceeds limit of <strong>{$post_max_size}</strong>";
            return $errors;
        }

        if (isset($_FILES['contribution_file_url'])) {
            $file = $_FILES['contribution_file_url'];
        }
        else if (isset($_FILES['MF__F_0_0'])) {
            $file = $_FILES['MF__F_0_0'];
        }
        else {
            return false;
        }

        $name     = $file['name'];
        $type     = $file['type'];
        $tmp_name = $file['tmp_name'];
        $error    = $file['error'] !== 0;
        $error_code = $file['error'];

        if (!$error) {
            contribution_add_new_file_to_contribution($contribution_id, $tmp_name, $name);
        }
        else {
            switch ($error_code) {
                case UPLOAD_ERR_INI_SIZE:
                case UPLOAD_ERR_FORM_SIZE:
                    $errors[$file['name']] = "The uploaded file exceeds the max of ".ini_get("upload_max_filesize");
                    break;
                case UPLOAD_ERR_PARTIAL:
                    $errors[$file['name']] = "The file was only partially uploaded, please try again";
                    break;
                case UPLOAD_ERR_NO_FILE:
                    break;
                default:
                    $errors[$file['name']] = "There was a general error, please try again";
                    break;
            }
        }

        for ($i = 1; true; $i++) {
            $file_key = "MF__F_0_$i";

            if (!isset($_FILES[$file_key])) {
                break;
            }
            else {
                $file = $_FILES[$file_key];

                $name     = $file['name'];
                $type     = $file['type'];
                $tmp_name = $file['tmp_name'];
                $error    = $file['error'] !== 0;
                $error_code = $file['error'];

                if (!$error){
                    contribution_add_new_file_to_contribution($contribution_id, $tmp_name, $name);
                }
                else {
                    // Some error occurred; maybe user cancelled file
                    switch ($error_code) {
                        case UPLOAD_ERR_INI_SIZE:
                        case UPLOAD_ERR_FORM_SIZE:
                            $errors[$file['name']] = "The uploaded file exceeds the max of ".ini_get("upload_max_filesize");
                            break;
                        case UPLOAD_ERR_PARTIAL:
                            $errors[$file['name']] = "The file was only partially uploaded, please try again";
                            break;
                        case UPLOAD_ERR_NO_FILE:
                            break;
                        default:
                            $errors[$file['name']] = "There was a general error, please try again";
                            break;
                    }
                }
            }
        }

        return $errors;
    }

    function print_login_and_new_account_form($login_form_action, $new_account_form_action, $referrer, $intro_text = "", $hidden_inputs = "") {
        $error = "";
        if (auth_auth_error()) {
            $error = '<p class="error_text">'.auth_get_error()."</p>";
        }

        print <<<EOT
        <div id="twofacelogin" class="table_container">

            {$intro_text}
            <p>{$error}</p>

            <p>Required fields are marked with an asterisk (*).</p>

            <table class="top">
                <tr>
                    <td>

EOT;
        print_login_form_panel($login_form_action, $referrer, $hidden_inputs);
        print <<<EOT
                    </td>
                    <td>

EOT;
        print_new_account_form_panel($new_account_form_action, $referrer, $hidden_inputs);
        print <<<EOT
                    </td>
                </tr>

                <tr>
                    <td colspan="2">

                        <p class="footnote">Your email address will not be given to anyone, and is used strictly for login purposes.</p>
                    </td>
                </tr>
            </table>
        </div>

EOT;
    }

    function print_login_form_panel($form_action, $referrer, $hidden_inputs = "") {
        $site_root = SITE_ROOT;

        if (isset($GLOBALS['contributor_email'])) {
            $contributor_email         = $GLOBALS['contributor_email'];
        }
        else if (isset($_REQUEST['contributor_email'])) {
            $contributor_email         = $_REQUEST['contributor_email'];
        }
        else {
            $contributor_email         = '';
        }

        print <<<EOT
            <form method="post" action="{$form_action}">
                <fieldset>
                    <legend>Login</legend>

                    <div class="horizontal_center">
                        <table class="form">

                            <tr>
                                <td>email*</td>

                                <td>
                                    <input type="text" size="15" name="contributor_email" value="{$contributor_email}" class="always-enabled" />
                                </td>
                            </tr>

                            <tr>
                                <td>password*</td>

                                <td>
                                    <input type="password" size="15" name="contributor_password" class="always-enabled" />
                                </td>
                            </tr>

                            <tr>
                                <td colspan="2">
                                    <input type="hidden" name="referrer" value="{$referrer}" class="always-enabled" />
                                    {$hidden_inputs}
                                    <input type="submit" name="submit" value="Login" class="always-enabled auto-width" />
                                </td>
                            </tr>

                            <tr>
                                <td colspan="2" id="forgot_password">
                                    <a href="{$site_root}admin/forgot-password.php">Forgot your password?</a>
                                </td>
                            </tr>

                        </table>
                    </div>
                </fieldset>
            </form>

EOT;
    }

    function print_new_account_form_panel($form_action, $referrer, $hidden_inputs = "") {
        if (isset($GLOBALS['contributor_email'])) {
            // This should never be the case anymore
            $contributor_email         = format_string_for_html($GLOBALS['contributor_email']);
            $contributor_name          = format_string_for_html(get_global_opt('contributor_name'));
            $contributor_organization  = format_string_for_html(get_global_opt('contributor_organization'));
            $contributor_desc          = get_global_opt('contributor_desc');
        }
        else if (isset($_REQUEST['contributor_email'])) {
            $contributor_email         = format_string_for_html($_REQUEST['contributor_email']);
            $contributor_name          = format_string_for_html(get_request_opt('contributor_name'));
            $contributor_organization  = format_string_for_html(get_request_opt('contributor_organization'));
            $contributor_desc          = get_request_opt('contributor_desc');
        }
        else {
            $contributor_email         = '';
            $contributor_name          = '';
            $contributor_organization  = '';
            $contributor_desc          = DEFAULT_CONTRIBUTOR_DESC;
        }

        print <<<EOT
            <form method="post" action="{$form_action}">
                <fieldset>
                    <legend>New Account</legend>

                    <table class="form">
                        <tr>
                            <td>description*</td>

                            <td>

EOT;

                        contributor_print_desc_list($contributor_desc, false);

                        print <<<EOT
                            </td>
                        </tr>

                        <tr>
                            <td>email*</td>
                            <td><input id="contributor_email_uid" type="text" size="15" name="contributor_email" value="$contributor_email" class="always-enabled"/></td>
                        </tr>

                        <tr>
                            <td>password*</td>
                            <td><input id="contributor_password_uid" type="password" size="15" name="contributor_password1" class="always-enabled"/></td>
                        </tr>

                        <tr>
                                <td>retype password*</td>

                                <td>
                                    <input type="password" size="15" name="contributor_password2" class="always-enabled" />
                                </td>
                            </tr>

                        <tr>
                            <td>name*</td>
                            <td><input title="First and last name required" id="contributor_name_uid" type="text" size="15" name="contributor_name"  value="$contributor_name" class="always-enabled"/></td>
                        </tr>

                        <tr>
                            <td>organization*</td>
                            <td><input id="contributor_organization_uid" type="text" size="15" name="contributor_organization"  value="$contributor_organization" class="always-enabled"/></td>
                        </tr>

                        <tr>
                            <td colspan="2">
                                <input type="hidden" name="create_new_account" value="1" class="always-enabled" />
                                <input type="hidden" name="referrer" value="{$referrer}" class="always-enabled" />
                                <input type="submit" name="submit" value="New Account" class="always-enabled auto-width"/>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </form>

EOT;
    }

    /**
     * Print a small admin panel that has options to control a contribution
     *
     * @param $contribution_id int contribution number to operate on 
     * @param $prefix string relative directory pointing to the web root
     */
    function print_contribution_admin_control_panel($contribution_id, $prefix) {
        // Determine if this is displayed on a "view" or "edit" page
        $view_panel = strpos($_SERVER["PHP_SELF"], "view-");
        if (is_bool($view_panel) && !$view_panel) {
            $view_panel = true;
        }
        else {
            $view_panel = false;
        }

        // Get contribution info
        $contribution = contribution_get_contribution_by_id($contribution_id);

        // Build a refernce back to this page
        $refer_here = "referrer={$prefix}teacher_ideas/edit-contribution.php?contribution_id={$contribution_id}";

        // Build user options here
        $options = array();

        // Give the switch to view/edit option
        if ($view_panel) {
            $options[] = "<a href=\"{$prefix}teacher_ideas/view-contribution.php?contribution_id={$contribution_id}&amp;{$refer_here}\">View Contribution</a>";
        }
        else {
            $options[] = "<a href=\"{$prefix}teacher_ideas/edit-contribution.php?contribution_id={$contribution_id}&amp;{$refer_here}\">Edit Contribution</a>";
        }

        // Get status and options based on approved status
        $approved = $contribution["contribution_approved"];
        $status_html = "Status: ";
        if ($contribution["contribution_approved"]) {
            $status_html .= "<span class=\"approved\">approved</span>";
            $options[] = "<a href=\"{$prefix}teacher_ideas/unapprove-contribution.php?contribution_id={$contribution_id}&amp;{$refer_here}\">Unapprove</a>";
        }
        else {
            $status_html .= "<span class=\"unapproved\">unapproved</span>";
            $options[] .= "<a href=\"{$prefix}teacher_ideas/approve-contribution.php?contribution_id={$contribution_id}&amp;{$refer_here}\">Approve</a>";
        }

        // Option to delete the entry
        $options[] .= "<a href=\"{$prefix}teacher_ideas/delete-contribution.php?contribution_id={$contribution_id}&amp;referrer={$prefix}teacher_ideas/manage-contributions.php\" onclick=\"return confirm('Are you sure you want to delete this contribution?');\">Delete</a>";

        // "Render" the options
        $options_html = "<li>".join("</li><li>", $options)."</li>";

        // Print the panel
        print <<<EOT
        <div id="contribution_control_panel">
            <fieldset>
                <legend>Admin Quick Control Panel</legend>
                <div id="contribution_status">
                    <p>
                    {$status_html}
                    </p>
                </div>
                <div id="contribution_options">
                    <p>
                        <strong>Options:</strong>
                        </p>
                        <ul>
                            {$options_html}
                        </ul>
                </div>
                    <div class="clear"></div>
            </fieldset>
        </div>

EOT;
    }

    /**
     * Looks through all the $REQUST key => value pairs and does one of 3 things:
     * 1. if the key is a multiselect control, make an association with the proper table (I assume this means levels etc)
     * 2. if the key is a "deletable_item", then add it to the files to keep list which will be returned (naming convention is confusing)
     * 3. if the key is a simulation id of form sim_id_* associate the contribution with that simulation
     * 
     * @param int $contribution_id id of the contribution
     * @return string array list of files to keep- TODO: not sure what this means
     */
    function contribution_establish_multiselect_associations_from_script_params($contribution_id) {
        $files_to_keep = array();

        // Now have to process multiselect controls:
        foreach($_REQUEST as $key => $value) {
            $matches = array();

            if (is_multiple_selection_control("$key")) {
                contribution_create_multiselect_association($contribution_id, $key, $value);
            }
            else if (is_deletable_item_control("$key")) {
                // We have to keep a file; extract the ID from the name:
                $contribution_file_id = get_deletable_item_control_id("$key");

                $files_to_keep[] = "$contribution_file_id";
            }
            else if (preg_match('/sim_id_([0-9]+)/i', "$key", $matches) == 1) {
                $sim_id = $matches[1];

                contribution_associate_contribution_with_simulation($contribution_id, $sim_id);
            }
        }

        return $files_to_keep;
    }

    function contribution_print_full_edit_form($contribution_id, $script, $referrer, $button_name = 'Update', $page = null) {
        $contributor_authenticated = $page->authenticate_user_is_authorized();

        $contributor = $page->authenticate_get_user();

        $contribution = contribution_get_contribution_by_id($contribution_id);

        // Check to see if this contribution is temporary from the submit page
        // TODO: change this to a real db query (it already exists!)
        $query = "SELECT sessionid FROM temporary_partial_contribution_track WHERE contribution_id=$contribution_id";
        $result = db_exec_query($query);
        $fill_in_data = false;
        if (mysql_num_rows($result) == 1) {
            $row = mysql_fetch_array($result);
            $sess_id = $row[0];
            if ($sess_id == session_id()) {
                $fill_in_data = true;
            }
        }
        else if ($contribution_id == -1) {
            // TODO: refactor so this makes more sense.  Essentially, this variable tells this
            // form if it should automatically fill in the contribution author, org, etc
            $fill_in_data = true;
        }

        if (!$contribution) {
            // Allow 'editing' of non-existent contributions:
            $contribution = db_get_blank_row('contribution');
        }

        $contribution = format_for_html($contribution);
        // Removing unsafe function 'get_code_to_create_variables_from_array',
        // just doing the equivalent by hand
        //eval(get_code_to_create_variables_from_array($contribution));
        $contribution_id = $contribution["contribution_id"];
        $contribution_title = $contribution["contribution_title"];
        $contribution_authors = $contribution["contribution_authors"];
        $contribution_keywords = $contribution["contribution_keywords"];
        $contribution_approved = $contribution["contribution_approved"];
        $contribution_desc = $contribution["contribution_desc"];
        $contribution_duration = $contribution["contribution_duration"];
        $contribution_answers_included = $contribution["contribution_answers_included"];
        $contribution_contact_email = $contribution["contribution_contact_email"];
        $contribution_authors_organization = $contribution["contribution_authors_organization"];
        $contribution_date_created = $contribution["contribution_date_created"];
        $contribution_date_updated = $contribution["contribution_date_updated"];
        $contribution_nomination_count = $contribution["contribution_nomination_count"];
        $contribution_flagged_count = $contribution["contribution_flagged_count"];
        $contribution_standards_compliance = $contribution["contribution_standards_compliance"];
        $contribution_from_phet = $contribution["contribution_from_phet"];
        $contribution_is_gold_star = $contribution["contribution_is_gold_star"];
        $contributor_id = $contribution["contributor_id"];

        /*
         * No don't do that, that doesn't make sense.
         * */
        //if ($contributor_id <= 0 && isset($contributor['contributor_id'])) {
        if ($fill_in_data &&
            ($contributor_id <= 0 && isset($contributor['contributor_id']))) {
            // The contribution didn't have any owner; assume the owner is the current editor:
            $contributor_id = $contributor['contributor_id'];
        }

        $contributor_is_team_member = false;

        // Set reasonable defaults:
        if ($contributor_authenticated) {
            $contributor_is_team_member = $contributor['contributor_is_team_member'];
            
            if ($fill_in_data) {
                if ($contribution_authors_organization == '') {
                    $contribution_authors_organization = $contributor['contributor_organization'];
                }
                if ($contribution_contact_email == '') {
                    $contribution_contact_email = $contributor['contributor_email'];
                }
                if ($contribution_authors == '') {
                    $contribution_authors = $contributor['contributor_name'];
                }
            }
        }

        if ($contribution_keywords == '' && isset($GLOBALS['sim_id'])) {
            $simulation = sim_get_sim_by_id($GLOBALS['sim_id']);

            $contribution_keywords = format_for_html($simulation['sim_keywords']);
        }

        $all_contribution_types = contribution_get_all_template_type_names();
        $contribution_types     = contribution_get_type_names_for_contribution($contribution_id);

        print <<<EOT
            <noscript>
               <p><strong><em>Please take note: </em></strong></p><p>Your browser does not support scripting, or it is turned off.  You must have scripting enabled to be able to submit information.</p>
            </noscript>
            <form id="contributioneditform" method="post" action="{$script}" enctype="multipart/form-data">
                <fieldset>
                    <legend>Required Information</legend>

                    <p>Required fields are marked with an asterisk (*).</p>

                    <table class="form">

EOT;

            // TODO: this is obselete, test to make sure
        if (!$contributor_authenticated) {
            if (isset($_REQUEST['loginmethod']) && strtolower($_REQUEST['loginmethod']) == 'dynamic') {
                contributor_print_quick_login();
            }
        }

        if (true || $contributor_is_team_member) {
            print <<<EOT
                    <tr>
                        <td>
                            contributor*
                        </td>

                        <td>

EOT;

            // Check for contributions that point to invalid contributors
            $validate_contributor = contributor_get_contributor_by_id($contributor_id);
            $result = contributor_get_contributor_by_id($validate_contributor["contributor_id"]);
            if ((!$result) || ($contributor_id <= 0)) {
                if ($contributor_is_team_member) {
                    $null_name = "No name specified";
                    $contributor_names = array(-1 => $null_name);

                    foreach (contributor_get_all_contributors() as $c) {
                        if (strlen(trim($c['contributor_name'])) > 0) {
                            $contributor_names[$c['contributor_id']] = $c['contributor_name'];
                        }
                    }
/*
                    $current_contributor = contributor_get_contributor_by_id($contributor_id);
                    $current_contributor_name = $current_contributor['contributor_name'];
*/

                    if ($contributor_id <= 0) {
                        print "<p>There is no contributor associated with the contribtuion, please select another:</p>";
                    }
                    else {
                        print "<p>The contributor associated with the contribtuion is invalid, please select another:</p>";
                    }

                    print_single_selection(
                        "new_contributor_id",
                        $contributor_names,
                        $null_name
                    );
                }
                else {
                    print "<p><input type=\"hidden\" name=\"contributor_id\" value=\"{$contributor_id}\" /><strong><em>No contributor has been specified</em></strong></p>\n";
                }
            }
            else {
                $current_contributor = contributor_get_contributor_by_id($contributor_id);
                $current_contributor_name = $current_contributor['contributor_name'];
                print "<p><input type=\"hidden\" name=\"contributor_id\" value=\"{$contributor_id}\" /><strong>$current_contributor_name</strong></p>\n";
            }

            print <<<EOT
                        </td>
                    </tr>

EOT;
        }

        print <<<EOT

                    <tr>
                        <td>
                            author(s)*
                        </td>

                        <td>
                            <input type="text" name="contribution_authors" value="$contribution_authors" size="30" />
                            <span class="example"><br/> e.g. John Smith, Mary Jane</span>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            1st author's organization*
                        </td>

                        <td>
                            <input type="text" name="contribution_authors_organization" value="$contribution_authors_organization" size="30"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            contact email*
                        </td>

                        <td>
                            <input type="text" name="contribution_contact_email" value="$contribution_contact_email" size="30" />
                        </td>
                    </tr>

                    <tr>
                        <td>
                            title*
                        </td>

                        <td>
                            <input type="text" name="contribution_title" value="$contribution_title" size="30"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            keywords*
                        </td>

                        <td>
                            <input type="text" name="contribution_keywords" value="$contribution_keywords" size="30" />
                            <span class="example"><br/>keywords should be separated by commas</span>
                        </td>
                    </tr>

EOT;

        print <<<EOT

                    <tr>
                        <td>simulation(s)*</td>

                        <td>

EOT;

                            print_multiple_selection(
                                'Simulation',
                                sim_get_all_sim_names(),
                                contribution_get_associated_simulation_listing_names($contribution_id),
                                true,
                                true,
                                "ms",
                                $page
                            );

        print <<<EOT
                        </td>
                    </tr>

                    <tr>
                        <td>type(s)*</td>

                        <td>

EOT;
                            print_multiple_selection(
                                'Type',
                                $all_contribution_types,
                                $contribution_types,
                                true,
                                true,
                                "ms",
                                $page
                            );

        print <<<EOT
                        </td>
                    </tr>

                    <tr>
                        <td>grade level(s)*</td>

                        <td>

EOT;

                            print_multiple_selection(
                                'Level',
                                contribution_get_all_template_level_names(),
                                contribution_get_level_names_for_contribution($contribution_id),
                                true,
                                true,
                                "ms",
                                $page
                            );

        print <<<EOT
                        </td>
                    </tr>

                    <tr>
                        <td>existing file(s)</td>

                        <td>

EOT;

                            print_deletable_list(
                                'File',
                                contribution_get_contribution_file_names($contribution_id),
                                $page
                            );

        $file_max_size = ini_get("upload_max_filesize");
        $post_max_size = ini_get("post_max_size");

        print <<<EOT
                        </td>
                    </tr>

                    <tr>
                        <td>new file(s)</td>

                        <td>
                            <input type="file" name="contribution_file_url" class="multi" />
                         </td>
                    </tr>

                    <tr>
                        <td>
                        </td>
                        <td>
                            Note: Maximum file size is <strong>{$file_max_size}</strong>, maximum upload of <strong>{$post_max_size}</strong> at a time.
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2">
                            <noscript><p style="text-align: right;">JavaScript is OFF, you cannot submit data</p></noscript>
                            <input name="submit" class="button" type="submit" value="$button_name" />
                        </td>
                    </tr>
                </table>

                </fieldset>

                <fieldset>
                    <legend>Optional Information</legend>

                    <table class="form">
                    <tr>
                        <td>
                            description
                        </td>

                        <td>
                            <textarea name="contribution_desc" rows="5" cols="40">$contribution_desc</textarea>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            subject areas
                        </td>

                        <td>

EOT;

                            print_multiple_selection(
                                'Subject',
                                contribution_get_all_template_subject_names(),
                                contribution_get_subject_names_for_contribution($contribution_id),
                                false,
                                true,
                                "ms",
                                $page
                            );

        print <<<EOT
                        </td>
                    </tr>

                    <tr>
                        <td>
                            duration
                        </td>

                        <td>

EOT;

                            print_single_selection(
                                "contribution_duration",
                                array(
                                    "0"     => "NA",
                                    "30"    => "30 minutes",
                                    "60"    => "60 minutes",
                                    "90"    => "90 minutes",
                                    "120"   => "120 minutes"
                                ),
                                $contribution_duration
                            );

        print <<<EOT
                        </td>
                    </tr>

                    <tr>
                        <td>
                            answers included
                        </td>

                        <td>

EOT;

                            print_checkbox(
                                "contribution_answers_included",
                                "",
                                $contribution_answers_included
                            );

        print <<<EOT
                        </td>
                    </tr>

                    </table>

                    <p>Please describe how the contribution complies with the <a href="http://www.nap.edu/readingroom/books/nses/html/6a.html">K-12 National Science Standards</a>:</p>

EOT;

                       contribution_print_standards_compliance($contribution_standards_compliance);

            if ($contributor_is_team_member) {
                print <<<EOT
                <table class="form">
                    <thead>
                    <tr>
                        <td colspan="2">
                            PhET Team Member Options
                        </td>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>
                            from phet
                        </td>

                        <td>

EOT;

                print_checkbox(
                    "contribution_from_phet",
                    "",
                    $contribution_from_phet
                );

                print <<<EOT
                        </td>
                    </tr>

EOT;

                print <<<EOT
                    <tr>
                        <td>
                            is gold star
                        </td>

                        <td>

EOT;

                print_checkbox(
                    "contribution_is_gold_star",
                    "",
                    $contribution_is_gold_star
                );

                print <<<EOT
                        </td>
                    </tr>
                    </tbody>
                    </table>

EOT;
            }

        print <<<EOT
                    <table class="form">
                        <tr>
                            <td colspan="2">
                                <noscript><p style="text-align: right;">JavaScript is OFF, you cannot submit data</p></noscript>
                                <input name="submit" class="button" type="submit" value="$button_name" />
                                <input type="hidden" name="referrer"        value="$referrer" />
                                <input type="hidden" name="contribution_id" value="$contribution_id" />
                                <input type="hidden" name="action"          value="update" />
                            </td>
                        </tr>
                    </table>
                 </fieldset>
            </form>

EOT;
    }

    function contribution_print_summary($contribution, $contributor_id, $contributor_is_team_member, $referrer = '') {
        $contribution_id       = $contribution['contribution_id'];
        $contribution_title    = format_string_for_html($contribution['contribution_title']);
        $contribution_authors  = format_string_for_html($contribution['contribution_authors']);
        $contribution_approved = format_string_for_html($contribution['contribution_approved']);

        $row_style = '';
        if (!$contribution_approved) {
            // TODO: move this into the main.css
            $row_style = "style=\"background-color: #ffbbbb;\"";
        }

        $path_prefix = SITE_ROOT."teacher_ideas/";

        $query_string = "?contribution_id=$contribution_id&amp;referrer=$referrer";

        $edit    = '';
        $delete  = '';
        $approve = '';

        if ($contributor_id !== null && ($contributor_id == $contribution['contributor_id'] || $contributor_is_team_member)) {
            $edit   .= "<a href=\"${path_prefix}edit-contribution.php$query_string\">edit</a>";
            $delete .= ", <a href=\"${path_prefix}delete-contribution.php$query_string\">delete</a>";

            if ($contributor_is_team_member) {
                if ($contribution_approved) {
                    $approve .= ", <a href=\"${path_prefix}unapprove-contribution.php$query_string\">unapprove</a>";
                }
                else {
                    $approve .= ", <a href=\"${path_prefix}approve-contribution.php$query_string\">approve</a>";
                }
            }
        }

        $contribution_link = "${path_prefix}view-contribution.php$query_string";

        // Quick print something if it blank
        if ($contribution_title == '') {
            $contribution_title = "<em>no title specified</em>";
        }
        if ($contribution_authors == '') {
            $contribution_authors = "<em>no authors specified</em>";
        }
        //print "<li><a href=\"$contribution_link\">$contribution_title</a> - $contribution_authors- ($edit$delete$approve)</li>";
        print <<<EOT
            <tr $row_style>
                <td><a href="$contribution_link">$contribution_title</a></td>
                <td>$contribution_authors</td>
                <td>($edit$delete$approve)</td>
            </tr>

EOT;
    }

    /**
     * Extract the full name and abbreviation from a contribution.
     * ${table_name}_abbrev is assumed to be CSV
     *
     * @param array $contribution array containing ${table_name}_desc|_abbrev
     * @param string $table_name prefix of the key to find
     * @return string '<abbr' HTML markup, with "N/A" & "Not Applicable" if not found
     */
    function contribution_generate_association_abbr($contribution, $table_name) {
        $desc = "Not Applicable";
        $abbr = "N/A";

        if (isset($contribution["${table_name}_desc"])) {
            $desc = $contribution["${table_name}_desc"];
        }
        if (isset($contribution["${table_name}_desc_abbrev"])) {
            $abbr = $contribution["${table_name}_desc_abbrev"];
        }

        // TODO: Is the abbrev really supposed to be CSV?  I think may be residue from an older design.
        $abbr = str_replace(',', '<br/>', $abbr);

        return "<abbr title=\"$desc\">$abbr</abbr>";
    }

    function contribution_generate_association_list($table_name, $associations) {
        $is_first = true;

        $list = '';

        foreach($associations as $association) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $list .= '<br/>';
            }

            $desc = $association["${table_name}_desc"];
            $abbr = $association["${table_name}_desc_abbrev"];

            $list .= "<abbr title=\"$desc\">$abbr</abbr>";
        }

        return $list;
    }

    function contribution_get_simulation_listings_as_list($contribution_id) {
        $names = contribution_get_associated_simulation_listing_names($contribution_id);

        $list = '';

        $is_first = true;

        foreach ($names as $name) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $list .= ', ';
            }

            $list .= $name;
        }

        return $list;
    }

    /**
     * Generate a HTML table row with info about the contribution
     *
     * @param array $contribution Information about a contribution
     * @param boolean $print_sims TRUE means print the "Simulations" column
     * @return string HTML table row
     */
    function contribution_get_contribution_summary_as_html($all_contribution_info, $print_sims = true) {
        global $referrer;

        $contribution = $all_contribution_info["contribution"];

        $html = '';
        //$sim_name = format_string_for_html($contribution["sim_name"]);
        $contribution_authors = format_string_for_html($contribution["contribution_authors"]);
        $contribution_date_updated = format_string_for_html($contribution["contribution_date_updated"]);
        $contribution_title = format_string_for_html($contribution["contribution_title"]);
        $contribution_id = format_string_for_html($contribution["contribution_id"]);
        $contribution_from_phet = format_string_for_html($contribution["contribution_from_phet"]);

        $gold_star_html = contribution_get_gold_star_html_for_contribution($contribution, 10);

        $sim_list = "";

        // Do sims
        $sims = array();
        foreach ($all_contribution_info["simulations"] as $sim) {
            $sims[] = sim_get_link_to_sim_page_by_name(format_string_for_html($sim["sim_name"]));
        }

        if (count($sims) > 0) {
            // Having problems changing the indent on lists, just do line breaks
            // Not as pretty but it works OK
            $sim_list = join("<br />", $sims);
            //$sim_list = "<ul class=\"simlist\"><li>".join("</li><li>", $sims)."</li></ul>";
        }
        else {
            $sim_list = "None";
        }

        $levels = array();
        foreach ($all_contribution_info["levels"] as $level) {
            $levels[] = contribution_generate_association_abbr(
                $level, 'contribution_level'
            );
        }
        if (count($levels)) {
            // Having problems changing the indent on lists, just do line breaks
            // Not as pretty but it works OK
            $level_list = join("<br />", $levels);
            //$level_list = "<ul class=\"levellist\"><li>".join("</li><li>", $levels)."</li></ul>";
        }
        else {
            $level_list = "None";
        }

        $types = array();
        foreach ($all_contribution_info["types"] as $type) {
            $types[] = contribution_generate_association_abbr(
                $type, 'contribution_type'
            );
        }
        if (count($types)) {
            // Having problems changing the indent on lists, just do line breaks
            // Not as pretty but it works OK
            $type_list = join("<br />", $types);
            //$type_list = "<ul class=\"typelist\"><li>".join("</li><li>", $types)."</li></ul>";
        }
        else {
            $type_list = "None";
        }

        $contribution_authors = explode(',', $contribution_authors);

        $contribution_author = $contribution_authors[0];

        $parsed_name = parse_name($contribution_author);

        $contribution_author  = $parsed_name['full_name'];
        $author_first_initial = $parsed_name['first_initial'];
        $author_last_name     = $parsed_name['last_name'];

        $time = strtotime($contribution_date_updated);

        $contribution_date_updated = date('n/y', $time);

        if ($author_first_initial == '') {
            $author_abbr = "$author_last_name";
        }
        else {
            $author_abbr = "$author_first_initial. $author_last_name";
        }

        $author_html = "<abbr title=\"$contribution_author\">$author_abbr</abbr>";

        $prefix = SITE_ROOT;
        $title_html = <<<EOT
                <a href="{$prefix}teacher_ideas/view-contribution.php?contribution_id=$contribution_id&amp;referrer=$referrer">$contribution_title</a>

EOT;

        if ($contribution_from_phet == 1) {
        $title_html = "${title_html} ".FROM_PHET_IMAGE_HTML;
        }

        $title_html .= $gold_star_html;

        $html .= "<tr><td>$title_html</td><td>$author_html</td><td>$level_list</td><td>$type_list</td>";

        if ($print_sims) {
            $html .= "<td>$sim_list</td>";
        }

        $html .= "<td>$contribution_date_updated</td></tr>";

        return $html;
    }

    /**
     * Generate a HTML table row with info about the contribution
     *
     * @param array $contribution Information about a contribution
     * @param boolean $print_sims TRUE means print the "Simulations" column
     * @return string HTML table row
     */
    function orig_contribution_get_contribution_summary_as_html($contribution, $print_sims = true) {
        // TODO: rename this function, since getting everything in HTML at this point is undesirable
        global $referrer;

        $html = '';
        $sim_name = format_string_for_html($contribution["sim_name"]);
        $contribution_authors = format_string_for_html($contribution["contribution_authors"]);
        $contribution_date_updated = format_string_for_html($contribution["contribution_date_updated"]);
        $contribution_title = format_string_for_html($contribution["contribution_title"]);
        $contribution_id = format_string_for_html($contribution["contribution_id"]);
        $contribution_from_phet = format_string_for_html($contribution["contribution_from_phet"]);

        $gold_star_html = contribution_get_gold_star_html_for_contribution($contribution, 10);

        $sim_list = "None";

        // TODO: It appears that this is expecting a CSV style list of simulation names
        // that go with the contribution, but in fact they are on different lines.
        // In fact, some of the sims have commas (,) in their names, so this breaks them
        // up inappropriately:."Circuit Construction Kit, Virtual Lab Version (DC Only)"
        if (isset($sim_name) && trim($sim_name) != '') {
            $sim_list = '';

            $is_first = true;

            foreach(explode(',', $sim_name) as $sim) {
                $sim = trim($sim);

                $cur_sim_link = sim_get_link_to_sim_page_by_name($sim);

                if ($is_first) {
                    $is_first = false;
                }
                else {
                    $sim_list .= '<br/>';
                }

                $sim_list .= $cur_sim_link;
            }
        }

        $level_list = contribution_generate_association_abbr(
            $contribution, 'contribution_level'
        );

        $type_list = contribution_generate_association_abbr(
            $contribution, 'contribution_type'
        );

        $contribution_authors = explode(',', $contribution_authors);

        $contribution_author = $contribution_authors[0];

        $parsed_name = parse_name($contribution_author);

        $contribution_author  = $parsed_name['full_name'];
        $author_first_initial = $parsed_name['first_initial'];
        $author_last_name     = $parsed_name['last_name'];

        $time = strtotime($contribution_date_updated);

        $contribution_date_updated = date('n/y', $time);

        if ($author_first_initial == '') {
            $author_abbr = "$author_last_name";
        }
        else {
            $author_abbr = "$author_first_initial. $author_last_name";
        }

        $author_html = "<abbr title=\"$contribution_author\">$author_abbr</abbr>";

        $prefix = SITE_ROOT;
        $title_html = <<<EOT
                <a href="{$prefix}teacher_ideas/view-contribution.php?contribution_id=$contribution_id&amp;referrer=$referrer">$contribution_title</a>

EOT;

        if ($contribution_from_phet == 1) {
        $title_html = "${title_html} ".FROM_PHET_IMAGE_HTML;
        }

        $title_html .= $gold_star_html;

        $html .= "<tr><td>$title_html</td><td>$author_html</td><td>$level_list</td><td>$type_list</td>";

        if ($print_sims) {
            $html .= "<td>$sim_list</td>";
        }

        $html .= "<td>$contribution_date_updated</td></tr>";

        return $html;
    }

    function contribution_get_contribution_file_names($contribution_id) {
        $contribution_file_names = array();

        $contribution_files = contribution_get_contribution_file_infos($contribution_id);

        foreach($contribution_files as $contribution_file) {
            $name = create_deletable_item_control_name('contribution_file_url', $contribution_file['contribution_file_id']);

            $contribution_file_names[$name] = $contribution_file['contribution_file_name'];
        }

        return $contribution_file_names;
    }

    /**
     * Deletes the specified file from the database 
     *
     * @param int $contribution_file_id id of the contributed file
     * @return true no matter the result of the operation
     */
    function contribution_delete_contribution_file($contribution_file_id) {
        $condition = array( 'contribution_file_id' => $contribution_file_id );

        db_delete_row('contribution_file', $condition);

        // TODO: return something more indicitave of the sucess of the operation (not that anybody is checking)
        return true;
    }

    /**
     * Given the contribution id and an array of file ids, delete all files that are not associated with the contribution
     *
     * @param int $contribution_id id of the contribution with associated files
     * @param int array $files_to_keep array of file ids that should NOT be deleted
     * @return true no matter the result
     */
    function contribution_delete_all_files_not_in_list($contribution_id, $files_to_keep) {
        $all_files = array();

        $contribution_files = contribution_get_contribution_file_infos($contribution_id);

        foreach($contribution_files as $contribution_file) {
            $all_files[] = $contribution_file['contribution_file_id'];
        }

        $files_to_delete = array_diff($all_files, $files_to_keep);

        foreach($files_to_delete as $file_to_delete) {
            contribution_delete_contribution_file($file_to_delete);
        }

        // TODO: should return a result more indictative of the result of the function's success
        return true;
    }

    /**
     * Get the info for all files associated with the contribution_id, no file content contents
     *
     * @param int $contribution_id id of the contribution
     * @return array containing the info for all the files associated with the id (empty if no files)
     */
    function contribution_get_contribution_file_infos($contribution_id) {
        $contribution_files = array();

        // Get all columns except the actual file
        $columns = array('contribution_file_id',
                         'contribution_file_name', 
                         'contribution_file_url', 
                         'contribution_file_size', 
                         'contribution_id');

        $query = "SELECT ".join(',', $columns)." FROM `contribution_file` WHERE `contribution_id`='$contribution_id'";
        $contribution_file_rows = db_exec_query($query);

        while ($contribution = mysql_fetch_assoc($contribution_file_rows)) {
            $contribution_files[] = $contribution;
        }

        return $contribution_files;
    }

    /**
     * Return all files & info associated with a contribution id
     *
     * @param int $contribution_id id of the contribution to get file info
     * @return array all file info (empty if no files associated with id)
     */
    function contribution_get_contribution_files($contribution_id) {
        $contribution_files = array();

        $contribution_file_rows = db_exec_query("SELECT * FROM `contribution_file` WHERE `contribution_id`='$contribution_id' ");

        while ($contribution = mysql_fetch_assoc($contribution_file_rows)) {
            $contribution_files[] = $contribution;
        }

        return $contribution_files;
    }

    function contribution_can_contributor_manage_contribution($contributor_id, $contribution_id) {
        if ($contribution_id == -1) {
            // No one owns this contribution:
            return true;
        }

        $contribution = contribution_get_contribution_by_id($contribution_id);
        $contributor  = contributor_get_contributor_by_id($contributor_id);

        return $contribution['contributor_id'] == $contributor_id ||
               $contributor['contributor_is_team_member'] == '1' ||
               $contribution['contributor_id'] == '-1';
    }

    function contribution_get_contributions_for_contributor_id($contributor_id) {
        return db_get_rows_by_condition('contribution', array('contributor_id' => $contributor_id), false, false, 'ORDER BY `contribution_title` ASC');
    }

    function contribution_get_coauthored_contributions_for_contributor_id($contributor_id) {
        $contributor = contributor_get_contributor_by_id($contributor_id);

        if ($contributor['contributor_is_team_member'] == 0) return array();

        $parsed_name = parse_name($contributor['contributor_name']);

        $last_name = $parsed_name['last_name'];

        $name = parse_name($contributor['contributor_name']);
        $last_name = $name['last_name'];

        // Select all coauthored contributions that are NOT owned by the contributor_id:
        $result = db_exec_query("SELECT * FROM `contribution` WHERE `contribution_authors` LIKE '%$last_name%' AND `contributor_id`<>'$contributor_id' ORDER BY `contribution_title` ASC");

        $contributions = array();

        while ($contribution = mysql_fetch_assoc($result)) {
            $contributions[] = $contribution;
        }

        return $contributions;
    }

    function contribution_get_other_manageable_contributions_for_contributor_id($contributor_id) {
        $contributor = contributor_get_contributor_by_id($contributor_id);

        if ($contributor['contributor_is_team_member'] == 0) return array();

        $parsed_name = parse_name($contributor['contributor_name']);

        $last_name = $parsed_name['last_name'];

        $name = parse_name($contributor['contributor_name']);
        $last_name = $name['last_name'];

        // TODO: roll this into the DB query?
        $temp_contribution_ids = array();
        $temp_contribution_track = db_get_all_rows('temporary_partial_contribution_track');
        foreach ($temp_contribution_track as $temp_contrib) {
            $temp_contribution_ids[] = $temp_contrib["contribution_id"];
        }

        // Select all contributions NOT owned by the contributor and where the contributor is NOT a coauthor:
        $result = db_exec_query("SELECT * FROM `contribution` WHERE `contribution_authors` NOT LIKE '%$last_name%' AND `contributor_id`<>'$contributor_id' ORDER BY `contribution_title` ASC");

        $contributions = array();

        while ($contribution = mysql_fetch_assoc($result)) {
            // Filter out anything in the temporary_partial_contribution_track table
            if (in_array($contribution["contribution_id"], $temp_contribution_ids)) {
                continue;
            }

            $contributions[] = $contribution;
        }

        return $contributions;
    }

    function contribution_delete_contribution($contribution_id) {
        $condition = array( 'contribution_id' => $contribution_id );

        db_delete_row('contribution',            $condition);
        db_delete_row('contribution_file',       $condition);
        db_delete_row('simulation_contribution', $condition);
        db_delete_row('contribution_comment',    $condition);
        db_delete_row('contribution_flagging',   $condition);
        db_delete_row('contribution_nomination', $condition);

        contribution_delete_all_multiselect_associations('contribution_level',   $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_type',    $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_subject', $contribution_id);

        return true;
    }

    function contribution_update_contribution($contribution) {
        if (!db_update_table('contribution', $contribution, 'contribution_id', $contribution['contribution_id'])) {
            return false;
        }

        return true;
    }

    function contribution_delete_all_multiselect_associations($assoc_name, $contribution_id) {
        db_exec_query("DELETE FROM `$assoc_name` WHERE `contribution_id`='$contribution_id' AND `${assoc_name}_is_template`='0' ");

        return true;
    }

    function contribution_get_association_abbreviation_desc($table_name, $text) {
        $result = db_exec_query("SELECT * FROM `$table_name` WHERE `${table_name}_desc`='$text' AND `${table_name}_is_template`='1' ");

        if (!$result) {
            return abbreviate($text);
        }

        $first_row = mysql_fetch_assoc($result);

        if (!$first_row) {
            return abbreviate($text);
        }

        $abbrev = $first_row["${table_name}_desc_abbrev"];

        if ($abbrev == '') {
            $abbrev = abbreviate($text);
        }

        return $abbrev;
    }

    /**
     * Returns the id of the table (embedded in the control_name) with the table_desc matching the given text.  Creates the association if none exists.
     *
     * @param int $contribution_id id of the contribution
     * @param string $multiselect_control_name name of control of form multiselect_([a-zA-Z0-9_]+)_id_([0-9]+)
     * @param string $text string to match in the table's table_desc column
     * @return false if control_name is not of from, else id if the association
     */
    function contribution_create_multiselect_association($contribution_id, $multiselect_control_name, $text) {
        $text = html_entity_decode($text);

        $matches = array();

        if (preg_match('/multiselect_([a-zA-Z0-9_]+)_id_([0-9]+)$/i', $multiselect_control_name, $matches) !== 1) {
            return false;
        }

        $table_name = $matches[1];

        // FIXME: no escaping of $text, or $table_name
        $result = db_exec_query("SELECT * FROM `$table_name` WHERE `${table_name}_desc`='$text' AND `contribution_id`='$contribution_id' ");

        if ($first_row = mysql_fetch_assoc($result)) {
            $id = $first_row["${table_name}_id"];
            return $id;
        }
        else {
            $id = db_insert_row(
                $table_name,
                array(
                    "${table_name}_desc"        => $text,
                    "${table_name}_desc_abbrev" => contribution_get_association_abbreviation_desc($table_name, $text),
                    "${table_name}_is_template" => '0',
                    'contribution_id'           => $contribution_id
                )
            );

            return $id;
        }
    }

    function contribution_add_new_contribution($contribution_title, $contributor_id, $file_tmp_name = null, $file_user_name = null) {
        /*
         * Remove trying to guess what the type of activity is
        if ($file_tmp_name != null) {
            if (preg_match('/.+\\.(doc|txt|rtf|pdf|odt)/i', $file_user_name) == 1) {
                $contribution_type = "Homework";
            }
            else if (preg_match('/.+\\.(ppt|odp)/i', $file_user_name) == 1) {
                $contribution_type = "Lecture";
            }
            else {
                $contribution_type = "Support";
            }
        }
        else {
            $contribution_type = "Lab";
        }
        */

        $contribution_id = db_insert_row(
            'contribution',
            array(
                'contribution_title'        => $contribution_title,
                'contributor_id'            => $contributor_id,
                'contribution_date_created' => date('YmdHis')
            )
        );

        if ($file_tmp_name != null) {
            if (contribution_add_new_file_to_contribution($contribution_id, $file_tmp_name, $file_user_name) == FALSE) {
                return FALSE;
            }
        }

        /*
         * Remove trying to guess what the type of activity is
        contribution_create_multiselect_association($contribution_id,
            'multiselect_contribution_type_id_0', $contribution_type
        );
        */

        return $contribution_id;
    }

    function contribution_get_contribution_file_by_id($contribution_file_id) {
        return db_get_row_by_id('contribution_file', 'contribution_file_id', $contribution_file_id);
    }

    function contribution_get_contribution_file_name($contribution_file_id) {
        $contribution = contribution_get_contribution_file_by_id($contribution_file_id);
        
        return $contribution['contribution_file_name'];
    }

    function contribution_get_contribution_file_contents($contribution_file_id) {
        $contribution = contribution_get_contribution_file_by_id($contribution_file_id);

        if ($contribution['contribution_file_contents'] != '') {
            return base64_decode($contribution['contribution_file_contents']);
        }

        return file_get_contents(SITE_ROOT.$contribution_file_url);
    }

    function contribution_get_contribution_file_link($contribution_file_id) {

    }

    function contribution_add_new_file_to_contribution($contribution_id, $file_tmp_name, $file_user_name) {
        $file_size = filesize($file_tmp_name);

        $contribution_file_id = db_insert_row(
                'contribution_file',
                array(
                    'contribution_id'            => $contribution_id,
                    'contribution_file_name'     => $file_user_name,
                    'contribution_file_contents' => base64_encode(file_get_contents($file_tmp_name)),
                    'contribution_file_size'     => $file_size
                )
            );

        return $contribution_file_id;
    }

    function contribution_unassociate_contribution_with_all_simulations($contribution_id) {
        db_exec_query("DELETE FROM `simulation_contribution` WHERE `contribution_id`='$contribution_id' ");

        return true;
    }

    /**
     * Automatically inserts a row to associate the sim_id with the contribution.  FIXME: if this creates duplicates
     *
     * @param int $contribution_id id of the contribution
     * @param int $sim_id id of the simulation
     * @return int id of the association in the simulation_contribution table
     */
    function contribution_associate_contribution_with_simulation($contribution_id, $sim_id) {
        $simulation_contribution_id = db_insert_row(
            'simulation_contribution',
            array(
                'sim_id'          => $sim_id,
                'contribution_id' => $contribution_id
            )
        );

        return $simulation_contribution_id;
    }

    function contribution_get_all_level_names() {
        $levels = array();

        $contribution_level_rows = db_exec_query("SELECT * FROM `contribution_level` ORDER BY `contribution_level_desc` ASC ");

        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];

            $name = create_multiselect_control_name('contribution_level', $id);

            $levels[$name] = $contribution_level['contribution_level_desc'];
        }

        return array_unique($levels);
    }

    function contribution_get_all_template_levels() {
        $levels = array();

        $contribution_level_rows = db_exec_query("SELECT * FROM `contribution_level` WHERE `contribution_level_is_template`='1' ORDER BY `contribution_level_order` ASC ");

        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];

            $name = create_multiselect_control_name('contribution_level', $id);

            $levels[$name] = $contribution_level;
        }

        return $levels;
    }

    function contribution_get_all_template_level_names() {
        $levels = contribution_get_all_template_levels();

        $level_names = array();

        foreach($levels as $key => $level) {
            $level_names[$key] = $level['contribution_level_desc'];
        }

        return array_unique($level_names);
    }

    function contribution_get_levels_for_contribution($contribution_id) {
        $levels = array();

        $contribution_level_rows = db_exec_query("SELECT * FROM `contribution_level` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_level_desc` ASC ");

        while ($contribution_level = mysql_fetch_assoc($contribution_level_rows)) {
            $id = $contribution_level['contribution_level_id'];

            $name = create_multiselect_control_name('contribution_level', $id);

            $levels[$name] = $contribution_level;
        }

        return $levels;
    }

    function contribution_get_level_names_for_contribution($contribution_id) {
        $levels = contribution_get_levels_for_contribution($contribution_id);

        $level_names = array();

        foreach($levels as $key => $level) {
            $level_names[$key] = $level['contribution_level_desc'];
        }

        return $level_names;
    }

    function contribution_get_all_subject_names() {
        $subjects = array();

        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` ORDER BY `contribution_subject_desc` ASC ");

        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];

            $name = create_multiselect_control_name('contribution_subject', $id);

            $subjects[$name] = $contribution_subject['contribution_subject_desc'];
        }

        return array_unique($subjects);
    }

    function contribution_get_all_template_subjects() {
        $subjects = array();

        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_order` ASC ");

        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];

            $name = create_multiselect_control_name('contribution_subject', $id);

            $subjects[$name] = $contribution_subject;
        }

        return $subjects;
    }

    function contribution_get_all_template_subject_names() {
        $subjects = array();

        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_subject_is_template`='1' ORDER BY `contribution_subject_order` ASC ");

        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];

            $name = create_multiselect_control_name('contribution_subject', $id);

            $subjects[$name] = $contribution_subject['contribution_subject_desc'];
        }

        return array_unique($subjects);
    }

    function contribution_get_subject_names_for_contribution($contribution_id) {
        $subjects = array();

        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_subject_desc` ASC ");

        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];

            $name = create_multiselect_control_name('contribution_subject', $id);

            $subjects[$name] = $contribution_subject['contribution_subject_desc'];
        }

        return $subjects;
    }

    function contribution_get_subjects_for_contribution($contribution_id) {
        $subjects = array();

        $contribution_subject_rows = db_exec_query("SELECT * FROM `contribution_subject` WHERE `contribution_id`='$contribution_id' ORDER BY `contribution_subject_desc` ASC ");

        while ($contribution_subject = mysql_fetch_assoc($contribution_subject_rows)) {
            $id = $contribution_subject['contribution_subject_id'];

            $name = create_multiselect_control_name('contribution_subject', $id);

            $subjects[$name] = $contribution_subject;
        }

        return $subjects;
    }

    function contribution_get_all_type_names() {
        $types = array();

        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` ORDER BY `contribution_type_desc` ASC ");

        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];

            $name = create_multiselect_control_name('contribution_type', $id);

            $types[$name] = "$type";
        }

        return array_unique($types);
    }

    function contribution_get_all_template_type_names() {
        $types = array();

        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_type_is_template` = '1' ORDER BY `contribution_type_order` ASC ");

        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];

            $name = create_multiselect_control_name('contribution_type', $id);

            $types[$name] = "$type";
        }

        return array_unique($types);
    }

    function contribution_get_types_for_contribution($contribution_id) {
        $types = array();

        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_id`='$contribution_id' ");

        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];

            $name = create_multiselect_control_name('contribution_type', $id);

            $types[$name] = $contribution_type;
        }

        return $types;
    }

    function contribution_get_type_names_for_contribution($contribution_id) {
        $types = array();

        $contribution_type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_id`='$contribution_id' ");

        while ($contribution_type = mysql_fetch_assoc($contribution_type_rows)) {
            $id   = $contribution_type['contribution_type_id'];
            $type = $contribution_type['contribution_type_desc'];

            $name = create_multiselect_control_name('contribution_type', $id);

            $types[$name] = "$type";
        }

        return $types;
    }

    function contribution_get_associated_simulation_listing_names($contribution_id) {
        $simulation_rows = db_exec_query("SELECT * FROM `simulation`,`simulation_contribution` WHERE `simulation`.`sim_id`=`simulation_contribution`.`sim_id` AND `simulation_contribution`.`contribution_id`='$contribution_id' ");

        $simulations = array();

        while ($simulation = mysql_fetch_assoc($simulation_rows)) {
            $id         = $simulation['sim_id'];
            $sim_name   = $simulation['sim_name'];

            $simulations["sim_id_$id"] = "$sim_name";
        }

        return $simulations;
    }

    function contribution_get_associated_simulation_listings($contribution_id) {
        $simulation_contribution_rows =
            db_exec_query("SELECT * FROM `simulation_contribution` WHERE `contribution_id`='$contribution_id' ");

        $simulation_contributions = array();

        while ($simulation_contribution = mysql_fetch_assoc($simulation_contribution_rows)) {
            $id = $simulation_contribution['sim_id'];

            $simulation_contributions["sim_id_$id"] = $simulation_contribution;
        }

        return $simulation_contributions;
    }

    function contribution_set_approved($contribution_id, $status) {
        if ($status) {
            $status = '1';
        }
        else {
            $status = '0';
        }

        return db_update_table('contribution', array( 'contribution_approved' => $status ), 'contribution_id', $contribution_id);
    }

    function contribution_explode_contribution_by_array($contribution, $array) {
        $new_contribs = array();

        if (count($array) == 0) {
            return array( $contribution );
        }

        // Execute join:
        foreach($array as $element) {
            $new_contrib = $contribution;

            foreach($element as $key => $value) {
                $new_contrib["$key"] = "$value";
            }

            $new_contribs[] = $new_contrib;
        }

        return $new_contribs;
    }


    function contribution_explode_contributions($contributions) {
        // Index by number:
        $contributions = array_values($contributions);

        $exploded = array();

        $explosion_functions = array(
            'contribution_get_levels_for_contribution',
            'contribution_get_types_for_contribution',
            'contribution_get_associated_simulation_listings',
        );

        foreach($explosion_functions as $explosion_function) {
            $exploded = array();

            foreach($contributions as $contribution) {
                $contribution_id = $contribution['contribution_id'];

                $array = call_user_func($explosion_function, $contribution_id);

                $new_contribs = contribution_explode_contribution_by_array($contribution, $array);

                $exploded = array_merge($exploded, $new_contribs);
            }

            $contributions = $exploded;
        }

        // Join simulation data:
        $final = array();

        foreach($exploded as $contribution) {
            if (isset($contribution['sim_id'])) {
                $simulation = sim_get_sim_by_id($contribution['sim_id']);

                if (is_array($simulation)) {
                    foreach($simulation as $key => $value) {
                        $contribution["$key"] = "$value";
                    }
                }
            }

            $final[] = $contribution;
        }

        return $final;
    }


    function newer_contribution_get_specific_contributions($sim_names, $type_descs, $level_descs) {
        $contributions = array();

        $sim_names   = array_remove($sim_names,   'all');
        $type_descs  = array_remove($type_descs,  'all');
        $level_descs = array_remove($level_descs, 'all');

        $query = "CREATE TEMPORARY TABLE t1 SELECT DISTINCT `contribution`.* FROM `contribution`";

        $where = '';

        $query .= " LEFT JOIN `simulation_contribution` ON `contribution`.`contribution_id`=`simulation_contribution`.`contribution_id`";
        $query .= " LEFT JOIN `simulation` ON `simulation_contribution`.`sim_id`=`simulation`.`sim_id`";

        if (count($sim_names) > 0) {
            $where .= ' WHERE';

            $where .= db_form_alternation_where_clause('simulation', 'sim_name', $sim_names);
        }

        $query .= " LEFT JOIN `contribution_type` ON `contribution`.`contribution_id`=`contribution_type`.`contribution_id`";

        if (count($type_descs) > 0) {
            if (strlen($where) > 0) $where .= " AND";
            else $where .= ' WHERE';

            $where .= db_form_alternation_where_clause('contribution_type', 'contribution_type_desc', $type_descs);
        }

        $query .= " LEFT JOIN `contribution_level` ON `contribution`.`contribution_id`=`contribution_level`.`contribution_id`";

        if (count($level_descs) > 0) {
            if (strlen($where) > 0) $where .= " AND";
            else $where .= ' WHERE';

            $where .= db_form_alternation_where_clause('contribution_level', 'contribution_level_desc', $level_descs);
        }

        // Remove the unapproved ones
        if (strlen($where) > 0) $where .= " AND";
        else $where .= ' WHERE';
        $where .= db_form_alternation_where_clause('contribution', 'contribution_approved', array("1"));

        $query .= "$where ORDER BY `contribution`.`contribution_title` ASC";

        $contribution_rows = db_exec_query($query);

        // Now get the contributions
        $con_rows = db_exec_query("SELECT * FROM t1");
        $master = array();
        while ($con_row = mysql_fetch_assoc($con_rows)) {
            $contribution_id = $con_row["contribution_id"];
            $master[$contribution_id]["contribution"] = $con_row;
            $master[$contribution_id]["types"] = array();
            $master[$contribution_id]["levels"] = array();
            $master[$contribution_id]["simulations"] = array();
        }

        // Now the types
        $type_rows = db_exec_query("SELECT * FROM `contribution_type` WHERE `contribution_id` IN (SELECT `contribution_id` FROM t1)");
        while ($type_row = mysql_fetch_assoc($type_rows)) {
            $contribution_id = $type_row["contribution_id"];
            $master[$contribution_id]["types"][] = $type_row;
        }

        // Now the levels
        $level_rows = db_exec_query("SELECT * FROM `contribution_level` WHERE `contribution_id` IN (SELECT `contribution_id` FROM t1)");
        while ($level_row = mysql_fetch_assoc($level_rows)) {
            $contribution_id = $level_row["contribution_id"];
            $master[$contribution_id]["levels"][] = $level_row;
        }

        // Now the sims
        $sql = <<<EOT
            SELECT sc.contribution_id, s.sim_name
              FROM simulation AS s, simulation_contribution AS sc
              WHERE sc.sim_id=s.sim_id
                AND sc.contribution_id 
                  IN (SELECT contribution_id FROM t1) ORDER BY sc.contribution_id;

EOT;

        $sim_rows = db_exec_query($sql);

        while ($sim_row = mysql_fetch_assoc($sim_rows)) {
            $contribution_id = $sim_row["contribution_id"];
            $master[$contribution_id]["simulations"][] = $sim_row;
        }
        return $master;
    }

    function orig_contribution_get_specific_contributions($sim_names, $type_descs, $level_descs) {
        $contributions = array();

        $sim_names   = array_remove($sim_names,   'all');
        $type_descs  = array_remove($type_descs,  'all');
        $level_descs = array_remove($level_descs, 'all');

        $query = "SELECT * FROM `contribution`";

        $where = '';

        $query .= " LEFT JOIN `simulation_contribution` ON `contribution`.`contribution_id`=`simulation_contribution`.`contribution_id`";
        $query .= " LEFT JOIN `simulation` ON `simulation_contribution`.`sim_id`=`simulation`.`sim_id`";

        if (count($sim_names) > 0) {
            $where .= ' WHERE';

            $where .= db_form_alternation_where_clause('simulation', 'sim_name', $sim_names);
        }

        $query .= " LEFT JOIN `contribution_type` ON `contribution`.`contribution_id`=`contribution_type`.`contribution_id`";

        if (count($type_descs) > 0) {
            if (strlen($where) > 0) $where .= " AND";
            else $where .= ' WHERE';

            $where .= db_form_alternation_where_clause('contribution_type', 'contribution_type_desc', $type_descs);
        }

        $query .= " LEFT JOIN `contribution_level` ON `contribution`.`contribution_id`=`contribution_level`.`contribution_id`";

        if (count($level_descs) > 0) {
            if (strlen($where) > 0) $where .= " AND";
            else $where .= ' WHERE';

            $where .= db_form_alternation_where_clause('contribution_level', 'contribution_level_desc', $level_descs);
        }

        // Remove the unapproved ones
        if (strlen($where) > 0) $where .= " AND";
        else $where .= ' WHERE';
        $where .= db_form_alternation_where_clause('contribution', 'contribution_approved', array("1"));

        $query .= "$where ORDER BY `contribution`.`contribution_title` ASC";

        $contribution_rows = db_exec_query($query);

        print "mysql_num_rows:".mysql_num_rows($contribution_rows);
        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contribution_id = $contribution['contribution_id'];

            $contributions["$contribution_id"] = $contribution;
        }

        print "count contributions:".count($contributions);

        return $contributions;
    }

    function contribution_get_all_contributions() {
        $contributions = array();

        $contribution_rows = db_exec_query("SELECT * FROM `contribution` ORDER BY `contribution_title` ASC");

        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contribution_id = $contribution['contribution_id'];

            $contributions["$contribution_id"] = $contribution;
        }

        return $contributions;
    }

    function contribution_get_all_approved_contributions() {
        $contributions = array();

        $contribution_rows = db_exec_query("SELECT * FROM `contribution` WHERE `contribution_approved`=1 ORDER BY `contribution_title` ASC");

        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contribution_id = $contribution['contribution_id'];

            $contributions["$contribution_id"] = $contribution;
        }

        return $contributions;
    }

    function contribution_get_all_contributions_for_sim($sim_id) {
        $contributions = array();

        $contribution_rows = db_exec_query("SELECT * FROM `contribution` , `simulation_contribution` WHERE `contribution` . `contribution_id` = `simulation_contribution` . `contribution_id` AND `simulation_contribution` . `sim_id` = '$sim_id' ORDER BY `contribution_title` ASC");

        while ($contribution = mysql_fetch_assoc($contribution_rows)) {
            $contributions[] = $contribution;
        }

        return $contributions;
    }

    function contribution_get_approved_contributions_for_sim($sim_id) {
        $contributions = contribution_get_all_contributions_for_sim($sim_id);

        foreach($contributions as $index => $contribution) {
            if ($contribution['contribution_approved'] == '0') {
                unset($contributions[$index]);
            }
        }

        return $contributions;
    }

    function contribution_get_contribution_by_id($contribution_id) {
        $contribution_rows = db_exec_query("SELECT * FROM `contribution` WHERE `contribution_id`='$contribution_id' ");

        return mysql_fetch_assoc($contribution_rows);
    }

    /**
     * Get all contributor data from the database
     *
     * @param string $email_keys (optional) if specified, return an array with usernames as keys, else just a normal array
     * @return array all contributor information, orderd by name
     */
    function contributor_get_all_contributors($email_keys = false) {
        $contributors = array();

        $contributor_rows =
            db_exec_query("SELECT * from `contributor` ORDER BY `contributor_name` ASC ");

        if ($email_keys === false) {
            while ($contributor = mysql_fetch_assoc($contributor_rows)) {
                $contributors[] = $contributor;
            }
        }
        else {
            while ($contributor = mysql_fetch_assoc($contributor_rows)) {
                $contributors[$contributor['contributor_email']] = $contributor;
            }
        }

        return $contributors;
    }

    /**
     * Get the contributor information with the given username
     *
     * @param string $username email address to find
     * @return mixed an arry of contributor info, else false
     */
    function contributor_get_from_contributor_email($username) {
        if (strlen(trim($username)) == 0) return false;

        $condition = array('contributor_email' => $username);
        $contributors = db_get_rows_by_condition('contributor', $condition, false, false);
        assert((count($contributors) == 0) || (count($contributors) == 1));

        if (count($contributors) > 0) {
            return $contributors[0];
        }

        return false;
    }

    /**
     * Determines if the given email address is a contributor in the database
     *
     * @param string $username FIXME: this is an email address of the contributor to find
     * @return bool true if found, false otherwise
     */
    function contributor_is_contributor($username) {
        if (strlen(trim($username)) == 0) return false;

        // Refactor: The database is case insensitive (as specified on creation), so can do this much faster
        $contributor = contributor_get_from_contributor_email($username);
        if ($contributor === false) {
            return false;
        }

        return true;
    }

    function contributor_send_password_reminder($username) {
        return;
        $contributor = contributor_get_contributor_by_username($username);

        if ($contributor) {
            $contributor_name     = $contributor['contributor_name'];
            $contributor_password = $contributor['contributor_password'];

            if (strlen($contributor_password) == 0) {
                $pass = "You haven't chosen a password (don't enter anything in the login dialog).\n";
            }
            else {
                $pass = "Your password is \"$contributor_password\" (without the quotation marks)\n";
            }

            mail($username,
                 "PhET Password Reminder",
                 "\n".
                 "Dear $contributor_name, \n".
                 "\n".
                 "Your login e-mail is \"$username\".\n".
                 $pass.
                 "\n".
                 "Regards,\n".
                 "\n".
                 "The PhET Team \n",

                 "From: The PhET Team <".PHET_HELP_EMAL.">");
        }
    }

    /**
     * Return all team members in the database
     *
     * @return an array with all team members
     */
    function contributor_get_team_members() {
        $admins = array();

        $contributor_rows = db_exec_query("SELECT * from `contributor` WHERE `contributor_is_team_member`='1' ORDER BY `contributor_name` ASC ");

        while ($contributor = mysql_fetch_assoc($contributor_rows)) {
            $admins[] = $contributor;
        }

        return $admins;
    }

    /**
     * Returns true if the email address is a team member
     *
     * @param string $username FIXME: this is an email address of the contributor to find
     * @return bool true if email is a team member, false otherwise
     */
    function contributor_is_admin_username($username) {
        $admins = contributor_get_team_members();

        foreach($admins as $contributor) {
            if (strtolower($contributor['contributor_email']) == strtolower($username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * For the given email address, return the id of the contributor
     *
     * @param string $username FIXME: this is an email address of the contributor to find
     * @return id if email found in database, else false
     */
    function contributor_get_id_from_contributor_username($username) {
        $contributor = contributor_get_from_contributor_email($username);
        if ($contributor === false) {
            return false;
        }

        return $contributor['contributor_id'];
    }

    /**
     * Confirm that the username and the password are in the database and associated with each other
     *
     * @param unknown_type $username
     * @param unknown_type $password
     * @return unknown
     */
    function contributor_valid_email_and_password($username, $password) {
        $contributor = contributor_get_from_contributor_email($username);
        if ($contributor === false) {
            return false;
        }

        if ($contributor['contributor_password'] == $password) {
            return true;
        }

        return false;
    }

    /**
     * Return the id of the contributor who matches the given email and password
     *
     * @param string $username FIXME: this is an email address of the contributor to find
     * @param string $password password associated with the email (will convert to lower case)
     * @return id if match, false otherwise
     */
    function contributor_get_id_from_contributor_username_and_password($username, $password) {
        $contributor = contributor_get_from_contributor_email($username);
        if ($contributor === false) {
            return false;
        }

        if ($contributor['contributor_password'] == $password) {
            return $contributor['contributor_id'];
        }

        return false;
    }

    /**
     * Return the id of the contributor who matches the given email and password. 
     *
     * @param string $username FIXME: this is an email address of the contributor to find
     * @param string $password password associated with the email (will convert to lower case)
     * @return id if match, false otherwise
     */
    function contributor_get_id_from_contributor_username_and_encrypted_password($username, $encrypted_password) {
        $contributor = contributor_get_from_contributor_email($username);
        if ($contributor === false) {
            return false;
        }

        if ($contributor['contributor_password'] == $encrypted_password) {
            return $contributor['contributor_id'];
        }

        return false;
    }

    /**
     * Determines if the name and password are associated in the database
     *
     * @param string $username FIXME: this is an email address of the contributor to find
     * @param string $password_hash password to match the email address
     * @return bool true if a match is found, false otherwise
     */
    function contributor_is_valid_login($username, $password_hash) {
        return (contributor_get_id_from_contributor_username_and_encrypted_password($username, $password_hash) !== false);
    }

    /**
     * Determine if the email address and password are for an administrator.
     *
     * @param string $username FIXME: this is an email address of the contributor to find
     * @param string $password_hash password to match the email address
     * @return bool true if they match and are an administrator, false otherwise
     */
    function contributor_is_valid_admin_login($username, $password_hash) {
        if (!contributor_is_admin_username($username)) return false;

        return contributor_is_valid_login($username, $password_hash);
    }

    function contributor_print_quick_login() {
        print <<<EOT
            <div id="quick_login_uid">
                <div class="field">
                    <span class="label_content">
                        <input type="text" size="20" name="contributor_name" id="contributor_name_uid" onchange="javascript:on_name_change();"/>
                    </span>

                    <span class="label">your name</span>
                </div>

                <div id="required_login_info_uid">
                    <div class="field">
                        <span class="label_content">
                            <input type="button" name="enter" value="Enter" />
                        </span>
                    </div>
                </div>

                <br/>

            </div>

EOT;
    }

    function contributor_add_new_blank_contributor($is_team_member = false) {
        $contributor_is_team_member = $is_team_member ? '1' : '0';

        return db_insert_row(
            'contributor',
            array(
                'contributor_is_team_member' => $contributor_is_team_member,
                'contributor_receive_email'  => '1'
            )
        );
    }

    function contributor_add_new_contributor($username, $password, $is_team_member = false) {
        $team_member_field = $is_team_member ? '1' : '0';

        return db_insert_row('contributor',
            array(
                'contributor_email'             => $username,
                'contributor_password'          => $password,
                'contributor_is_team_member'    => $team_member_field,
                'contributor_receive_email'     => '1'
            )
        );
    }

    /**
     * Return contributor information for the given id
     *
     * @param unknown_type $contributor_id
     * @return id on success, unknown on failure
     */
    function contributor_get_contributor_by_id($contributor_id) {
        return db_get_row_by_id('contributor', 'contributor_id', $contributor_id);
    }

    /**
     * Finds contributor data give the name
     *
     * @param string $contributor_name name of the contributor to find, must match exactly
     * @return false if not found, contributor info otherwise
     */
    function contributor_get_contributor_by_name($contributor_name) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_name`='$contributor_name' ");

        if (!$result) {
            return false;
        }

        return mysql_fetch_assoc($result);
    }

    /**
     * Finds contributor data give the email address
     *
     * @param string $contributor_username email address of the contributor
     * @return false if not found, contributor info otherwise
     */
    function contributor_get_contributor_by_username($contributor_username) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_email`='$contributor_username' ");

        if (!$result) {
            return false;
        }

        return mysql_fetch_assoc($result);
    }

    /**
     * Finds contributor data give the email address
     *
     * @param string $contributor_username email address of the contributor  FIXME: security risk: partial matches on email address
     * @return false if not found, contributor info otherwise
     */
    function contributor_get_contributor_by_username_fuzzy($contributor_username) {
        $result = db_exec_query("SELECT * FROM `contributor` WHERE `contributor_email` LIKE '%$contributor_username%' ");

        if (!$result) {
            return false;
        }

        return mysql_fetch_assoc($result);
    }

    function contributor_print_desc_list($contributor_desc, $wide) {
        if ($wide) {
            $wide_style = ' auto-width';

            $options = array(
                DEFAULT_CONTRIBUTOR_DESC,
                'I am a teacher interested in using PhET in the future',
                'I am a student who uses PhET',
                'I am a student interested in using PhET in the future',
                'I am just interested in physics',
                'Other'
            );
        }
        else {
            $wide_style = '';

            /*
                give shorthand descriptions for twoface login/register form
                (mainly for IE6 which does not display wide SELECT elements
                properly).
             */
            $options = array(
                DEFAULT_CONTRIBUTOR_DESC => "Teacher using PhET",
                'I am a teacher interested in using PhET in the future' => "Teacher new to PhET",
                'I am a student who uses PhET' => "Student using PhET",
                'I am a student interested in using PhET in the future' => "Student new to PhET",
                'I am just interested in physics' => "Interested in physics",
                'Other' => 'Other'
            );
        }

        print_single_selection(
            'contributor_desc',
            $options,
            $contributor_desc,
            "class=\"always-enabled$wide_style\""
        );
    }

    function contributor_print_full_edit_form($editor_contributor_id, $contributor_id, $script, $optional_message = null,
                                              $standard_message = "<p>You may edit your profile information below.</p>") {

        $editor_contributor = contributor_get_contributor_by_id($editor_contributor_id);

        $editor_is_team_member = $editor_contributor["contributor_is_team_member"];

        $contributor = contributor_get_contributor_by_id($contributor_id);

        // Removing unsafe function 'get_code_to_create_variables_from_array',
        // just doing the equivalent by hand
        //eval(get_code_to_create_variables_from_array($contributor));
        $contributor = format_for_html($contributor );
        $contributor_id = $contributor["contributor_id"];
        $contributor_email = $contributor["contributor_email"];
        $contributor_password = $contributor["contributor_password"];
        $contributor_is_team_member = $contributor["contributor_is_team_member"];
        $contributor_name = $contributor["contributor_name"];
        $contributor_organization = $contributor["contributor_organization"];
        $contributor_address = $contributor["contributor_address"];
        $contributor_office = $contributor["contributor_office"];
        $contributor_city = $contributor["contributor_city"];
        $contributor_state = $contributor["contributor_state"];
        $contributor_country = $contributor["contributor_country"];
        $contributor_postal_code = $contributor["contributor_postal_code"];
        $contributor_primary_phone = $contributor["contributor_primary_phone"];
        $contributor_secondary_phone = $contributor["contributor_secondary_phone"];
        $contributor_fax = $contributor["contributor_fax"];
        $contributor_title = $contributor["contributor_title"];
        $contributor_receive_email = $contributor["contributor_receive_email"];
        $contributor_last_known_ip = $contributor["contributor_last_known_ip"];
        $contributor_desc = $contributor["contributor_desc"];

        if ($contributor_desc == '') {
            $contributor_desc = DEFAULT_CONTRIBUTOR_DESC;
        }

        $contributor_receive_email_checked  = $contributor_receive_email  == '1' ? 'checked="checked"' : '';
        $contributor_is_team_member_checked = $contributor_is_team_member == '1' ? 'checked="checked"' : '';

        print <<<EOT
            <form method="post" action="$script">
                <fieldset>
                    <legend>Profile for $contributor_name</legend>

EOT;

        if ($optional_message !== null) {
            print "$optional_message";
        }

        print <<<EOT
                    $standard_message

                    <table class="form">
                        <tr>
                            <td>password (optional, enter here and below if you want to change it)</td>

                            <td>
                                <input type="password" name="new_contributor_password" size="25"/>
                            </td>
                        </tr>

                        <tr>
                            <td>retype password</td>

                            <td>
                                <input type="password" name="new_contributor_password2" size="25"/>
                            </td>
                        </tr>

                        <tr>
                            <td>name*</td>

                            <td>
                                <input type="text" name="contributor_name" value="$contributor_name" size="25"/>
                            </td>
                        </tr>

                        <tr>
                            <td>organization*</td>

                            <td>
                                <input type="text" name="contributor_organization" value="$contributor_organization" size="25"/>
                            </td>
                        </tr>

                        <tr>
                            <td>description*</td>

                            <td>

EOT;
                                contributor_print_desc_list($contributor_desc, true);

                        print <<<EOT
                            </td>
                        </tr>

                        <tr>
                            <td>job title</td>

                            <td>
                                <input type="text" name="contributor_title" value="$contributor_title" size="25" />
                            </td>
                        </tr>

                        <tr>
                            <td>address line 1</td>

                            <td>
                                <input type="text" name="contributor_address" value="$contributor_address" size="25" />
                            </td>
                        </tr>

                        <tr>
                            <td>address line 2</td>

                            <td>
                                <input type="text" name="contributor_office" value="$contributor_office" size="25" />
                            </td>
                        </tr>

                        <tr>
                            <td>city</td>

                            <td>
                                <input type="text" name="contributor_city"
                                    value="$contributor_city" id="city" size="15" />
                            </td>
                        </tr>

                        <tr>
                            <td>state/province</td>

                            <td>
                                <input type="text" name="contributor_state"
                                    value="$contributor_state" id="state" size="15" />
                            </td>
                        </tr>

                        <tr>
                            <td>country</td>

                            <td>
                                <input type="text" name="contributor_country"
                                    value="$contributor_country" id="country" size="15" />
                            </td>
                        </tr>

                        <tr>
                            <td>zip/postal code</td>

                            <td>
                                <input type="text" name="contributor_postal_code"
                                    value="$contributor_postal_code" id="postal_code" size="10" />
                            </td>
                        </tr>

                        <tr>
                            <td>primary phone</td>

                            <td>
                                <input type="text" name="contributor_primary_phone"
                                    value="$contributor_primary_phone" id="primary_phone" size="12" />
                            </td>
                        </tr>

                        <tr>
                            <td>secondary phone</td>

                            <td>
                                <input type="text" name="contributor_secondary_phone"
                                    value="$contributor_secondary_phone" id="secondary_phone" size="12" />
                            </td>
                        </tr>

                        <tr>
                            <td>fax</td>

                            <td>
                                <input type="text" name="contributor_fax"
                                    value="$contributor_fax" id="fax" size="12" />
                            </td>
                        </tr>

                        <tr>
                            <td>receive phet email</td>

                            <td>
                                <input type="hidden"   name="contributor_receive_email" value="0" />
                                <input type="checkbox" name="contributor_receive_email" value="1" $contributor_receive_email_checked />
                                <br/>
                                The PhET newsletter is sent 4 times per year to announce major changes to the simulations. You may unsubscribe at any time.
                            </td>
                        </tr>

EOT;

                if ($editor_is_team_member == 1) {
                    print <<<EOT
                        <tr>
                            <td>is team member</td>

                            <td>
                                <input type="hidden"   name="contributor_is_team_member" value="0" />
                                <input type="checkbox" name="contributor_is_team_member" value="1" $contributor_is_team_member_checked />
                            </td>
                        </tr>

EOT;
                }

                print <<<EOT
                    </table>

                    <input class="button" name="Submit" type="submit" id="submit" value="Done" />

                    <input type="hidden" name="contributor_id" value="$contributor_id" />
                 </fieldset>
            </form>

EOT;
    }

    function contributor_delete_contributor($contributor_id) {
        return db_delete_row('contributor',
            array(
                'contributor_id' => $contributor_id
            )
        );
    }

    function contributor_update_contributor($contributor_id, $update_array) {
        return db_update_table('contributor', $update_array, 'contributor_id', $contributor_id);
    }

    function contributor_update_contributor_from_script_parameters($contributor_id) {
        $params = gather_script_params_into_array('contributor_');

        contributor_update_contributor($contributor_id, $params);
    }

    function contributor_gather_fields_into_globals($contributor_id) {
        $contributor = contributor_get_contributor_by_id($contributor_id);

        foreach($contributor as $key => $value) {
            $GLOBALS["$key"] = "$value";
        }
    }

    // For lack of a better place right now
    function comment_id_is_valid($comment_id) {
        return !(false === db_get_row_by_condition("contribution_comment", array("contribution_comment_id" => $comment_id)));
    }

    function comment_get_comment_by_id($comment_id) {
        return db_get_row_by_condition("contribution_comment", array("contribution_comment_id" => $comment_id));
    }

    function comment_get_all_comments() {
        // Set up the query by hand
        $sql = "SELECT * FROM `contribution_comment` ".
            "LEFT JOIN `contribution` ".
            "ON `contribution_comment`.`contribution_id`=`contribution`.`contribution_id` ".
            "LEFT JOIN `contributor` ".
            "ON `contribution_comment`.`contributor_id`=`contributor`.`contributor_id`";
        $result = db_exec_query($sql);

        // Parse the results
        $rows = array();
        while ($row = mysql_fetch_assoc($result)) {
            $rows[] = $row;
        }

        stripslashes_deep($rows);

        return $rows;
    }

?>