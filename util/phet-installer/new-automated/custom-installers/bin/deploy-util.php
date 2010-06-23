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
        curl_setopt($ch, CURLOPT_URL, "http://phet.colorado.edu/admin/new-installer?timestamp=$timestamp");
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $output = curl_exec($ch);
        curl_close($ch);
    }

    //--------------------------------------------------------------------------
    // Function to copy the currently deployed installers to a back up
    // directory.
    //--------------------------------------------------------------------------
    function deploy_back_up_current_installers(){

        // Make sure that the root directory for storing backups of previous
        // installers exists.
        if ( !is_dir( INSTALLER_BACKUP_ROOT_DIR ) ){
            flushing_echo( "Error: Root directory for installer backups does not exist, dirname = ".INSTALLER_BACKUP_ROOT_DIR );
            flushing_echo( "Aborting backup of current installers, NO BACKUPS HAVE BEEN CREATED." );
            return;
        }
        else{
            flushing_echo( "Root directory for installer backups found, proceeding with backup." );
        }

        // Create the subdirectory where the backups will be stored.  The
        // directory name will be based on today's date, as opposed to the
        // date when the installers were initially created.
        $backup_dir_stem_name = INSTALLER_BACKUP_ROOT_DIR."backup";
        $backup_dir_name = $backup_dir_stem_name.date("-Y-m-d").'/';
        if ( is_dir( $backup_dir_name ) ){
            // The backup directory already exists, which generally means that
            // the installers have already been built at least once today.  It
            // is probably better to keep the old ones rather than overwriting
            // them, so abort here.
            flushing_echo( "Warning: Backup directory already exists for today's date, ABORTING BACKUP OF CURRENT INSTALLERS." );
            return;
        }
        elseif( !mkdir( $backup_dir_name ) ){
            // Unable to create the backup directory.  Log a warning and abort.
            flushing_echo( "Error: Unable to create backup subdirectory, ABORTING BACKUP OF CURRENT INSTALLERS." );
            return;
        }
        else{
            flushing_echo( "Successfully created backup subdirectory, name = ".$backup_dir_name );
        }

        // Copy the existing installers into the backup directory.
        flushing_echo( "Copying currently deployed installers to backup directory." );
        back_up_file( DEPLOY_DIR.WINDOWS_INSTALLER_FILE_NAME, $backup_dir_name.WINDOWS_INSTALLER_FILE_NAME );
        back_up_file( DEPLOY_DIR.OSX_INSTALLER_FILE_NAME, $backup_dir_name.OSX_INSTALLER_FILE_NAME );
        back_up_file( DEPLOY_DIR.LINUX_INSTALLER_FILE_NAME, $backup_dir_name.LINUX_INSTALLER_FILE_NAME );
        back_up_file( DEPLOY_DIR.CD_ROM_INSTALLER_FILE_NAME, $backup_dir_name.CD_ROM_INSTALLER_FILE_NAME );

        // Copy the version file over too.
        back_up_file( DEPLOY_DIR.VERSION_INFO_FILE_NAME, $backup_dir_name.VERSION_INFO_FILE_NAME );

        // Delete any backups that have outlived their usefulness.
        remove_older_installer_backups();
    }

    //--------------------------------------------------------------------------
    // Function to back up the specified file by copying it to the specified
    // directory, printing out status information as appropriate.
    //--------------------------------------------------------------------------
    function back_up_file( $src_file, $dest_file ){
        if ( copy( $src_file, $dest_file ) ){
            flushing_echo( "Successfully copied ".$src_file." to ".$dest_file );
        }
        else{
            flushing_echo( "Error: Unable to copy ".$src_file." to ".$dest_file );
        }
    }

    //--------------------------------------------------------------------------
    // Function to remove backups that are no longer needed.
    //--------------------------------------------------------------------------
    define("NUM_BACKUPS_TO_MAINTAIN", 5); // Roughly a month's worth.
    function remove_older_installer_backups(){

        // Make a list of all backup diretories that exist.
        $backup_dir_list = glob(INSTALLER_BACKUP_ROOT_DIR."backup*", GLOB_ONLYDIR);

        // Are there more than enough backups?
        flushing_echo("Detected ".count( $backup_dir_list ). " existing installer backups.");
        $num_excess_backups = count( $backup_dir_list ) - NUM_BACKUPS_TO_MAINTAIN;
        if ( $num_excess_backups > 0 ){

            // Yes, there are.  We should delete the oldest ones.  BTW, this
            // loop assums that the directories are supplied in the same order
            // as an 'ls' command, which was found to always be true during
            // testing, but I (jblanco) don't know whether PHP guarantees this.

            flushing_echo( "Preparing to remove ".$num_excess_backups." excess backup(s)." );

            foreach($backup_dir_list as $backup_dir){
                flushing_echo("Removing backup dir = ".$backup_dir);
                rmdir($backup_dir);
                $num_excess_backups--;
                if ( $num_excess_backups <= 0 ){
                    // We now have the correct number of backups, so bail out
                    // of the loop.
                    break;
                }
            }
        }
        else{
            flushing_echo("Not removing any excess backups.");
        }
    }

    //--------------------------------------------------------------------------
    // Function to deploy the installers and insert the creation date into the
    // database.
    //--------------------------------------------------------------------------
    function deploy_all(){

        // Back up the current set of installers.
        deploy_back_up_current_installers();

        // Copy the new installers to the appropriate directory.
        deploy_installers();

        // Create the version file that specifies when the installers were
        // created.
        deploy_create_version_info_file();

        // Inform the web site that the update has occured.
        deploy_inform_web_site();
    }

?>
