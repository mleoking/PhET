<?php

    require_once("file-util.php");

    // *****************************************************************************
    // Global Configuration
    define("OS_WINDOWS", "WINNT");
    define("OS_WIN32",   "WIN32");
    define("OS_AIX",     "AIX");
    define("OS_MAC",     "Darwin");
    define("OS_LINUX",   "Linux");
    define("OS_SUN",     "SunOS");

    define("WEBSITE_PAGES_PATTERN", '*.htm*, *.php');

    define("ROOT_DIR",      file_cleanup_local_filename(dirname(__FILE__)."/"));
    define("TEMP_DIR",      file_cleanup_local_filename(ROOT_DIR."temp/"));
    define("OUTPUT_DIR",    file_cleanup_local_filename(TEMP_DIR."installer-output/"));

    function GET_OS_BOUND_REL_PATH($constantPrefix) {
        return file_cleanup_local_filename(ROOT_DIR."${constantPrefix}/".PHP_OS."/");
    }

    function GET_OS_BOUND_NAME($constantPrefix) {
        return constant("${constantPrefix}_".PHP_OS);
    }

    // *****************************************************************************
    // PhET Website Configuration
    define("PHET_VERSION",                      "1.0");
    define("PHET_ROOT_URL",                     "http://phet.colorado.edu/");
    define("PHET_WEBSITE_URL",                  PHET_ROOT_URL);
    define("PHET_SIMS_SUBDIR",                  "sims/");
    define("PHET_RIPPER_FILTER",                '"+*phet.colorado.edu*" "+*phet.colorado.edu/sims*" "-*get-upload.php?contribution_file_id=*" "-*view-contribution.php*" "-*download-archive.php*" "-*/phet-dist/*" "-*search.php*" "-*get-run-offline.php*" "-*login-and-redirect.php*" "-*do-ajax-login.php*" "-*get-member-file.php*" "-*/teacher_ideas/browse.php?*" "-*digg.com*" "-*stumbleupon.com*" ');
    define("PHET_WEBSITE_ROOT_PARTIAL_PATTERN", '[^"]+colorado\.edu');
    define("PHET_WEBSITE_ROOT_PATTERN",         '/'.PHET_WEBSITE_ROOT_PARTIAL_PATTERN.'/');

    // *****************************************************************************
    // CD-ROM Configuration
    define("AUTORUN_FILENAME",                     'autorun.inf');
    define("AUTORUN_ICON_NAME",                 'phet-icon.ico');
    define("AUTORUN_ICON_SRC",                     file_cleanup_local_filename(ROOT_DIR."Installer-Resources/Install-Path/".AUTORUN_ICON_NAME));
    define("AUTORUN_ICON_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_ICON_NAME));
    define("AUTORUN_FILE_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_FILENAME));

    define("CDROM_FILE_NAME",                   "PhET-".PHET_VERSION."-CD-ROM.zip");
    define("CDROM_FILE_DEST",                   OUTPUT_DIR.CDROM_FILE_NAME);

    // *****************************************************************************
    // Website Ripper Configuration

    // The name of the HTTrack directory:
    define("RIPPER_DIR_NAME",    "HTTrack");

    define("RIPPED_WEBSITE_ROOT", file_cleanup_local_filename(TEMP_DIR."website/"));
    define("RIPPED_WEBSITE_TOP",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT."phet.colorado.edu/"));

    // The ripper executable itself:

    // The ripper executables per OS:
    define("RIPPER_EXE_Linux",   "httrack");
    define("RIPPER_EXE_WINNT",   "httrack.exe");
    define("RIPPER_EXE_Darwin",  "httrack");

    define("RIPPER_DIR",  GET_OS_BOUND_REL_PATH(RIPPER_DIR_NAME));
    define("RIPPER_EXE",  RIPPER_DIR.GET_OS_BOUND_NAME("RIPPER_EXE"));

    // Command-line args of the ripper:
    define("RIPPER_ARGS", '"'.PHET_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER.' -j %q0 -%e0');

    // *****************************************************************************
    // BitRock Configuration
    define("BITROCK_PLATFORM_EXEC_SUFFIX_OSX",         ".app");
    define("BITROCK_PLATFORM_EXEC_SUFFIX_WINDOWS",     ".exe");
    define("BITROCK_PLATFORM_EXEC_SUFFIX_LINUX",     ".bin");

    define("BITROCK_PRODUCT_SHORTNAME", "PhET");
    define("BITROCK_PLATFORM_OSX",      "osx");
    define("BITROCK_PLATFORM_WINDOWS",  "windows");
    define("BITROCK_PLATFORM_LINUX",    "linux");

    define("BITROCK_PRODUCT_VERSION",   PHET_VERSION);
    define("BITROCK_INSTALLDIR_MACRO",  '@@INSTALLDIR@@');

    define("BITROCK_DIR",               file_cleanup_local_filename(ROOT_DIR."BitRock/"));
    define("BITROCK_BUILDFILE_DIR",     file_cleanup_local_filename(BITROCK_DIR."projects/"));
    define("BITROCK_BUILDFILE",         file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."phet-installer-buildfile.xml"));
    define("BITROCK_EXE_DIR",           file_cleanup_local_filename(BITROCK_DIR));
    define("BITROCK_EXE_Linux",         "bitrock.sh");
    define("BITROCK_EXE_WINNT",         "bitrock.bat");
    define("BITROCK_EXE_Darwin",        "bitrock.sh");
    define("BITROCK_EXE",               GET_OS_BOUND_NAME("BITROCK_EXE"));

    define("BITROCK_DIST_DIR",          file_cleanup_local_filename(BITROCK_DIR."output/"));

    define("BITROCK_DIST_PREFIX",       BITROCK_PRODUCT_SHORTNAME.'-'.BITROCK_PRODUCT_VERSION.'-');
    define("BITROCK_DIST_POSTFIX",      "-installer");

    define("BITROCK_DISTNAME_WINNT",      BITROCK_DIST_PREFIX.BITROCK_PLATFORM_WINDOWS.BITROCK_DIST_POSTFIX.BITROCK_PLATFORM_EXEC_SUFFIX_WINDOWS);
    define("BITROCK_DISTNAME_Linux",      BITROCK_DIST_PREFIX.BITROCK_PLATFORM_LINUX  .BITROCK_DIST_POSTFIX.BITROCK_PLATFORM_EXEC_SUFFIX_LINUX);
    define("BITROCK_DISTNAME_Darwin",     BITROCK_DIST_PREFIX.BITROCK_PLATFORM_OSX    .BITROCK_DIST_POSTFIX.BITROCK_PLATFORM_EXEC_SUFFIX_OSX);

    define("BITROCK_DIST_SRC_WINNT",      file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_WINNT));
    define("BITROCK_DIST_SRC_Linux",      file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_Linux));
    define("BITROCK_DIST_SRC_Darwin",     file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_Darwin));

    define("BITROCK_DIST_DEST_WINNT",      file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_WINNT));
    define("BITROCK_DIST_DEST_Linux",      file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_Linux));
    define("BITROCK_DIST_DEST_Darwin",  file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_Darwin));

    define("BITROCK_PRE_ARGS",            " build ");

    define("BITROCK_OUTPUT_DIR",         file_cleanup_local_filename(BITROCK_DIR."output/"));

    $g_bitrock_dists = array(
        BITROCK_PLATFORM_WINDOWS => BITROCK_DIST_SRC_WINNT,
        BITROCK_PLATFORM_LINUX      => BITROCK_DIST_SRC_Linux,
        BITROCK_PLATFORM_OSX      => BITROCK_DIST_SRC_Darwin
    );

?>
