<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sys-utils.php");

    $filename = PORTAL_ROOT.'sims/common-strings/common-strings.jar';
    send_file_to_browser($filename, null, 'application/java-archive', 'attachment');

?>