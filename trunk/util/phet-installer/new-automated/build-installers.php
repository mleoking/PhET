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
    require_once("jar-util.php");
    require_once("jnlp.php");
    require_once("ia.php");
    require_once("xml-util.php");
    require_once("build-util.php");
    require_once("rip-util.php");

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
    // Main routine - interprets command line options and executes the
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

                if (is_checked('insert-installer-creation-time'))
                    builder_insert_installer_creation_time();

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
