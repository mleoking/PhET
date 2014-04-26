<?php

    //=========================================================================
    // This file contains a script that rips (i.e. creates a local copy or
    // "mirror" of) the of the PhET web site, makes modifications to the files,
    // and then rebuilds the PhET installers that enable the installation of a
    // local mirror of the site on a user's machine.
    //=========================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("args.php");
    require_once("config.php");
    require_once("deploy-util.php");
    require_once("file-util.php");
    require_once("global.php");
    require_once("installer-util.php");
    require_once("ripper-util.php");

    //-------------------------------------------------------------------------
    // Rip the web site and rebuild the installer.
    //-------------------------------------------------------------------------
    function full_rip_and_rebuild() {

        $args = new Args();

        // Grab a file lock to prevent multiple simultaneous executions.
        if ( !file_lock( LOCK_FILE_STEM_NAME ) ){
            flushing_echo( "ERROR: The PhET installer builder appears to be completing another build." );
            flushing_echo( "If you believe this to be incorrect, use the appropriate script to force" );
            flushing_echo( "an unlock (something like \"force-unlock.sh\") and try again." );
            return;
        }

        // Log the start time of this operation.
        $start_time = exec( "date" );
        flushing_echo( "Starting full rip and rebuild of PhET installers at time $start_time" );

        // Build installers without activities.
        perform_build_steps( "PHET", OUTPUT_DIR, OUTPUT_DIR.CD_ROM_INSTALLER_FILE_NAME  );

        // Build installers with activities.
        perform_build_steps( "PHET_WITH_ACTIVITIES", INSTALLERS_WITH_ACTIVITIES_DIR, OUTPUT_DIR.DVD_ROM_INSTALLER_FILE_NAME );

        // If specified, deploy the installers to the production web site.
        if ( $args->flag( 'deploy' ) ){
           deploy_all();
        }

        // Output the time of completion.
        $end_time = exec( "date" );
        flushing_echo( "\nCompleted rebuild at time $end_time" );

        // Release the lock.
        file_unlock( LOCK_FILE_STEM_NAME );
    }

    /**
     * Consolidate any files ripped from the phet-downloads.colorado.edu domain
     * with the files ripped from phet.colorado.edu by copying the former into
     * the space where the latter were stored, and then modify any links in the
     * HTML files.  See Unfuddle #3616 for more information about this.
     */
    function consolidate_ripped_files(){

       if ( file_exists( RIPPED_WEBSITE_ROOT.'/phet-downloads.colorado.edu' ) ){
          flushing_echo( 'Found files phet-downloads.colorado.edu, consolidating with phet.colorado.edu' );
          file_recurse_copy( RIPPED_WEBSITE_ROOT.'/phet-downloads.colorado.edu', RIPPED_WEBSITE_ROOT.'/phet.colorado.edu' );

          // Replace any references (i.e. links) to files in the phet-downloads dir.
          $all_html_files = file_list_in_directory( RIPPED_WEBSITE_ROOT, '*.html' );
          $replacement_count = 0;
          foreach ( $all_html_files as $file_name ){
             // Assume that only files in the 'contributions' directories
             // contain any references.  This speeds this conversion up a lot,
             // but is brittle, and will break if any other things start being
             // served from phet-downloads.
             if ( strpos( $file_name, 'contributions' ) !== false ){
                file_replace_string_in_file( $file_name, '\.\.\/phet-downloads.colorado.edu\/', '' );
                $replacement_count++;
             }
          }
          flushing_echo( 'Processed '.$replacement_count.' files in the contributions dir for replacement of phet-downloads domain.' );
       }
    }

    function perform_build_steps( $rip_config, $output_dir, $rommable_output_dir ){

        // Remove previous copy of web site.
        ripper_remove_website_copy();

        // Log the time at which the rip started.
        $rip_start_time = exec( "date" );
        flushing_echo( "Rip for config $rip_config started at time $rip_start_time" );

        // Rip the web site.
        ripper_rip_website( $rip_config );
        ripper_download_sims();
        ripper_get_all_permissions_jars();

        // Log the time at which the rip completed.
        $rip_finish_time = exec( "date" );
        flushing_echo( "Rip for config $rip_config completed at time $rip_finish_time" );

        // Make sure permissions of the ripped website are correct.
        file_chmod_recursive( RIPPED_WEBSITE_ROOT, 0775, 0775 );

        // Incorporate the timestamp information that indicates when this
        // version of the installer was created.
        installer_create_marker_file();

        // Move the individually translated jar files out of the sims directory
        // so that they won't be included in the local mirror installers.
        ripper_move_out_translated_jars();

        // Move any files that were served from the phet-download domain into
        // the main rip. See Unfuddle ticket #3616 for more info if needed.
        consolidate_ripped_files();

        // Insert the creation time into the marker file.  Note that there is
        // no distribution tag for the standard PhET installers.
        installer_insert_installer_creation_time( null );

        // Build the local installers, meaning installers that can be used to
        // install a local mirror of the PhET web site.
        installer_build_local_mirror_installers( BITROCK_PHET_LOCAL_MIRROR_BUILDFILE, $output_dir );

        // Build the rommable distribution, which contains all of the installers
        // installers and is suitable for burning on CD.
        installer_build_rommable_distribution( $output_dir, $rommable_output_dir );
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    full_rip_and_rebuild();

?>
