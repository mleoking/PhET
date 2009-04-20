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
    define( "SINGLE_SIM_RIP_TOP", file_cleanup_local_filename( SINGLE_SIM_RIP_DIR."phet.colorado.edu/" ) );

    //--------------------------------------------------------------------------
    // Function for ripping the main site, meaning that it traverses each link
    // and downloads the HTML files, images, etc that comprise the site.
    //--------------------------------------------------------------------------
    function ripper_rip_website() {
        flushing_echo("Ripping website with ".RIPPER_EXE." ".RIPPER_ARGS);

        $result = exec(RIPPER_EXE." ".RIPPER_ARGS);

        flushing_echo($result);
    }

    //-------------------------------------------------------------------------
    // Function for obtaining (a.k.a. "ripping") a single simulation from the
    // web site.
    //-------------------------------------------------------------------------
    function ripper_rip_single_sim( $sim_name ) {

        // Make sure that the specified sim already exists.  If not,
        // refreshing it is not allowed.
        $full_path_to_sim = RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR.$sim_name;
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
        $java_rip_command = RIPPER_EXE." ".'"'.PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$sim_name.'"'.' -I0 -q -v'." -O ".SINGLE_SIM_RIP_DIR.' \'-*\''.' \'+*.jnlp\''.' \'+*screenshot*\''.' \'+*thumbnail*\'';
        // The command below doesn't seem to save much time - maybe a minute -
        // in the process of ripping the web site.
        //$java_rip_command = RIPPER_EXE." ".RIPPER_ARGS.' --update -v';
        flushing_echo("Ripping files for sim ".$sim_name." with command: ".$java_rip_command);
        system( $java_rip_command );

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
        $flash_rip_command = RIPPER_EXE." ".'"'.PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$sim_name.'"'.' -I0 -q -v'." -O ".SINGLE_SIM_RIP_DIR.' \'-*\''.' \'+*.swf\''.' \'+*.html\''.' \'+*screenshot*\''.' \'+*thumbnail*\'';
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
        $full_rip_sim_path = RIPPED_WEBSITE_TOP.PHET_SIMS_SUBDIR.$sim_name.'/';

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
        define("SUBSET_RIPPER_ARGS", '"'.PHET_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.'"+*'.PHET_ROOT_URL.'sims*"'.' -E30 -v %q0 -%e0'); 
        flushing_echo("Ripping subset of website with ".RIPPER_EXE." ".SUBSET_RIPPER_ARGS);

        $result = exec(RIPPER_EXE." ".SUBSET_RIPPER_ARGS);

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
                    $local_file_name = 
                        file_cleanup_local_filename(preg_replace(PHET_WEBSITE_ROOT_PATTERN, $directory , $absolute_url));

                    if ( file_exists( $local_file_name ) ) {
                        flushing_echo("File $local_file_name already exists locally, skipping download.");
                    }
                    else if ( is_dir( $local_file_name ) ) {
                        flushing_echo("File $local_file_name is a directory, skipping download.");
                    }
                    else {
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
            $jnlp = jnlp_replace_codebase_with_local_file_macro($jnlp, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_INSTALLDIR_MACRO);

            // Replace any remaining 'http' href links with macro:
            $jnlp = jnlp_replace_absolute_links_with_local_file_macro($jnlp, PHET_WEBSITE_ROOT_PARTIAL_PATTERN, BITROCK_INSTALLDIR_MACRO);

            // If this is a post-IOM simulation, add the permissions request.
            if ( installer_is_post_iom_sim( $jnlp_filename ) ){
                flushing_echo("Sim is post-IOM, adding permissions request to file: ".$jnlp_filename);
                $jnlp = jnlp_add_permissions_request( $jnlp );
            }
            else {
                flushing_echo("Sim is pre-IOM, not adding permissions request to file: ".$jnlp_filename);
            }

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
            $flash_resource_dir_listing = file_get_contents(PHET_WEBSITE_URL.PHET_SIMS_SUBDIR.$core_filename);

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
    function ripper_download_sims() {

        // Get the resources for the Java sims.
        ripper_download_java_rsrcs( RIPPED_WEBSITE_TOP );

        // Get the resources for the Flash sims.
        ripper_download_flash_rsrcs( RIPPED_WEBSITE_TOP );

        // Add the marker file, needed for sim usage tracking.
        installer_create_marker_file();
    }

?>
