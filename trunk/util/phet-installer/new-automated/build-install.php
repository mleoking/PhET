<?php

    //============================================================================
    // This file contains PHP functions for a number of the steps that are
    // necessary for creating the installer, such as ripping the web site, creating
    // the marker file, etc.  It is through the functions in this file that the
    // building of the installers is controlled, either from a web interface or
    // from shell scripts.
    //============================================================================


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

    //--------------------------------------------------------------------------
    // Function for ripping the main site, meaning that it traverses each link
    // and downloads the HTML files, images, etc that comprise the site.
    //--------------------------------------------------------------------------
    function builder_rip_website() {
        flushing_echo("Ripping website with ".RIPPER_EXE." ".RIPPER_ARGS);

        $result = exec(RIPPER_EXE." ".RIPPER_ARGS);

        flushing_echo($result);
    }

    //--------------------------------------------------------------------------
    // Function to remove an existing copy of the web site, i.e. one that was
    // previouly ripped.
    //--------------------------------------------------------------------------
    function builder_remove_website_copy() {
        flushing_echo("Checking if a previously ripped copy of the web site exists...");
        if (file_exists( RIPPED_WEBSITE_ROOT )){
            flushing_echo("Removing existing copy of the web site from ".RIPPED_WEBSITE_ROOT);
            $remove_cmd = "'/bin/ls -r ".RIPPED_WEBSITE_ROOT."'";
            system('rm -rf '.RIPPED_WEBSITE_ROOT);
        }
        else{
            flushing_echo("No existing copy of web site found...moving on.");
        }
    }

    //--------------------------------------------------------------------------
    // This function is primarily used for testing, and rips a subset of the
    // web site instead of the whole thing.  To use it, swap out the call
    // to the full ripper function.  Just don't forget to swap it back.
    //--------------------------------------------------------------------------
    function builder_rip_website_subset() {
        define("SUBSET_RIPPER_ARGS", '"'.PHET_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.'"+*'.PHET_ROOT_URL.'sims*"'.' -E30 -v %q0 -%e0'); 
        flushing_echo("Ripping subset of website with ".RIPPER_EXE." ".SUBSET_RIPPER_ARGS);

        $result = exec(RIPPER_EXE." ".SUBSET_RIPPER_ARGS);

        flushing_echo($result);
    }

    //--------------------------------------------------------------------------
    // Download needed Java resources that can't be obtained by ripping
    // the main web site (because there are no direct links to them).
    //--------------------------------------------------------------------------
    function builder_download_java_rsrcs() {

        flushing_echo("Downloading all Java simulation resources...");

        $jnlp_files = jnlp_get_all_in_directory(RIPPED_WEBSITE_TOP);

        $file_num = 1;

        foreach ($jnlp_files as $jnlp_filename => $jnlp) {
            flushing_echo("Processing JNLP file $file_num of ".count($jnlp_files)."...");

            $codebase = jnlp_get_codebase($jnlp);

            // This array will store any resources we can't find, due to programmer error
            // in constructing the JNLP file. These missing resources don't cause a problem
            // for webstart when run from the web, but do when running locally.
            $missing_resources = array();

            $resource_num = 1;

            $hrefs = jnlp_get_all_resource_links($jnlp);

            // Loop through each resource, downloading it to the ripped website directory:
            foreach ($hrefs as $href) {
                flushing_echo("Downloading resource $resource_num of ".count($hrefs)."...");

                if (!web_is_absolute_url($href)) {
                    flushing_echo("TOP");
                    $absolute_url = file_get_real_path($codebase.$href);
                }
                else {
                    flushing_echo("BOTTOM");
                    $absolute_url = $href;
                }

                if (($contents = file_get_contents($absolute_url)) !== false) {
                    $local_file_name = file_cleanup_local_filename(preg_replace(PHET_WEBSITE_ROOT_PATTERN, RIPPED_WEBSITE_TOP, $absolute_url));

                    if (!is_dir($local_file_name)) {
                        flushing_echo("Downloaded $absolute_url to $local_file_name\n");

                        file_put_contents_anywhere($local_file_name, $contents);
                    }
                }
                else {
                    flushing_echo("Could not find resource: $absolute_url. Removing element $href from JNLP file...\n");

                    $missing_resources[] = $href;
                }

                ++$resource_num;
            }

            // Strip out any missing resources from the JNLP file:
            foreach ($missing_resources as $missing_resource) {
                $jnlp = jnlp_remove_resource_link($jnlp, $missing_resource);
            }

            // Replace the codebase with a macro which can be replaced during the install process:
            $jnlp = jnlp_replace_codebase_with_local_file_macro($jnlp, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_INSTALLDIR_MACRO);

            // Replace any remaining 'http' href links with macro:
            $jnlp = jnlp_replace_absolute_links_with_local_file_macro($jnlp, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_INSTALLDIR_MACRO);

            // Output the new JNLP file:
            file_put_contents($jnlp_filename, $jnlp); 
            ++$file_num;
        }
    }

    // TODO: REWORKING THESE FUNCTIONS
    //--------------------------------------------------------------------------
    // Download needed Java resources that can't be obtained by ripping
    // the main web site (because there are no direct links to them).  Note
    // that this also modifies the JNLP files in prepration for inclusion in
    // the installer.
    //--------------------------------------------------------------------------
    function builder_download_java_rsrcs2() {

        flushing_echo("Downloading all Java simulation resources...");

        $jnlp_files = jnlp_get_all_in_directory(RIPPED_WEBSITE_TOP);

        $file_num = 1;

        foreach ($jnlp_files as $jnlp_filename) {
            flushing_echo("Processing JNLP file $file_num of ".count($jnlp_files)."...");
            flushing_echo("   JNLP file name = $jnlp_filename");

	    // Convert file to UTF8
	    $jnlp_contents = jnlp_convert_to_utf8($jnlp_filename);

            $codebase = jnlp_get_codebase($jnlp_filename);

            // This array will store any resources we can't find, due to programmer error
            // in constructing the JNLP file. These missing resources don't cause a problem
            // for webstart when run from the web, but do when running locally.
            $missing_resources = array();

            $resource_num = 1;

            $hrefs = jnlp_get_all_resource_links($jnlp_contents);

            // Loop through each resource, downloading it to the ripped website directory:
            foreach ($hrefs as $href) {
                flushing_echo("Downloading resource $resource_num of ".count($hrefs)."...");

                if (!web_is_absolute_url($href)) {
                    flushing_echo("TOP");
                    $absolute_url = file_get_real_path($codebase.$href);
                }
                else {
                    flushing_echo("BOTTOM");
                    $absolute_url = $href;
                }

                if (($contents = file_get_contents($absolute_url)) !== false) {
                    $local_file_name = file_cleanup_local_filename(preg_replace(PHET_WEBSITE_ROOT_PATTERN, RIPPED_WEBSITE_TOP, $absolute_url));

                    if (!is_dir($local_file_name)) {
                        flushing_echo("Downloaded $absolute_url to $local_file_name\n");

                        file_put_contents_anywhere($local_file_name, $contents);
                    }
                }
                else {
                    flushing_echo("Could not find resource: $absolute_url. Removing element $href from JNLP file...\n");

                    $missing_resources[] = $href;
                }

                ++$resource_num;
            }

            // Strip out any missing resources from the JNLP file:
            foreach ($missing_resources as $missing_resource) {
                $jnlp_contents = jnlp_remove_resource_link($jnlp_contents, $missing_resource);
            }

            // Replace the codebase with a macro which can be replaced during the install process:
            $jnlp_contents = jnlp_replace_codebase_with_local_file_macro($jnlp_contents, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_INSTALLDIR_MACRO);

            // Replace any remaining 'http' href links with macro:
            $jnlp_contents = jnlp_replace_absolute_links_with_local_file_macro($jnlp_contents, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_INSTALLDIR_MACRO);

            // Output the new JNLP file:
            file_put_contents($jnlp_filename, $jnlp_contents); 
            ++$file_num;
        }
    }

    //--------------------------------------------------------------------------
    // Download the required flash resources that can't be obtained by ripping
    // the main site (because there are no direct links to them).
    //--------------------------------------------------------------------------
    function builder_download_flash_rsrcs() {

        flushing_echo("Downloading all Flash simulation resources...");

        // Find all of the swf (for Shock Wave Flash) files in the ripped web
        // site files.
        $swf_files = file_list_in_directory(RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR, "*.swf");

        if ( count( $swf_files) == 0 ){
            // No flash files were found.  This is unexpected.
            flushing_echo("WARNING: No swf (i.e. Flash) sims detected.");
        }

        foreach ($swf_files as $swf_full_path) {
            // Extract key portions of the full path name in order to figure
            // out what needs to be requested from the web server.
            $last_slash_pos = strripos($swf_full_path, "/");
            $swf_filename = substr($swf_full_path, $last_slash_pos + 1);
            $core_filename = substr_replace($swf_filename, "", strripos($swf_filename, ".swf"));
            $destination_dir = substr_replace($swf_full_path, "", $last_slash_pos);
            flushing_echo("Obtaining resources for Flash sim $swf_filename");

            // Obtain a listing of all files in the corresponding directory
            // of the web site.  Note that this requires htaccess to be
            // turned on for this directory.
            $flash_resource_dir_listing = file_get_contents(PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$core_filename);

            // Go through the listing and extract the names of all .xml files,
            // and then copy them into the local directory containing the
            // ripped website.
            $regex = '/href=".*\.xml"/';
            preg_match_all($regex, $flash_resource_dir_listing, $xml_hrefs);

            if ( count ( $xml_hrefs[0] ) == 0 ){
                // This can happen if the Flash sim has never been translated,
                // but it is worth logging a warning.
                flushing_echo("WARNING: No xml files found for the $core_filename sim.");
            }
            else{
                foreach ($xml_hrefs[0] as $xml_href){
                    $xml_file_name = str_ireplace("href=\"", "", $xml_href);
                    $xml_file_name = str_ireplace("\"", "", $xml_file_name);
                    flushing_echo("Retrieving file $xml_file_name");
                    $xml_file_url = PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$core_filename."/".$xml_file_name;
                    $xml_local_name = $destination_dir."/".$xml_file_name;
                    $xml_file_contents = file_get_contents($xml_file_url);
                    file_put_contents_anywhere($xml_local_name, $xml_file_contents);
                }
            } 

            // Go through the listing and extract the names of all .properties
            // files (generally there will be only one), and then copy them
            // into the local directory containing the ripped website.

            $regex = '/href=".*\.properties"/';
            preg_match_all($regex, $flash_resource_dir_listing, $properties_hrefs);

            if ( count ( $properties_hrefs[0] ) == 0 ){
                // This can happen if the Flash sim has never been translated,
                // but it is worth logging a warning.
                flushing_echo("WARNING: No properties file found for the $core_filename sim.");
            }
            else {
                foreach ($properties_hrefs[0] as $properties_href){
                    $properties_file_name = str_ireplace("href=\"", "", $properties_href);
                    $properties_file_name = str_ireplace("\"", "", $properties_file_name);
                    flushing_echo("Retrieving file $properties_file_name");
                    $properties_file_url = PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$core_filename."/".$properties_file_name;
                    $properties_local_name = $destination_dir."/".$properties_file_name;
                    $properties_file_contents = file_get_contents($properties_file_url);
                    file_put_contents_anywhere($properties_local_name, $properties_file_contents);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // Ripping the web site obtains some of the files needed for running the
    // sims, but not all of them, since some files are not directly referenced
    // by the main web site.  This function obtains the other required
    // resources.
    //--------------------------------------------------------------------------
    function builder_download_sims() {

        // Get the resources for the Java sims.
        builder_download_java_rsrcs();

        // Get the resources for the Flash sims.
        builder_download_flash_rsrcs();

        // Add the marker file, needed for sim usage tracking.
        create_marker_file();
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
    function create_marker_file(){
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
    function insert_installer_creation_time(){

        // Get the value of the time stamp.
        $time = time();
        flushing_echo("Creation Timestamp = $time");
 
        // Add the time stamp to the marker file, used by the Java sims.
        $marker_file_name = RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR.MARKER_FILE_NAME;
        if (!($fp = fopen($marker_file_name, 'a'))) {
            flushing_echo('Error: Cannot open marker file, time stamp not added.');
        }
        else{
            fwrite($fp, "installer.creation.date.epoch.seconds=".$time."\n");
            flushing_echo('Successfully added timestamp marker to '.MARKER_FILE_NAME);
        }

        // Add the timestamp to each of the HTML files used to launch Flash sims.
        $files = glob(RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR."*/*.html");
        $sedCmd = 'sed -i -e s/@@INSTALLER_CREATION_TIMESTAMP@@/'.$time.'/ ';
        foreach ($files as $file){
            exec($sedCmd.$file);
        }
        flushing_echo("Processed ".sizeof($files)." HTML files for possible timestamp insertion.");

        // Write the timestamp to a temporary file so that we can use it later
        // if needed (such as for putting the creation timestamp into the DB).
        if (!($fp = fopen(CREATION_TIMESTAMP_FILE_NAME, 'w'))) {
            flushing_echo('Error: Unable to open temporary timestamp file.');
        }
        else{
            fwrite($fp, $time);
            flushing_echo('Successfully created timestamp file '.CREATION_TIMESTAMP_FILE_NAME);
        }
    }

    //--------------------------------------------------------------------------
    // Print the usage information.
    //--------------------------------------------------------------------------
    function print_help() {
        flushing_echo("Usage: build-install [--full]\n".
                      "                     [--remove-website-copy]\n".
                      "                     [--rip-website]\n".
                      "                     [--download-sims]\n".
                      "                     [--create-marker-file]\n".
                      "                     [--build-all]\n".
                      "                     [--help]\n");

    }

    //--------------------------------------------------------------------------
    // Function for analyzing the command line options.
    //--------------------------------------------------------------------------
    function is_checked($attr) {
        if (is_cmd_line_option_enabled($attr)) {
            return true;
        }
        else if (is_cmd_line_option_enabled("full")) {
            return true;
        }

        return isset($_REQUEST[$attr]);
    }

    //--------------------------------------------------------------------------
    // Main routine - interprests command line options and executes the
    // indicated requests.
    //--------------------------------------------------------------------------
    function main() {
        if (is_cmd_line_option_enabled("help")) {
            print_help();
        }
        else {
            if (file_lock("install-builder")) {

                // Note that the order of operations below is important in the
                // case where all operations are performed.

                if (is_checked('remove-website-copy'))
                    builder_remove_website_copy();

                if (is_checked('rip-website'))
                    builder_rip_website();

                if (is_checked('download-sims'))
                    builder_download_sims();

                if (is_checked('create-marker-file'))
                    create_marker_file();

                if (is_checked('insert-installer-creation-time'))
                    insert_installer_creation_time();

                if (is_checked('build-all'))
                    builder_build_all();
        }
        else {
                flushing_echo("The PhET AutoInstallBuilder appears to be completing a previous build. Refresh this page to ignore this warning and build anyway.");
        }

        file_unlock("install-builder");

        }
    }

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    main();

?>
