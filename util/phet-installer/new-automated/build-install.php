<?php

    require_once("autorun.php");
    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("web-util.php");
    require_once("jnlp.php");
    require_once("ia.php");
    require_once("xml-util.php");

    function builder_rip_website() {
        flushing_echo("Ripping website with ".RIPPER_EXE." ".RIPPER_ARGS);

        $result = exec(RIPPER_EXE." ".RIPPER_ARGS);

        flushing_echo($result);
    }

    // This function is primarily used for testing, and rips a subset of the
    // web site instead of the whole thing.  To use it, swap out the call
    // to the full ripper function.  Just don't forget to swap it back.
    function builder_rip_website_subset() {
        define("SUBSET_RIPPER_ARGS", '"'.PHET_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.'"+*'.PHET_ROOT_URL.'sims*"'.' -E30 -v %q0 -%e0'); 
        flushing_echo("Ripping subset of website with ".RIPPER_EXE." ".SUBSET_RIPPER_ARGS);

        $result = exec(RIPPER_EXE." ".SUBSET_RIPPER_ARGS);

        flushing_echo($result);
    }

    function builder_download_java_rsrcs() {
        // Download needed Java resources that can't be obtained by ripping
        // the main web site (because there are no direct links to them).

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

    function builder_download_flash_rsrcs() {
        // Download needed flash resources that can't be obtained by ripping
        // the main web site (because there are no direct links to them).

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

    function builder_download_sims() {
        // Ripping the web site obtains some of the files needed for running
        // the sims, but not all of them, since some files are not directly
        // referenced by the main web site.  This function obtains the other
        // required resources.

        // Get the resources for the Java sims.
        builder_download_java_rsrcs();

        // Get the resources for the Flash sims.
        builder_download_flash_rsrcs();
    }

    function builder_download_installer_webpages() {
        // Here we download all the '-installer' versions of webpages, which are
        // not linked into the website and therefore not ripped.
        flushing_echo("SKIPPING: Not downloading installer versions of webpages...");
	return;

        flushing_echo("Downloading installer versions of webpages...");

        // Loop through all ripped webpages:
        foreach (file_list_in_directory(RIPPED_WEBSITE_TOP, WEBSITE_PAGES_PATTERN) as $local_name) {
            // Form the URL: [PhET Website]/[Webpage Name]-installer.htm
            $extension = file_get_extension($local_name);

            $local_installer_name = file_remove_extension($local_name)."-installer.$extension";

            $url = str_replace(RIPPED_WEBSITE_TOP, PHET_ROOT_URL, $local_installer_name);

            // If the file exists, get it and put it locally (replacing the online
            // version of the same name):
            if (($contents = file_get_contents($url)) !== false) {
                file_put_contents_anywhere($local_name, $contents);
            }
        }
    }

    function builder_perform_macro_substitutions() {
        // This function performs a macro substitution in all HTML files. For
        // example, $DATE$ is replaced by the current date.
        flushing_echo("Performing macro substitutions...");

        $macro_map = array();

        $macro_map["DATE"] = date("n/j/Y");

        foreach ($macro_map as $key => $value) {
            flushing_echo("\$$key\$ = $value");
        }

        foreach (file_list_in_directory(RIPPED_WEBSITE_TOP, WEBSITE_PAGES_PATTERN) as $filename) {
            file_replace_macros_in_file($filename, $macro_map);
        }
    }

    function builder_build_all() {
        flushing_echo("Building all installers for all configurations...");

        // Create output directory:
        file_mkdirs(OUTPUT_DIR);

        // Make the autorun file for Windows CD-ROM (this copies installer stuff):
        autorun_create_autorun_file(basename(BITROCK_DIST_SRC_WINNT));

        // Build Windows, Linux, Mac installers:
        installer_build_installers("all");

        // Build CD-ROM distribution:
        // Temporarily disable until PHP memory limit increased.
        installer_build_cd_rom_distribution();

        // Clean up autorun files:
        autorun_cleanup_files();
    }

    function builder_deploy_all() {
        flushing_echo("Deploying all installers for all configurations...");
    }

    function print_help() {
        flushing_echo("Usage: build-install [--full]\n".
                      "                     [--rip-website]\n".
                      "                     [--download-sims]\n".
                      "                     [--download-installer-webpages]\n".
                      "                     [--perform-macro-substitutions]\n".
                      "                     [--build-all]\n".
                      "                     [--deploy-all]\n".
                      "                     [--help]\n");

    }

    function is_checked($attr) {
        if (is_cmd_line_option_enabled($attr)) {
            return true;
        }
        else if (is_cmd_line_option_enabled("full")) {
            return true;
        }

        return isset($_REQUEST[$attr]);
    }

    function main() {
        if (is_cmd_line_option_enabled("help")) {
            print_help();
        }
        else {
            if (file_lock("install-builder")) {
                if (is_checked('rip-website'))
                    builder_rip_website();

                if (is_checked('download-sims'))
                    builder_download_sims();

                if (is_checked('download-installer-webpages'))
                    builder_download_installer_webpages();

                if (is_checked('perform-macro-substitutions'))
                    builder_perform_macro_substitutions();

                if (is_checked('build-all'))
                    builder_build_all();

                if (is_checked('deploy-all'))
                    builder_deploy_all();

                flushing_echo("All done!");
            }
            else {
                flushing_echo("The PhET AutoInstallBuilder appears to be completing a previous build. Refresh this page to ignore this warning and build anyway.");
            }

            file_unlock("install-builder");
        }
    }

    main();

?>
