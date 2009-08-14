<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/web-utils.php");

    session_start();
    cookie_var_clear("contributor_email");
    cookie_var_clear("contributor_password_hash");
    session_write_close();

    if (isset($_REQUEST['url'])) {
        $url = $_REQUEST['url'];
    }
    else {
        $url = SITE_ROOT.'teacher_ideas/user-edit-profile.php';
    }

    // Do a header redirect
    force_header_redirect($url);

    // ...and do a meta refresh just in case
    force_redirect($url, 0);

?>