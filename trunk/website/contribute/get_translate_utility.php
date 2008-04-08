<?php

    include_once("../admin/global.php");
    include_once(SITE_ROOT."admin/sys-utils.php");

    send_file_to_browser('translation-utility.jar', null, 'application/java-archive', 'attachment');

?>