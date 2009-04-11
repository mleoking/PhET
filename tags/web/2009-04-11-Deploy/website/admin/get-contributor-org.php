<?php

    // This file is called in JavaScript for some AJAX goodness.

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/contrib-utils.php");

    if (!isset($_REQUEST['contributor_email'])) {
        exit;
    }

    $contributor = contributor_get_contributor_by_username($_REQUEST['contributor_email']);

    if ($contributor) {
        print format_for_html($contributor['contributor_organization']);
    }

?>