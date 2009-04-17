<?php

    //=========================================================================
    // This file contains a script that rips the web site.  It either deletes
    // the local copy and re-rips the entire thing, or it refreshes only the
    // functionality for a single sim (excluding that sims activities).
    //=========================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("installer-util.php");
    require_once("rip-util.php");

    //-------------------------------------------------------------------------
    // Obtains the name of the simulation from the command line arguments and
    // takes the steps necessary to update that simulation in the local copy
    // of the web site and then rebuild the installers.
    //-------------------------------------------------------------------------
    function rip_website() {

        $args = $_SERVER['argv'];

        if (count($args) != 2){
            flushing_echo("Usage: $args[0] <sim-name or \"all\">");
            return;
        }
          
        $sim_name = $args[1];

        // Grab the lock to prevent multiple simultaneous executions.
        file_lock("install-builder");

        if ( $sim_name == "all" ) {
            flushing_echo( "Performing complete rip of web site: ".PHET_ROOT_URL );

            // Remove existing copy (i.e. mirror) of the web site.
            remove_website_copy();

            // Re-rip the entire web site, including any resources that aren't
            // obtained from following HTML links but that are needed
            // nonetheless.
            builder_rip_website();
            builder_download_sims();

            // Add the markers and timestamps needed in order to identify
            // this installation when running on a user's machine.
            builder_insert_installer_creation_time();
        }
        else {
            flushing_echo( "Performing rip for sim : ".$sim_name );

            // Remove any previous rips of single sims that are lying around.
            if ( file_exists( SINGLE_SIM_RIP_DIR ) ){
                flushing_echo( "Removing previous single sim rip, directory = ".SINGLE_SIM_RIP_DIR );
                exec( "rm -rfv ".SINGLE_SIM_RIP_DIR );
            }
            else {
                flushing_echo( "No previous single sim rip found in ".SINGLE_SIM_RIP_DIR.", not deleting anything." );
            }

            // Log the start time of this operation.
            $start_time = exec("date");
            flushing_echo("Starting refresh of sim $sim_name at time $start_time");

            // Rip the specified sim from the main web site.
            $rip_successful = rip_single_sim($sim_name);
            if ( $rip_successful == false ){
                flushing_echo("Error: Failed to obtain sim from the web site, sim = ".$sim_name);
                return;
            }

            // Copy the ripped files into the pre-existing full mirror.
            copy_sim_into_full_mirror( $sim_name );

            // Refresh the timestamp information.
            builder_create_marker_file();
            builder_insert_installer_creation_time();
        }

        // Release the lock.
        file_unlock("install-builder");

        // Output the time of completion.
        $end_time = exec("date");
        flushing_echo("\nCompleted rip at time $end_time");
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    rip_website();

?>
