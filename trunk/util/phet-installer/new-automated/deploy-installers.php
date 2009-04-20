<?php

    //============================================================================
    // This file contains script that simply copies the installers from the
    // temporary directory where they should end up after being built to the
    // directory used by the main PhET web site.
    //============================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("installer-util.php");

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    deploy_installers();

?>
