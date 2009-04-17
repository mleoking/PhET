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

    //--------------------------------------------------------------------------
    // Main routine - interprets command line options and executes the
    // indicated requests.
    //--------------------------------------------------------------------------
    function deploy_installers() {
        // Verify that the files exist.
        flushing_echo("Attemping to deploy the installers.");
        if ( !file_exists( file_cleanup_local_filename( OUTPUT_DIR ) ) ) {
            flushing_echo("Error: Directory containing installers not found, aborting deployment.");
            return;
        }
        else {
            flushing_echo("Found it!");
        }
        flushing_echo("!!!! Deployment is stubbed !!!!!");
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    deploy_installers();

?>
