<?php       
    /*
    
    A very simple, one-file password protection mechanism that uses cookies,
    designed to be used to restrict access to certain pages to those 
    contributors who are PhET team members.
    
    If the user hasn't logged in, the script will prompt the user to login.
    If the user isn't a team member, the script will print an error message
    and exit.
    
    */    
    include_once("../admin/authentication.php");
    
    do_authentication(true);
    
    function print_not_team_member() {
        print <<<EOT
            <h1>Permissions Error</h1>

            <p class="error">
                You do not have permission to access this page, because you are not a PhET team member.
            </p>
EOT;
        }

    if (!$contributor_is_team_member) {
        print_site_page('print_not_team_member', 9);
        
        exit;
    }
?>