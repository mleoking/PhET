<?php

    require_once("file-util.php");
    require_once("global.php");
    require_once("config.php");
    require_once("zip.lib.php");

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
