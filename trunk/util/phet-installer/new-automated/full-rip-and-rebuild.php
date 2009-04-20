<?php

    //=========================================================================
    // This file contains a script that rips (i.e. creates a local copy or
    // "mirror" of) the full PhET web site, rebuilds the installers, and
    // optionally deploys the installers to the production web site.
    //=========================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("installer-util.php");
    require_once("ripper-util.php");

    function print_usage() {
    }

    //-------------------------------------------------------------------------
    // Rip the web site, rebuild the installers, and then deploy them if the
    // user has so indicated.
    // takes the steps necessary to update that simulation in the local copy
    // of the web site and then rebuild the installers.
    //-------------------------------------------------------------------------
    function full_rip_and_rebuild() {

        $args = $_SERVER['argv'];

        if ( count( $args ) == 1 ){
            // Deploy flag is not present.
            $deploy = false;
        }
        else if ( count( $args >= 2 ) ){
            // Verify that the only accepted option is the 2nd argument.
            if ( ( $args[1] != "--deploy" ) || ( count( $args ) > 2 ) ) {
                flushing_echo("Usage: $args[0] [--deploy]");
                exit( 1 );
            }
            $deploy = true;
        }
          
        // Log the start time of this operation.
        $start_time = exec("date");
        flushing_echo("Starting full rip and rebuild of installers at time $start_time");

        // Remove previous copy of web site.
        ripper_remove_website_copy();

        // Rip the web site.
        ripper_rip_website();

        // Incorporate the timestamp information that indicates when this
        // version of the installer was created.
        installer_create_marker_file();
        installer_insert_installer_creation_time();

        // Rebuild the installers.
        installer_build_all();

        if ( $deploy ) {
            // Deploy the simulations to the production web site.
            deploy_installers();
        }

        // Output the time of completion.
        $end_time = exec("date");
        flushing_echo("\nCompleted rebuild at time $end_time");
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    full_rip_and_rebuild();

?>
