<?php

    //=========================================================================
    // This file contains a script the optains only the files for the
    // specified sim from the web site and then updates the local copy (i.e.
    // mirror), and then rebuilds the installers based on this partially
    // updated mirror.  If specified by the parameters, the new installers
    // can also be deployed to the production web site.
    //=========================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("installer-util.php");
    require_once("ripper-util.php");

    //-------------------------------------------------------------------------
    // Print usage string for this script.
    //-------------------------------------------------------------------------
    function print_usage() {
        flushing_echo("Usage: $args[0] <simulation project name> [--deploy]");
    }

    //-------------------------------------------------------------------------
    // Obtains the name of the simulation from the command line arguments and
    // takes the steps necessary to update that simulation in the local copy
    // of the web site, and then rebuild the installers.
    //-------------------------------------------------------------------------
    function partial_rip_and_rebuild() {

        $args = $_SERVER['argv'];

        if ( ( count( $args ) == 1) || ( count( $args ) > 3 ) ) {
            // Incorrect number of arguments.
            print_usage();
            return;
        }

        // Figure out if the user wants to deploy.
        $deploy = false;
        if ( count( $args ) == 3 ) {
            if ( $args[2] == "--deploy" ) {
                $deploy = true;
            }
            else {
                // There is a second argument, but it appears incorrect.
                print_usage();
                return;
            }
        }

        // Get the name of the sim project to be ripped.
        $sim_name = $args[1];

        // Grab the lock to prevent multiple simultaneous executions.
        file_lock("install-builder");

        // Log the start time of this operation.
        $start_time = exec( "date" );
        flushing_echo( "Starting refresh of sim $sim_name at time $start_time" );

        // Remove any previous rips of single sims that are lying around.
        if ( file_exists( SINGLE_SIM_RIP_DIR ) ) {
            flushing_echo( "Removing previous single sim rip, directory = ".SINGLE_SIM_RIP_DIR );
            exec( "rm -rfv ".SINGLE_SIM_RIP_DIR );
        }
        else {
            flushing_echo( "No previous single sim rip found in ".SINGLE_SIM_RIP_DIR.", not deleting anything." );
        }

        // Rip the specified sim from the main web site.
        $rip_successful = ripper_rip_single_sim($sim_name);
        if ( $rip_successful == false ){
            flushing_echo("Error: Failed to obtain sim from the web site, sim = ".$sim_name);
            return;
        }

        // Copy the ripped files into the pre-existing full mirror.
        ripper_copy_sim_into_full_mirror( $sim_name );

        // Refresh the timestamp information.
        installer_create_marker_file();
        installer_insert_installer_creation_time();

        // Rebuild the installers.
        installer_build_all();

        // Release the lock.
        file_unlock("install-builder");

        // Output the time of completion.
        $end_time = exec("date");
        flushing_echo("\nCompleted refresh at time $end_time");
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    partial_rip_and_rebuild();

?>
