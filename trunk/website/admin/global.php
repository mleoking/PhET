<?php

    /**
     * This constant is used so that included scripts can reference the files 
     * they require using an absolute path, which seems to be required due to
     * odd behavior of require/include functions.
     */     
    define("SITE_ROOT", "../");

    ini_set('upload_max_filesize', '20M');
?>