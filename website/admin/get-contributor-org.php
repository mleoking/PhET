<?php

    // This file is called in JavaScript for some AJAX goodness.

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");

    if (!isset($_REQUEST['contributor_email'])) {
        exit;
    }

    $contributor = contributor_get_contributor_by_username($_REQUEST['contributor_email']);
    
    if ($contributor) {
        print format_for_html($contributor['contributor_organization']);
    }

?>