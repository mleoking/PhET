<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/password-protection.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    
    function do_update() { 
        $contributor_id = $_REQUEST['contributor_id'];
        
        contributor_update_contributor_from_script_parameters($contributor_id);
    }
    
    function do_new() {
        $contributor_id = contributor_add_new_blank_contributor();
        
        $_REQUEST['contributor_id']       = "$contributor_id";
        $_REQUEST['contributor_password'] = web_create_random_password();
        
        do_update();
    }
    
    function do_delete() {
        eval(get_code_to_create_variables_from_array($_REQUEST));
        
        contributor_delete_contributor($contributor_id);
    }

    function handle_action() {
        eval(get_code_to_create_variables_from_array($_REQUEST));
    
        if (isset($action)) {
            if ($action == "update") {
                do_update();
            }
            else if ($action == "new") {
                do_new();
            }
            else if ($action == "delete") {
                do_delete();
            }
        }
    }

    function print_contributors() {
        eval(get_code_to_create_variables_from_array($_REQUEST));
    
        $contributors = contributor_get_all_contributors();

        print <<<EOT
        <h1>Manage Contributors</h1>
        
        <p>With this form, you can add, delete, and update the status of PhET contributors, including
        PhET team members.</p>
        
        <div class="compact">
            <table>
                <thead>
                    <tr>
                        <td>Name</td>   <td>Email</td>  <td>Team</td>    <td>Action</td>
                    </tr>
                </thead>
                
                <tbody>
                    <tr>
EOT;
    
        foreach($contributors as $contributor) {
            $contributor_id             = $contributor['contributor_id'];
            $contributor_name           = $contributor['contributor_name'];
            $contributor_email          = $contributor['contributor_email'];
            $contributor_is_team_member = $contributor['contributor_is_team_member'];
        
            $checked_status = $contributor_is_team_member == '1' ? "checked=\"checked\"" : "";
        
            print <<<EOT
                    <form action="manage-contributors.php" method="post">
                        <input type="hidden" name="action"          value="update" />
                        <input type="hidden" name="contributor_id"  value="$contributor_id" />
                        <input type="hidden" name="contributor_is_team_member" value="0" />

                        <tr>
                            <td><input type="text"     name="contributor_name"           value="$contributor_name"  /></td>
                            <td><input type="text"     name="contributor_email"          value="$contributor_email" /></td>
                            <td><input type="checkbox" name="contributor_is_team_member" value="1" $checked_status  /></td>

                            <td>
                                <input type="submit" name="action" value="update" />
                                <input type="submit" name="action" value="delete" />

								<a href="edit-other-profile.php?edit_contributor_id=$contributor_id">Edit</a>
                            </td>
                        </tr>
                    </form>
EOT;
        }
    
    
        print <<<EOT2
                    </tr>
                    
                    <tr>
                        <form action="manage-contributors.php" method="post">
                            <input type="hidden" name="action" value="new" />

                            <td><input type="text" name="contributor_name" /></td>
                            <td><input type="text" name="contributor_email" /></td>
                            <td><input type="checkbox" name="contributor_is_team_member" value="1" /></td>

                            <td><input type="submit" value="Add" /></td>
                        </form>                    
                    </tr>
                </tbody>
            </table>
        </div>
EOT2;
    }
    
    handle_action();
    
    print_site_page('print_contributors', 9);

?>