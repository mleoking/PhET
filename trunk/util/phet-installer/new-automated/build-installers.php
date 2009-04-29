<?php

    //============================================================================
    // This file contains PHP functions for a number of the steps that are
    // necessary for creating the installer, such as ripping the web site, creating
    // the marker file, etc.  It is through the functions in this file that the
    // building of the installers is controlled, either from a web interface or
    // from shell scripts.
    //============================================================================


    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("autorun.php");
    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("web-util.php");
    require_once("jar-util.php");
    require_once("jnlp.php");
    require_once("installer-util.php");
    require_once("xml-util.php");
    require_once("ripper-util.php");

    //--------------------------------------------------------------------------
    // Main routine for this file.
    //--------------------------------------------------------------------------
    function build_installers() {
        installer_build_all();
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    build_installers();

?>
