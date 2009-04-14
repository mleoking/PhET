<?php

    //=========================================================================
    // This file contains the functionality needed to 'refresh' the installers
    // for a single simulation, meaning that the given simulation will be
    // obtained from the web site and then the installers will be rebuilt with
    // this (presumably new) version of the simulation and the currently
    // cached version of all other simulations.
    //=========================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("autorun.php");
    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("web-util.php");
    require_once("jnlp.php");
    require_once("ia.php");
    require_once("xml-util.php");
    require_once("build-install.php");

    //--------------------------------------------------------------------------
    // Local definitions.
    //--------------------------------------------------------------------------

    // The name of the directory where the rip of the single simulation will be
    // temporarily stored before being moved in to the full local web site
    // copy.
    define( "SINGLE_SIM_RIP_DIR", file_cleanup_local_filename( TEMP_DIR."single-sim-rip/" ) );

    // Directory where sim rip will end up.
    define( "SINGLE_SIM_RIP_TOP", file_cleanup_local_filename( SINGLE_SIM_RIP_DIR."phet.colorado.edu/" ) );

    //-------------------------------------------------------------------------
    // Function for obtaining (a.k.a. "ripping") a single simulation from the
    // web site.
    //-------------------------------------------------------------------------
    function rip_single_sim( $sim_name ) {

        // Make sure that the specified sim already exists.  If not,
        // refreshing it is not allowed.
        $full_path_to_sim = RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR.$sim_name;
        if ( !file_exists( $full_path_to_sim ) ) {
            flushing_echo( "Error: Unable to locate sim: ".$sim_name.", aborting." );
            return false;
        }
        else{
            flushing_echo( "Found sim, full path is: ".$full_path_to_sim );
        }

        // Determine the type of sim and perform the appropriate rip.
        if ( is_java_sim( $full_path_to_sim ) ) {
            flushing_echo( "This is a Java sim" );
            rip_java_sim( $sim_name );
        }
        else if ( is_flash_sim( $full_path_to_sim ) ) {
            flushing_echo( "This is a Flash sim" );
            rip_flash_sim( $sim_name );
        }
        else {
            flushing_echo( "Error: The sim ".$sim_name." does not appear to be a Flash or Java sim, aborting." );
            return false;
        }

        return true;
    }

    //-------------------------------------------------------------------------
    // Function for determining if the given simulation is a flash sim.  This
    // is done by looking for the expected file types.
    //-------------------------------------------------------------------------
    function is_flash_sim( $sim_directory ) {

        // See if there are any SWF files present.
        $swf_file_list = file_list_in_directory( $sim_directory, "*.swf" );

        if ( count($swf_file_list ) > 0 ){
            // Assume that this is a Flash sim due to the presence of SWF files.
            return true;
        }
        else{
            // No SWF files, must not be a Flash sim.
            return false;
        }
    }
    
    //-------------------------------------------------------------------------
    // Function for determining if the given simulation is a java sim.  This
    // is done by looking for the expected file types.
    //-------------------------------------------------------------------------
    function is_java_sim( $sim_directory ) {

        // See if there are any JNLP files present.
        $jnlp_file_list = file_list_in_directory( $sim_directory, "*.jnlp" );

        if ( count($jnlp_file_list ) > 0 ){
            // Assume that this is a Java sim due to the presence of JNLP files.
            return true;
        }
        else{
            // No JNLP files, must not be a Java sim.
            return false;
        }
    }

    //-------------------------------------------------------------------------
    // Function for ripping a single Java sim from the web site.
    //-------------------------------------------------------------------------
    function rip_java_sim( $sim_name ) {
        $java_rip_command = RIPPER_EXE." ".'"'.PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$sim_name.'"'.' -I0 -q -v'." -O ".SINGLE_SIM_RIP_DIR.' \'-*\''.' \'+*.jnlp\''.' \'+*screenshot*\'';
        // The command below doesn't seem to save much time - maybe a minute -
        // in the process of ripping the web site.
        //$java_rip_command = RIPPER_EXE." ".RIPPER_ARGS.' --update -v';
        flushing_echo("Ripping files for sim ".$sim_name." with command: ".$java_rip_command);
        system( $java_rip_command );

        // Download the additional resources that are needed by this sim but
        // that are not directory obtained through a rip of the web site.
        builder_download_java_rsrcs( SINGLE_SIM_RIP_TOP );
    }

    //-------------------------------------------------------------------------
    // Function for ripping a single flash sim from the web site.
    //-------------------------------------------------------------------------
    function rip_flash_sim( $sim_name ) {
        $flash_rip_command = RIPPER_EXE." ".'"'.PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$sim_name.'"'.' -I0 -q -v'." -O ".SINGLE_SIM_RIP_DIR.' \'-*\''.' \'+*.swf\''.' \'+*.html\''.' \'+*screenshot*\''.' \'+*thumbnail*\'';
        flushing_echo("Ripping files for sim ".$sim_name." with command: ".$flash_rip_command);
        system( $flash_rip_command );

        // Download the additional resources that are needed by this sim but
        // that are not directory obtained through a rip of the web site.
        builder_download_flash_rsrcs( SINGLE_SIM_RIP_TOP );
    }

    //-------------------------------------------------------------------------
    // Copies a simulation from the single sim mirror into the full web site
    // mirror.
    //-------------------------------------------------------------------------
    function copy_sim_into_full_mirror( $sim_name ) {

        // Copy over newly ripped files.
        $full_rip_sim_path = RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR.$sim_name.'/';
        $single_sim_rip_path = SINGLE_SIM_RIP_TOP.PHET_SIMS_SUBDIR.$sim_name;
        $copy_command = "cp -f $single_sim_rip_path".'/* '."$full_rip_sim_path";
        echo "!!!!! $copy_command \n";
        flushing_echo("Copying sim files into full mirror.");
        exec( $copy_command );
    }

    //-------------------------------------------------------------------------
    // Obtains the name of the simulation from the command line arguments and
    // takes the steps necessary to update that simulation in the local copy
    // of the web site and then rebuild the installers.
    //-------------------------------------------------------------------------
    function refresh_installers() {

        $args = $_SERVER['argv'];

        if (count($args) != 2){
            flushing_echo("Usage: $args[0] <sim-name>");
            return;
        }
          
        $sim_name = $args[1];

        // Grab the lock to prevent multiple simultaneous executions.
        file_lock("install-builder");

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

        // Rebuild the installers.
        flushing_echo("Stubbed: Should rebuild the intallers here.");

        // Release the lock.
        file_unlock("install-builder");

        // Output the time of completion.
        $end_time = exec("date");
        flushing_echo("Completed refresh at time $end_time");
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    refresh_installers();

?>
