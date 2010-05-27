<?php

    //============================================================================
    // This file contains functions that support the process of deploying the
    // installers to the production web site.
    //============================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("installer-util.php");
    require_once("file-util.php");

    //--------------------------------------------------------------------------
    // Create the version information file that is used by the PhET web site to
    // determine the creation date of the installers.
    //
    // IMPORTANT: This uses the time stamp value that is put into a temporary
    // file when the marker file is created, so the marker file must be
    // created prior to calling this function for the creation time to be
    // accurate.
    //--------------------------------------------------------------------------
    function deploy_create_version_info_file(){

        $version_info_file_path = OUTPUT_DIR.VERSION_INFO_FILE_NAME;
        flushing_echo( "Creating version information file, destination = ".$version_info_file_path );

        // Make sure that the file containing the creation time stamp exists.
        if (!file_exists(CREATION_TIMESTAMP_FILE_NAME)){
           flushing_echo( "Error: Timestamp file does not exist, file name = ".CREATION_TIMESTAMP_FILE_NAME);
           return;
        }
        
        // Get the value of the time stamp.
        $time = file_get_contents(CREATION_TIMESTAMP_FILE_NAME);
        flushing_echo( "Creation Timestamp used in version file = $time" );

        // Write the value into the version info file.
        $creation_time_property = 'timestamp='.$time;
        flushing_echo( "Contents of version info file: $creation_time_property" );
        file_put_contents_anywhere( $version_info_file_path, $creation_time_property );

        // Put the version file in the destination directory.
        copy($version_info_file_path, DEPLOY_DIR.VERSION_INFO_FILE_NAME);
    }

    //--------------------------------------------------------------------------
    // Deploy the installers to the production web site by copying them to the
    // appropriate directory.
    //--------------------------------------------------------------------------
    function deploy_installers() {

        // Verify that the installer files exist.
        flushing_echo("Attempting to deploy the installers.");
        if ( !file_exists( file_cleanup_local_filename( OUTPUT_DIR ) ) ) {
            flushing_echo("Error: Directory containing installers not found, aborting deployment.");
            return;
        }
        else {
            flushing_echo("Found the installer files.");
        }

        // Copy the files to the distribution directory.
        if ( copy( OUTPUT_DIR.LINUX_INSTALLER_FILE_NAME, DEPLOY_DIR.LINUX_INSTALLER_FILE_NAME ) ){
            flushing_echo( "Linux installer successfully copied to: ".DEPLOY_DIR );
        }
        else {
            flushing_echo( "Error: Unable to successfully copy linux installer to: ".DEPLOY_DIR );
        }
        if ( copy( OUTPUT_DIR.WINDOWS_INSTALLER_FILE_NAME, DEPLOY_DIR.WINDOWS_INSTALLER_FILE_NAME) ){
            flushing_echo( "Windows installer successfully copied to: ".DEPLOY_DIR );
        }
        else {
            flushing_echo( "Error: Unable to successfully copy windows installer to: ".DEPLOY_DIR );
        }
        if ( copy( OUTPUT_DIR.OSX_INSTALLER_FILE_NAME, DEPLOY_DIR.OSX_INSTALLER_FILE_NAME ) ){
            flushing_echo( "OSX installer successfully copied to: ".DEPLOY_DIR );
        }
        else {
            flushing_echo( "Error: Unable to successfully copy OSX installer to: ".DEPLOY_DIR );
        }
        if ( copy( OUTPUT_DIR.CD_ROM_INSTALLER_FILE_NAME, DEPLOY_DIR.CD_ROM_INSTALLER_FILE_NAME ) ){
            flushing_echo( "CD ROM installer successfully copied to: ".DEPLOY_DIR );
        }
        else {
            flushing_echo( "Error: Unable to successfully copy CD ROM installer to: ".DEPLOY_DIR );
        }
    }

    //--------------------------------------------------------------------------
    // Function to let the web site know that the installers have been updated.
    // This function relies on the presence of the local timestamp file.
    //--------------------------------------------------------------------------
    function deploy_inform_web_site(){

        // Make sure that the file containing the creation time stamp exists.
        if (!file_exists(CREATION_TIMESTAMP_FILE_NAME)){
           flushing_echo( "Error: Timestamp file does not exist, file name = ".CREATION_TIMESTAMP_FILE_NAME);
           return;
        }
        
        // Get the value of the time stamp.
        $timestamp = file_get_contents(CREATION_TIMESTAMP_FILE_NAME);
        flushing_echo( "Creation Timestamp used for updating web site = $timestamp" );

        // Send the HTTP request that updates the web site.
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "http://phetsims.colorado.edu/admin/new-installer?timestamp=$timestamp");
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $output = curl_exec($ch);
        flushing_echo($output);
        curl_close($ch);
    }

    //--------------------------------------------------------------------------
    // Function to deploy the installers and insert the creation date into the
    // database.
    //--------------------------------------------------------------------------
    function deploy_all(){

        // Copy the installers to the appropriate directory.
        deploy_installers();

        // Create the version file and copy it into place too.
        deploy_create_version_info_file();

        // Inform the web site that the update has occured.
        deploy_inform_web_site();
    }

?>
