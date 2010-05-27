<?php

    //============================================================================
    // This file contains functions that copy the installers from the temporary
    // directory where they should end up after initially being built to the
    // directory used by the main PhET web site.
    //============================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("installer-util.php");
    require_once("file-util.php");

    //--------------------------------------------------------------------------
    // Function to insert the timestamp for the creation of the installers into
    // the database that will be read by the deployed sims in order to
    // determine whether a new version of the installer is available.
    //--------------------------------------------------------------------------
    function insert_timestamp_into_db(){
        if ( !file_exists( CREATION_TIMESTAMP_FILE_NAME ) ){
            flushing_echo( "Error: Unable to locate timestamp file ".CREATION_TIMESTAMP_FILE_NAME.", time stamp not inserted into DB." );
        }

        // Read the timestamp, which should be the only thing in the file.
        $timestamp = file_get_contents( CREATION_TIMESTAMP_FILE_NAME );

        // Execute the script that inserts the value into the DB.
        exec( "/web/htdocs/phet/cl_utils/add-installer-timestamp.php $timestamp" );
    }

    //--------------------------------------------------------------------------
    // Function to deploy the installers and insert the creation date into the
    // database.
    //--------------------------------------------------------------------------
    function deploy_all(){
        deploy_installers();
        insert_timestamp_into_db();
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    deploy_all();

?>
