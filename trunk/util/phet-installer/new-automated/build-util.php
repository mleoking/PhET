<?php

    //============================================================================
    // This file contains PHP functions needed for building the installers.
    //============================================================================


    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("autorun.php");
    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("web-util.php");
    require_once("jar-util.php");
    require_once("jnlp.php");
    require_once("ia.php");
    require_once("xml-util.php");

    //-------------------------------------------------------------------------
    // Function to determine if this Java simulation is a post-IOM sim, which
    // means that it contains features that require system resources such as
    // disk and network access.  IMPORTANT NOTE: This is meant to be a
    // temporary function that should be removed once all the simulations
    // contain the IOM features and following the IOM naming and signing
    // conventions.
    //-------------------------------------------------------------------------
    function is_post_iom_sim( $jnlp_filename ) {
    
        // Determine the directory in which this sim resides.
        $sim_directory = substr( $jnlp_filename, 0, strripos( $jnlp_filename, "/", 0 ) );

        // Locate the main JAR file for this sim.  The function returns a
        // list, but there should only be one file on it.
        $jar_files = file_list_in_directory( $sim_directory, "*_all.jar" );

        // See if we found one and only one file.
        if ( count( $jar_files ) == 0 ) {
            // File with this name doesn't exist, must be a pre-IOM sim.
            echo "!!!!!!! No _all.jar file found in this directory\n";
            return false;
        }
        else if ( count( $jar_files ) > 1 ) {
            // This shouldn't happen, so log an error and force an exit.
            flushing_echo("Error: Multiple _all.jar files found in directory - this is unexpected - aborting.");
            exit(1);
        }
        else {
            // We found the one main JAR file.  Now see if it is signed.
            $primary_jar_file = $jar_files[0];
            if ( is_jar_signed( $primary_jar_file ) ){
                return true;
            }
            else{
                return false;
            }
        }
    }

    //--------------------------------------------------------------------------
    // Function for building installers for all of the supported platforms.
    //--------------------------------------------------------------------------
    function builder_build_all() {
        flushing_echo("Building all installers for all configurations...");

        // Create output directory:
        file_mkdirs(OUTPUT_DIR);

        // Make the autorun file for Windows CD-ROM (this copies installer stuff):
        autorun_create_autorun_file(basename(BITROCK_DIST_SRC_WINNT));

        // Build Windows, Linux, Mac installers:
        installer_build_installers("all");

        // Build CD-ROM distribution:
        installer_build_cd_rom_distribution();

        // Clean up autorun files:
        autorun_cleanup_files();
    }

    //--------------------------------------------------------------------------
    // Function for creating the marker file that will be used by the tracking
    // code to determine whether a simulation was run from full installation
    // versus some other means (such as a single downloaded JAR file).  See
    // unfuddle ticket #875 for more information.
    //--------------------------------------------------------------------------
    function builder_create_marker_file(){
        $marker_file_name = RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR.MARKER_FILE_NAME;
        flushing_echo("Marker file name = $marker_file_name");
        $contents = "# DO NOT DELETE THIS FILE.\n# This file identifies this installation of PhET.\n\n";
        file_put_contents_anywhere($marker_file_name, $contents);
    }

    //--------------------------------------------------------------------------
    // Insert the creation time of the installer into the marker file and into
    // the HTML files that launch the Flash simulations.  The marker file and
    // flash simulations must be modified at the same time so that the time
    // stamp will be the same for both.
    //--------------------------------------------------------------------------
    function builder_insert_installer_creation_time(){

        // Get the value of the time stamp.
        $time = time();
        flushing_echo( "Creation Timestamp = $time" );
 
        // Add the time stamp to the marker file, used by the Java sims.
        $marker_file_name = RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR.MARKER_FILE_NAME;
        if ( !( $fp = fopen( $marker_file_name, 'a' ) ) ) {
            flushing_echo('Error: Cannot open marker file, time stamp not added.');
        }
        else{
            fwrite( $fp, "installer.creation.date.epoch.seconds=".$time."\n" );
            flushing_echo( 'Successfully added timestamp marker to '.MARKER_FILE_NAME );
        }

        // Add the timestamp to each of the HTML files used to launch Flash 
        // sims.  If the timestamp is already present, update it.
        $html_file_names = glob( RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR."*/*.html" );
        foreach ( $html_file_names as $html_file_name ) {
            $html_file_contents = file_get_contents( $html_file_name );
            if ( strstr( $html_file_contents, '@@INSTALLER_CREATION_TIMESTAMP@@' ) != false ){
                // The timestamp has never been inserted into this file, so insert it.
                $html_file_contents = preg_replace( '/@@INSTALLER_CREATION_TIMESTAMP@@/', $time, $html_file_contents );
            }
            else{
                // The timestamp must have already been replaced, meaning that
                // the sim to which this HTML file corresponds was not
                // re-downloaded for this build of the installers.  In this
                // case, insert a new timestamp value over the old one.  NOTE:
                // The approach used here assumes a 10-digit timestamp, and
                // will only work until Nov of the year 2286.  If, God forbid,
                // this is still in use at that time, a new solution will need
                // to be devised.
                $html_file_contents = preg_replace( "/installerCreationTimestamp=........../", 'installerCreationTimestamp='.$time, $html_file_contents );
            }
            // Write the updated file back to the original location.
            file_put_contents_anywhere( $html_file_name, $html_file_contents );
        }
        flushing_echo( "Processed ".sizeof ( $html_file_names )." HTML files for possible timestamp insertion." );

        // Write the timestamp to a temporary file so that we can use it later
        // if needed (such as for putting the creation timestamp into the DB).
        if ( !( $fp = fopen(CREATION_TIMESTAMP_FILE_NAME, 'w' ) ) ) {
            flushing_echo( 'Error: Unable to open temporary timestamp file.' );
        }
        else{
            fwrite( $fp, $time );
            flushing_echo( 'Successfully created timestamp file '.CREATION_TIMESTAMP_FILE_NAME );
        }
    }

?>
