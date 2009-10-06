<?php

    //=========================================================================
    // This file contains a script that rips (i.e. creates a local copy or
    // "mirror" of) the Arabic translation of the PhET web site, rebuilds the
    // installer that enables the installation of a mirror of this site on
    // a different server.
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
    // Rip the web site and rebuild the installer.
    //-------------------------------------------------------------------------
    function full_rip_and_rebuild() {

        // Grab a file lock to prevent multiple simultaneous executions.
        if ( !file_lock( LOCK_FILE_STEM_NAME ) ){
            flushing_echo("ERROR: The PhET installer builder appears to be completing another build.");
            flushing_echo("If you believe this to be incorrect, use the appropriate script to force");
            flushing_echo("an unlock (something like \"force-unlock.sh\") and try again.");
            return;
        }

        // Log the start time of this operation.
        $start_time = exec("date");
        flushing_echo("Starting full rip and rebuild of KSU installer at time $start_time");

        // Remove previous copy of web site.
        ripper_remove_website_copy();

        // Rip the web site.
        ripper_rip_website();
        ripper_download_sims();

        // Make sure permissions of the ripped website are correct.
        file_chmod_recursive( RIPPED_WEBSITE_ROOT, 0775, 0775 );

        // Incorporate the timestamp information that indicates when this
        // version of the installer was created.
        installer_create_marker_file();
        installer_insert_installer_creation_time();

        // Insert the distribution tag.
        installer_insert_distribution_tag( DISTRIBUTION_TAG );

        // Build the full set of local mirror installers.
        installer_build_local_mirror_installers();

        // Move the installers into the location within the ripped web site
        // where they need to be in order to be incorporated within the web
        // mirror installer.
        rename( OUTPUT_DIR.WINDOWS_INSTALLER_FILE_NAME, RIPPED_WEBSITE_INSTALLER_DIR.WINDOWS_INSTALLER_FILE_NAME );
        rename( OUTPUT_DIR.LINUX_INSTALLER_FILE_NAME, RIPPED_WEBSITE_INSTALLER_DIR.LINUX_INSTALLER_FILE_NAME );
        rename( OUTPUT_DIR.OSX_INSTALLER_FILE_NAME, RIPPED_WEBSITE_INSTALLER_DIR.OSX_INSTALLER_FILE_NAME );
        rename( OUTPUT_DIR.CD_ROM_INSTALLER_FILE_NAME, RIPPED_WEBSITE_INSTALLER_DIR.CD_ROM_INSTALLER_FILE_NAME );

        // Remove the marker file, since we don't want it to be present in the
        // web mirror.
        installer_remove_marker_file();

        // Build the web mirror installer, meaning an installer that can be
        // used to set up a remotely hosted copy of the web site.
        installer_build_windows_web_mirror_installer();

        // Output the time of completion.
        $end_time = exec("date");
        flushing_echo("\nCompleted rebuild at time $end_time");

        // Release the lock.
        file_unlock( LOCK_FILE_STEM_NAME );
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    full_rip_and_rebuild();

?>
