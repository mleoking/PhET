<?php

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/sys-utils.php");

    $filename = SIMS_ROOT.'java-common-strings/java-common-strings_en.jar';
    send_file_to_browser($filename, null, 'application/java-archive', 'attachment');

?>