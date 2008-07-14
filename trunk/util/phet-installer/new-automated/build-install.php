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

    // JPB TBD !!! - Temp for making flash internationalization work.
    function copy_flash_files() {
        flushing_echo("Copying flash files into ripped website directory");

        exec("cp ../sims/arithmetic/*.xml ./temp/website/phet.colorado.edu/sims/arithmetic/");
        exec("cp ../sims/arithmetic/*.properties ./temp/website/phet.colorado.edu/sims/arithmetic/");
        exec("cp ../sims/black-body-radiation/*.xml ./temp/website/phet.colorado.edu/sims/black-body-radiation/");
        exec("cp ../sims/black-body-radiation/*.properties ./temp/website/phet.colorado.edu/sims/black-body-radiation/");
        exec("cp ../sims/charges-and-fields/*.xml ./temp/website/phet.colorado.edu/sims/charges-and-fields/");
        exec("cp ../sims/charges-and-fields/*.properties ./temp/website/phet.colorado.edu/sims/charges-and-fields/");
        exec("cp ../sims/curve-fit/*.xml ./temp/website/phet.colorado.edu/sims/curve-fit/");
        exec("cp ../sims/curve-fit/*.properties ./temp/website/phet.colorado.edu/sims/curve-fit/");
        exec("cp ../sims/equation-grapher/*.xml ./temp/website/phet.colorado.edu/sims/equation-grapher/");
        exec("cp ../sims/equation-grapher/*.properties ./temp/website/phet.colorado.edu/sims/equation-grapher/");
        exec("cp ../sims/lens/*.xml ./temp/website/phet.colorado.edu/sims/lens/");
        exec("cp ../sims/lens/*.properties ./temp/website/phet.colorado.edu/sims/lens/");
        exec("cp ../sims/lunar-lander/*.xml ./temp/website/phet.colorado.edu/sims/lunar-lander/");
        exec("cp ../sims/lunar-lander/*.properties ./temp/website/phet.colorado.edu/sims/lunar-lander/");
        exec("cp ../sims/mass-spring-lab/*.xml ./temp/website/phet.colorado.edu/sims/mass-spring-lab/");
        exec("cp ../sims/mass-spring-lab/*.properties ./temp/website/phet.colorado.edu/sims/mass-spring-lab/");
        exec("cp ../sims/my-solar-system/*.xml ./temp/website/phet.colorado.edu/sims/my-solar-system/");
        exec("cp ../sims/my-solar-system/*.properties ./temp/website/phet.colorado.edu/sims/my-solar-system/");
        exec("cp ../sims/projectile-motion/*.xml ./temp/website/phet.colorado.edu/sims/projectile-motion/");
        exec("cp ../sims/projectile-motion/*.properties ./temp/website/phet.colorado.edu/sims/projectile-motion/");
        exec("cp ../sims/string-wave/*.xml ./temp/website/phet.colorado.edu/sims/string-wave/");
        exec("cp ../sims/string-wave/*.properties ./temp/website/phet.colorado.edu/sims/string-wave/");
        exec("cp ../sims/vector-math/*.xml ./temp/website/phet.colorado.edu/sims/vector-math/");
        exec("cp ../sims/vector-math/*.properties ./temp/website/phet.colorado.edu/sims/vector-math/");

    }
    // End JPB TBD

    function builder_download_sims() {
        // Here we have to find and parse all jnlp files, and download all the
        // resources referenced in the codebase.
        flushing_echo("Downloading all simulations and their resources...");

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
                    $absolute_url = file_get_real_path($codebase.$href);
                }
                else {
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
                if (is_checked('rip-website')){
                    builder_rip_website();
                    copy_flash_files();
                }

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
