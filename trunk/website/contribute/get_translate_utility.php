<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sys-utils.php");

    send_file_to_browser('translation-utility.jar', null, 'application/java-archive', 'attachment');

?>