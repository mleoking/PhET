<?php

    //========================================================================
    // This file contains contains the functions for creating and building
    // the installers.
    //========================================================================

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
    require_once("xml-util.php");
    require_once("zip.lib.php");

    //-------------------------------------------------------------------------
    // Function to determine if this Java simulation is a post-IOM sim, which
    // means that it contains features that require system resources such as
    // disk and network access.  IMPORTANT NOTE: This is meant to be a
    // temporary function that should be removed once all the simulations
    // contain the IOM features and following the IOM naming and signing
    // conventions.
    //-------------------------------------------------------------------------
    function installer_is_post_iom_sim( $jnlp_filename ) {
    
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
    function installer_build_all() {
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
    function installer_create_marker_file(){
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
    function installer_insert_installer_creation_time(){

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

    //-------------------------------------------------------------------------
    // Function for determining if the given simulation is a flash sim.  This
    // is done by looking for the expected file types.
    //-------------------------------------------------------------------------
    function installer_is_flash_sim( $sim_directory ) {

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
    function installer_is_java_sim( $sim_directory ) {

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

    //--------------------------------------------------------------------------
    // Deploy the installers to the production web site by copying them to the
    // appropriate directory.
    //--------------------------------------------------------------------------
    function deploy_installers() {

        // Verify that the installer files exist.
        flushing_echo("Attemping to deploy the installers.");
        if ( !file_exists( file_cleanup_local_filename( OUTPUT_DIR ) ) ) {
            flushing_echo("Error: Directory containing installers not found, aborting deployment.");
            return;
        }
        else {
            flushing_echo("Found the installer files.");
        }

        // Create a back of the existing files.
        // Note: This invokes an existing shell script.  At some point, it may
        // make sense to incorporate the functionality of this script into the
        // PHP code.
        exec(DEPLOY_DIR."create_backup.sh");

        // Copy the files to the distribution directory.
        if ( copy( OUTPUT_DIR.'*Installer*', DEPLOY_DIR ) ){
            flushing_echo( "Installers successfully copyed to: ".DEPLOY_DIR );
        }
        else {
            flushing_echo( "Error: Unable to successfully copy installers to: ".DEPLOY_DIR );
        }
    }

    function installer_get_full_dist_name($dist) {
        return "PhET-dist-$dist";
    }

    function installer_get_zipped_mac_bundle_name($distfile) {
        return file_remove_extension($distfile).".zip";
    }

    function installer_build_installers($dist, $macro_map = array()) {
        global $g_bitrock_dists;

        $build_prefix  = installer_get_full_dist_name($dist);
        $buildfile_ext = file_get_extension(BITROCK_BUILDFILE);

        $new_buildfile = BITROCK_BUILDFILE_DIR."${build_prefix}.${buildfile_ext}";

        file_create_parents_of_file($new_buildfile);

        if (!copy(BITROCK_BUILDFILE, $new_buildfile)) {
            return false;
        }

        $macro_map['VERSION'] = BITROCK_PRODUCT_VERSION;

        if (!file_replace_macros_in_file($new_buildfile, $macro_map)) {
            return false;
        }

        $return_var = 0;

        $cwd = getcwd();

        $exe_dir = file_with_local_separator(BITROCK_EXE_DIR);

        // Change working directory to location of EXE:
        chdir($exe_dir);

        foreach ($g_bitrock_dists as $platform => $distfile) {
            $cmd_line = $exe_dir.BITROCK_EXE.BITROCK_PRE_ARGS.'"'."$new_buildfile".'" '.$platform;

            $cmd_line = file_with_local_separator($cmd_line);

            flushing_echo("Executing $cmd_line");

            system($cmd_line, $return_var);

            if ($return_var != 0) {
                flushing_echo("BitRock failed to build installer $distfile for configuration $dist on platform $platform.");
            }

            if ($platform == BITROCK_PLATFORM_OSX) {
                // OSX requires special treatment - the .app directory must be bundled into a zip file.
                $zipped_bundle_name = installer_get_zipped_mac_bundle_name($distfile);

                flushing_echo("Zipping Mac OS X distribution $distfile to $zipped_bundle_name");

                file_native_zip($distfile, $zipped_bundle_name);

                // Remove application bundle:
                file_remove_all($distfile);
            }
        }

        chdir($cwd);

        flushing_echo("Copying installers to ".OUTPUT_DIR);

        // Now move everything in the BitRock directory to the output directory:
        file_copy(BITROCK_DIST_DIR, OUTPUT_DIR, true);

        return true;
    }

    function installer_build_cd_rom_distribution() {
        flushing_echo("Creating CD-ROM distribution ".CDROM_FILE_DEST);

        // Now make CD-ROM bundle:
        file_native_zip(
            array(
                BITROCK_DIST_DEST_WINNT,
                installer_get_zipped_mac_bundle_name(BITROCK_DIST_DEST_Darwin),
                BITROCK_DIST_DEST_Linux,
                AUTORUN_ICON_DEST,
                AUTORUN_FILE_DEST
            ),
            CDROM_FILE_DEST
        );
    }
?>
