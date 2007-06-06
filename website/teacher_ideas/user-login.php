<?php
    /*
    
        This script handles user login. It's designed to be included in 
        another script (the 'including script'), to restrict access to that
        script to individuals who have a contributor account on the PhET 
        website.
        
        If this script is successfully included, it will define a global
        variable 'contributor_id', corresponding to the id of the contributor,
        along with all other fields of the contributor, such as: 'contributor_name',
        'contributor_organization', 'contributor_email', etc.
    
    */
    
    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/authentication.php");

    do_authentication(true);
?>