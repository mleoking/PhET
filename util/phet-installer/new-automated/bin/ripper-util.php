<?php

    //============================================================================
    // This file contains PHP functions for ripping the PhET web site.  Some of
    // the functions are somewhat general in nature, while others are very
    // specific to the needs of the PhET project.
    //============================================================================

    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("web-util.php");
    require_once("jar-util.php");
    require_once("jnlp.php");
    require_once("installer-util.php");
    require_once("xml-util.php");

    //--------------------------------------------------------------------------
    // Local definitions.
    //--------------------------------------------------------------------------

    // The name of the directory where the rip of the single simulation will be
    // temporarily stored before being moved in to the full local web site
    // copy.
    define( "SINGLE_SIM_RIP_DIR", file_cleanup_local_filename( TEMP_DIR."single-sim-rip/" ) );

    // Directory where sim rip will end up.
    define( "SINGLE_SIM_RIP_TOP", file_cleanup_local_filename( SINGLE_SIM_RIP_DIR.PHET_HOSTNAME_NO_COLONS.'/' ) );

    //--------------------------------------------------------------------------
    // Function for ripping the main site, meaning that it traverses each link
    // and downloads the HTML files, images, etc that comprise the site.
    //--------------------------------------------------------------------------
    function ripper_rip_website($config) {
        flushing_echo("Ripping website, config = ".$config);
        if ($config == "PHET"){
            $ripper_args = RIPPER_ARGS_PHET;
        }
        else if ($config == "YF"){
            $ripper_args = RIPPER_ARGS_YF;
        }
        else if ($config == "KSU"){
            $ripper_args = RIPPER_ARGS_KSU;
        }
        else {
            flushing_echo("ERROR: Unknown rip config ".$config.", assuming YF.");
            $ripper_args = RIPPER_ARGS_YF;
        }

        flushing_echo("Command for ripping web site: ".RIPPER_EXE." ".$ripper_args);

        $result = exec(RIPPER_EXE." ".$ripper_args);

        flushing_echo($result);
    }

    //-------------------------------------------------------------------------
    // Function for obtaining (a.k.a. "ripping") a single simulation from the
    // web site.
    //-------------------------------------------------------------------------
    function ripper_rip_single_sim( $sim_name ) {

        // Make sure that the specified sim already exists.  If not,
        // refreshing it is not allowed.
        $full_path_to_sim = RIPPED_WEBSITE_SIMS_PARENT_DIR.PHET_SIMS_SUBDIR.$sim_name;
        if ( !file_exists( $full_path_to_sim ) ) {
            flushing_echo( "Error: Unable to locate sim: ".$sim_name.", aborting." );
            return false;
        }       
        else{   
            flushing_echo( "Found sim, full path is: ".$full_path_to_sim );
        }

        // Determine the type of sim and perform the appropriate rip.
        if ( installer_is_java_sim( $full_path_to_sim ) ) {
            flushing_echo( "This is a Java sim" );
            ripper_rip_java_sim( $sim_name );
        }
        else if ( installer_is_flash_sim( $full_path_to_sim ) ) {
            flushing_echo( "This is a Flash sim" );
            ripper_rip_flash_sim( $sim_name );
        }
        else {
            flushing_echo( "Error: The sim ".$sim_name." does not appear to be a Flash or Java sim, aborting." );
            return false;
        }

        return true;
    }

    //-------------------------------------------------------------------------
    // Function for ripping a single Java sim from the web site.
    //-------------------------------------------------------------------------
    function ripper_rip_java_sim( $sim_name ) {
        $java_rip_command = RIPPER_EXE." ".'"'.PHET_ROOT_URL.PHET_SIMS_SUBDIR.$sim_name.'"'.' -I0 -q -v'." -O ".SINGLE_SIM_RIP_DIR.' \'-*\''.' \'+*.jnlp\''.' \'+*screenshot*\''.' \'+*thumbnail*\'';
        flushing_echo("Ripping files for sim ".$sim_name." with command: ".$java_rip_command);
        system( $java_rip_command );

        // Make sure the directory where the rip exists has the correct permissions.
        chmod( SINGLE_SIM_RIP_DIR, 0774 );

        // Download the additional resources that are needed by this sim but
        // that are not directory obtained through a rip of the web site.
        ripper_download_java_rsrcs( SINGLE_SIM_RIP_TOP );
    }

    //-------------------------------------------------------------------------
    // Function for checking if a local copy, a.k.a. mirror, of the web site
    // is present.
    //-------------------------------------------------------------------------
    function ripper_check_for_existing_mirror() {
        flushing_echo("!!! Stubbed !!!");
    }

    //-------------------------------------------------------------------------
    // Function for ripping a single flash sim from the web site.
    //-------------------------------------------------------------------------
    function ripper_rip_flash_sim( $sim_name ) {
        $flash_rip_command = RIPPER_EXE." ".'"'.PHET_ROOT_URL.PHET_SIMS_SUBDIR.$sim_name.'"'.' -I0 -q -v'." -O ".SINGLE_SIM_RIP_DIR.' \'-*\''.' \'+*.swf\''.' \'+*.html\''.' \'+*screenshot*\''.' \'+*thumbnail*\'';
        flushing_echo("Ripping files for sim ".$sim_name." with command: ".$flash_rip_command);
        system( $flash_rip_command );

        // Download the additional resources that are needed by this sim but
        // that are not directory obtained through a rip of the web site.
        ripper_download_flash_rsrcs( SINGLE_SIM_RIP_TOP );
    }

    //-------------------------------------------------------------------------
    // Copies a simulation from the single sim mirror into the full web site
    // mirror.
    //-------------------------------------------------------------------------
    function ripper_copy_sim_into_full_mirror( $sim_name ) {

        $single_sim_rip_path = SINGLE_SIM_RIP_TOP.PHET_SIMS_SUBDIR.$sim_name;
        $full_rip_sim_path = RIPPED_WEBSITE_SIMS_PARENT_DIR.PHET_SIMS_SUBDIR.$sim_name.'/';

        // The httrack ripper produces an index file, and though there is
        // supposed to be an option that prevents it from being created, it
        // doesn't seem to work as documented.  So, at least for now, we
        // explicitly get rid of it here.
        exec( "rm $single_sim_rip_path".'/index.html' );

        // Peform the actual copy operation.
        flushing_echo("Copying sim files into full mirror.");
        $copy_command = "cp -f $single_sim_rip_path".'/* '."$full_rip_sim_path";
        exec( $copy_command );
    }

    //--------------------------------------------------------------------------
    // Function to remove an existing copy of the web site, i.e. one that was
    // previouly ripped.
    //--------------------------------------------------------------------------
    function ripper_remove_website_copy() {
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
    function ripper_rip_website_subset() {
        flushing_echo("Ripping subset of website with ".RIPPER_EXE." ".RIPPER_ARGS_SUBSET);

        $result = exec(RIPPER_EXE." ".RIPPER_ARGS_SUBSET);

        flushing_echo($result);
    }

    //--------------------------------------------------------------------------
    // Download needed Java resources that can't be obtained by ripping
    // the main web site (because there are no direct links to them).
    //--------------------------------------------------------------------------
    function ripper_download_java_rsrcs( $directory ) {

        flushing_echo("Downloading all Java simulation resources found in $directory...");

        $jnlp_files = jnlp_get_all_in_directory( $directory );

        $file_num = 1;

        flushing_echo("Found ".count($jnlp_files)." JNLP files");

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
                // The following debug statement can be uncommented if a count
                // of downloaded resources is desired, but it tends to make
                // the log a bit verbose, so it is generally left commented
                // out.
                // flushing_echo("Downloading resource $resource_num of ".count($hrefs)."...");

                if (!web_is_absolute_url($href)) {
                    $absolute_url = file_get_real_path($codebase.$href);
                }
                else {
                    flushing_echo("BOTTOM");
                    $absolute_url = $href;
                }

                if (($contents = file_get_contents($absolute_url)) !== false) {
                    $local_file_name = 
                        file_cleanup_local_filename(preg_replace(PHET_WEBSITE_ROOT_PATTERN, $directory , $absolute_url));

                    if ( !file_exists( $local_file_name ) && !is_dir( $local_file_name ) ) {
                        file_put_contents_anywhere($local_file_name, $contents);
                        flushing_echo("Downloaded $absolute_url to $local_file_name\n");
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
            $jnlp = jnlp_replace_codebase_with_macro($jnlp, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_CODEBASE_MACRO);

            // Replace any remaining 'http' href links with macro:
            $jnlp = jnlp_replace_absolute_links_with_local_file_macro($jnlp, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_CODEBASE_MACRO);

            // Add the permissions request to the JNLP files.
            $jnlp = jnlp_add_permissions_request( $jnlp );

            // Output the new JNLP file:
            file_put_contents($jnlp_filename, $jnlp); 
            ++$file_num;
        }
    }

    //--------------------------------------------------------------------------
    // Download the required flash resources that can't be obtained by ripping
    // the main site (because there are no direct links to them).
    //--------------------------------------------------------------------------
    function ripper_download_flash_rsrcs( $directory ) {

        flushing_echo("Downloading all Flash simulation resources...");

        // Find all of the swf (for Shock Wave Flash) files in the ripped web
        // site files.
        $swf_files = file_list_in_directory( $directory.PHET_SIMS_SUBDIR, "*.swf");

        if ( count( $swf_files ) == 0 ) {
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
            $flash_resource_dir_listing = file_get_contents(PHET_ROOT_URL.PHET_SIMS_SUBDIR.$core_filename);

            // Go through the listing and extract the names of all .xml files,
            // and then copy them into the local directory containing the
            // ripped website.
            $regex = '/href=".*\.xml"/';
            preg_match_all($regex, $flash_resource_dir_listing, $xml_hrefs);

            if ( count ( $xml_hrefs[0] ) == 0 ) {
                // XML files may not be present in post-IOM sims, since (I
                // think) the translated strings are incorporated directly
                // into the HTML.
                flushing_echo("NOTE: No XML files found for the $core_filename sim.");
            }
            else {
                foreach ($xml_hrefs[0] as $xml_href) {
                    $xml_file_name = str_ireplace("href=\"", "", $xml_href);
                    $xml_file_name = str_ireplace("\"", "", $xml_file_name);
                    flushing_echo("Retrieving file $xml_file_name");
                    $xml_file_url = PHET_ROOT_URL.PHET_SIMS_SUBDIR.$core_filename."/".$xml_file_name;
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
                    $properties_file_url = PHET_ROOT_URL.PHET_SIMS_SUBDIR.$core_filename."/".$properties_file_name;
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
    function ripper_download_sims() {

        // Get the resources for the Java sims.
        ripper_download_java_rsrcs( RIPPED_WEBSITE_SIMS_PARENT_DIR );

        // Add the marker file, needed for sim usage tracking.
        installer_create_marker_file();
    }

    //--------------------------------------------------------------------------
    // Move the translated jar files out of the sims directory and into a
    // temporary directory.  Note that there is a sister function to this one
    // that moves them back.  This was created so that an installer can be
    // created that does not include these (often fairly large) files.
    //--------------------------------------------------------------------------
    function ripper_move_out_translated_jars() {

        // Create the directory where these files will reside (if it doesn't
        // already exist.
        if (!file_exists(TRANSLATED_JAR_TEMP_DIR)){
            mkdir(TRANSLATED_JAR_TEMP_DIR);
        }

        // Make a list of all the simulation directories.
        $sim_dir_contents = glob( RIPPED_WEBSITE_SIMS_PARENT_DIR.PHET_SIMS_SUBDIR."*" );

        // Loop through each directory moving all translated jar files.
        foreach ( $sim_dir_contents as $sim_dir ) {
            if (is_dir($sim_dir)){
                // Extract the name of the sim project.
                $sim_project_name_pos = strripos($sim_dir, "/") + 1;
                $sim_project_name = substr($sim_dir, $sim_project_name_pos);

                // Create the destination directory.
                $destination_dir = TRANSLATED_JAR_TEMP_DIR.$sim_project_name.'/';
                if (!file_exists($destination_dir)){
                    mkdir($destination_dir);
                }

                // Get a list of the jar files for this project.
                $project_dir_contents = glob( $sim_dir.'/*.jar' );
                foreach ( $project_dir_contents as $file_name ) {
                    if (!preg_match('/.*_all.jar/', $file_name)){  // Make sure we don't move the "all" jar.
                        // Get the name of the file by itself (w/o rest of path).
                        $short_file_name_pos = strripos($file_name, "/") + 1;
                        $short_file_name = substr($file_name, $short_file_name_pos);
                        // Perform the actual move operation.
                        rename($file_name, $destination_dir.$short_file_name);
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // This is a very specialized function that replaces the links that point
    // to the installer files linked to on the ripped site with links to the
    // main phet.colorado.edu site.  This was created to allow us to create an
    // installer for Young and Freedman that was ripped from the wicket site
    // but that pointed to installers on the main site.
    //--------------------------------------------------------------------------
    function ripper_replace_installer_links() {
        $installerLinkFileName = RIPPED_WEBSITE_ROOT.ENGLISH_LOCALE_STRING."/get-phet/full-install.html";
        if (file_exists($installerLinkFileName)){
            file_replace_string_in_file($installerLinkFileName, '\.\.\/\.\.\/installer\/PhET-Installer_windows\.exe', 'http://phet.colorado.edu/phet-dist/installers/PhET-Installer_windows.exe');
            file_replace_string_in_file($installerLinkFileName, '\.\.\/\.\.\/installer\/PhET-Installer_osx\.zip', 'http://phet.colorado.edu/phet-dist/installers/PhET-Installer_osx.zip');
            file_replace_string_in_file($installerLinkFileName, '\.\.\/\.\.\/installer\/PhET-Installer_linux\.bin', 'http://phet.colorado.edu/phet-dist/installers/PhET-Installer_linux.bin');
            file_replace_string_in_file($installerLinkFileName, '\.\.\/\.\.\/installer\/PhET-Installer_cdrom\.zip', 'http://phet.colorado.edu/phet-dist/installers/PhET-Installer_cdrom.zip');
        }
        else{
            print "Error: Unable to locate installer link file, skipping replacement operation.\n";
        }
    }

    //--------------------------------------------------------------------------
    // Move the translated jar files from the temporary directory back to the
    // appropriate directory in the copy of the web site.  Note that before
    // calling this, the function that moves the files to the temporary
    // directory should be called.  This function was created to address a
    // need to exclude the translated jar files from the local mirrors.
    //--------------------------------------------------------------------------
    function ripper_restore_translated_jars() {

        // Make sure the temporary directory exists.
        if (!file_exists(TRANSLATED_JAR_TEMP_DIR)){
            print "ERROR: Temporary directory for translated jar files does not exist, aborting.\n";
            return;
        }

        // Make a list of all the backup directories.
        $backup_dir_contents = glob( TRANSLATED_JAR_TEMP_DIR."*" );

        // Loop through each directory moving all translated jar files back to
        // where they belong.
        foreach ( $backup_dir_contents as $backup_dir ) {
            if (is_dir($backup_dir)){
                // Extract the name of the sim project.
                $sim_project_name_pos = strripos($backup_dir, "/") + 1;
                $sim_project_name = substr($backup_dir, $sim_project_name_pos);

                // Find the destination directory.
                $destination_dir = RIPPED_WEBSITE_SIMS_PARENT_DIR.PHET_SIMS_SUBDIR.$sim_project_name.'/';
                if (!file_exists($destination_dir)){
                    print "ERROR: Destination directory for translated jar files can not be found, skipping, dir = ".
                        $destination_dir."\n";
                    continue;
                }

                // Get a list of the files to be moved.
                $project_dir_contents = glob( $backup_dir.'/*.jar' );
                // Move all of the files in this directory.
                foreach ( $project_dir_contents as $file_name ) {
                    // Get the name of the file by itself (w/o rest of path).
                    $short_file_name_pos = strripos($file_name, "/") + 1;
                    $short_file_name = substr($file_name, $short_file_name_pos);
                    // Perform the actual move operation.
                    rename($file_name, $destination_dir.$short_file_name);
                }
                // Delete this backup directory.
                rmdir($backup_dir);
            }
        }

        // Remove the temporary directory where the translated jars where
        // stored.
        if (count(glob(TRANSLATED_JAR_TEMP_DIR."*") == 0)){
            rmdir(TRANSLATED_JAR_TEMP_DIR);
        }
        else{
            print "ERROR: ".TRANSLATED_JAR_TEMP_DIR." is not empty, not deleting it.\n";
        }
    }

?>
