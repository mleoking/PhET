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

    //-------------------------------------------------------------------------
    // Function for obtaining (a.k.a. "ripping") a single simulation from the
    // web site.
    //-------------------------------------------------------------------------
    function builder_rip_sim( $sim_name ) {

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
            remove_sim_files( $full_path_to_sim );
            rip_java_sim( $sim_name );
        }
        else if ( is_flash_sim( $full_path_to_sim ) ) {
            flushing_echo( "This is a Flash sim" );
            remove_sim_files( $full_path_to_sim );
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
    // Function to remove all files that currently make up the specified sim.
    //-------------------------------------------------------------------------
    function remove_sim_files( $sim_directory ) {
        $remove_command = "rm -f ".$sim_directory.'/*';
        flushing_echo("STUBBED - Remove command = ".$remove_command);
        // system( $remove_command );
    }

    //-------------------------------------------------------------------------
    // Function for ripping a single Java sim from the web site.
    //-------------------------------------------------------------------------
    function rip_java_sim( $sim_name ) {
        //./HTTrack/Linux/httrack "http://phet.colorado.edu/sims/balloons/" -O ./rip_test '-*' '+*.jnlp' '+*screenshot*' -q
        //$java_rip_command = RIPPER_EXE." ".'"'.PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$sim_name'" -O "'.RIPPED_WEBSITE_ROOT;
        $java_rip_command = RIPPER_EXE;
        flushing_echo("STUBBED - Would rip Java sim with command:".$java_rip_command);
    }

    //-------------------------------------------------------------------------
    // Function for ripping a single flash sim from the web site.
    //-------------------------------------------------------------------------
    function rip_flash_sim($sim) {
        flushing_echo("Stubbed, should be ripping flash sim: ".$sim);
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
          
        file_lock("install-builder");
        $sim_name = $args[1];
        $rip_successful = builder_rip_sim($sim_name);
        if ($rip_successful){
            // TODO!!!! Rebuild the installers here if the previous operation worked.
            flushing_echo("Stubbed: Should rebuild the intallers here.");
        }
        else{
            flushing_echo("Error: Failed to obtain sim from the web site, sim = ".$sim_name);
        }

        file_unlock("install-builder");
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    refresh_installers();

?>
