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
    function builder_rip_sim($sim) {

        // Make sure that the specified sim already exists.  If not,
        // refreshing it is not allowed.
        $full_path_to_sim = RIPPED_WEBSITE_ROOT.PHET_SIMS_SUBDIR.$sim;
        flushing_echo("Full path to sim: ".$full_path_to_sim);

        flushing_echo("Stubbed, should be ripping sim: ".$sim);
        return true;
    }

    //-------------------------------------------------------------------------
    // Function for determining if the given simulation is a flash sim.  This
    // is done by looking for the expected file types.
    //-------------------------------------------------------------------------
    function is_flash_sim($sim) {
        flushing_echo("Stubbed, should be seeing if this is a flash sim: ".$sim);
    }
    
    //-------------------------------------------------------------------------
    // Function for determining if the given simulation is a java sim.  This
    // is done by looking for the expected file types.
    //-------------------------------------------------------------------------
    function is_java_sim($sim) {
        flushing_echo("Stubbed, should be seeing if this is a java sim: ".$sim);
    }

    //-------------------------------------------------------------------------
    // Function to remove all files that currently make up the specified sim.
    //-------------------------------------------------------------------------
    function remove_sim_files($sim) {
        flushing_echo("Stubbed, should be removing files for sim: ".$sim);
    }

    //-------------------------------------------------------------------------
    // Function for ripping a single Java sim from the web site.
    //-------------------------------------------------------------------------
    function rip_java_sim($sim) {
        flushing_echo("Stubbed, should be ripping java sim: ".$sim);
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
            flushing_echo("Usage: refresh_installers <sim-name>");
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
