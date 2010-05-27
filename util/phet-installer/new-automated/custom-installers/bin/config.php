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

    define("ROOT_DIR",                  file_cleanup_local_filename(dirname(dirname(__FILE__))."/"));
    define("PARENT_DIR",                file_cleanup_local_filename(dirname(ROOT_DIR))."/");
    define("TEMP_DIR",                  file_cleanup_local_filename(ROOT_DIR."temp/"));
    define("OUTPUT_DIR",                file_cleanup_local_filename(TEMP_DIR."installer-output/"));
    define("DEPLOY_DIR",                "/web/htdocs/phet/phet-dist/installers/");
    define("TRANSLATED_JAR_TEMP_DIR",   file_cleanup_local_filename(TEMP_DIR."translated-jar-tmp/"));

    function GET_OS_BOUND_REL_PATH($constantPrefix) {
        return file_cleanup_local_filename(ROOT_DIR."${constantPrefix}/".PHP_OS."/");
    }

    function GET_OS_BOUND_NAME($constantPrefix) {
        return constant("${constantPrefix}_".PHP_OS);
    }

    function CREATE_FILTER_ITEM($controlChar, $hostName, $path){
        return '"'.$controlChar.$hostName."/".$path.'"';
    }

    // *****************************************************************************
    // Locale-specific configuration.
    define("ENGLISH_LOCALE_STRING",     "en");
    define("ARABIC_LOCALE_STRING",     "ar");
    
    // *****************************************************************************
    // PhET Website Configuration
    define("PHET_VERSION",                      "1.0");
    define("PHET_HOSTNAME",                     "phetsims.colorado.edu");
    define("PHET_ROOT_URL",                     "http://".PHET_HOSTNAME."/");
    define("PHET_ENGLISH_WEBSITE_URL",          PHET_ROOT_URL.ENGLISH_LOCALE_STRING."/");
    define("PHET_ARABIC_WEBSITE_URL",           PHET_ROOT_URL.ARABIC_LOCALE_STRING."/");
    define("PHET_SIMS_SUBDIR",                  "sims/");

    // Definition of the filters, which specify what to include/exclude from
    // the rip.  These filters are meant to capture the entire web site as
    // needed for either the PhET installers or customized for a specific
    // customer.  So far, that generally means getting it for a particular language.
    define("PHET_RIPPER_FILTER_PHET", '"-*wickettest*"'.' '.CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'ar/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'files/activities/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'installer/*'));
    define("PHET_RIPPER_FILTER_YF",   '"-*wickettest*"'.' '.CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'ar/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'files/activities/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'installer/*'));
    define("PHET_RIPPER_FILTER_KSU",  '"-*wickettest*"'.' '.CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'en/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'files/activities/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'installer/*'));

    // Filter definition for a "lite" rip, meaning one that rips less than
    // the full web site.  This is generally swapped in for the full rip
    // filters when doing testing that requires a lot of iterations, since 
    // this will generally be much quicker than a full rip.
    define("PHET_LITE_RIPPER_FILTER",            '"-*wickettest*"'.' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/balloons/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/battery-resistor-circuit/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/battery-voltage/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/blackbody-spectrum/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/bound-states/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/build-tools/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/calculus-grapher/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/cavendish-experiment/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/charges-and-fields/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/charges-and-fields-scala/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/circuit-construction-kit/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/color-vision/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/conductivity/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/curve-fitting/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/discharge-lamps/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/eating-and-exercise/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/efield/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/electric-hockey/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/energy-skate-park/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/equation-grapher/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/estimation/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/faradays-law/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/flash-common-strings/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/force-law-lab/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/forces-1d/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/fourier/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/friction/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/geometric-optics/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/glaciers/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/greenhouse/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/hydrogen-atom/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/ideal-gas/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/java-common-strings/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/ladybug-motion-2d/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/lasers/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/lunar-lander/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/mass-spring-lab/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/maze-game/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/microwaves/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/motion-2d/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/moving-man/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/mri/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/mvc-example/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/my-solar-system/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/natural-selection/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/nuclear-physics/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/ohms-law/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/old-blackbody-spectrum/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/optical-quantum-control/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/optical-tweezers/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/pendulum-lab/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/phetgraphics-demo/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/photoelectric/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/ph-scale/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/plinko-probability/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/projectile-motion/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/quantum-tunneling/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/quantum-wave-interference/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/radio-waves/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/reactions-and-rates/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/resistance-in-a-wire/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/resources/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/rotation/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/rutherford-scattering/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/self-driven-particle-model/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/semiconductor/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/signal-circuit/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/sim-template/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/soluble-salts/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/sound/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/states-of-matter/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/stern-gerlach/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/temp.txt/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/test-flash-project/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/test-project/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/the-ramp/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/translations/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/translation-utility/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/travoltage/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/vector-addition/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/wave-interference/*').' '.
        CREATE_FILTER_ITEM('-', PHET_HOSTNAME, 'sims/wave-on-a-string/*'));

    define("PHET_WEBSITE_ROOT_PARTIAL_PATTERN", '[^"]+colorado\.edu');
    define("PHET_WEBSITE_ROOT_PATTERN",         '/'.PHET_WEBSITE_ROOT_PARTIAL_PATTERN.'/');

    // *****************************************************************************
    // CD-ROM Configuration
    define("AUTORUN_FILENAME",                     'autorun.inf');
    define("AUTORUN_ICON_NAME",                 'phet-icon.ico');
    define("AUTORUN_ICON_SRC",                     file_cleanup_local_filename(PARENT_DIR."Installer-Resources/Install-Path/".AUTORUN_ICON_NAME));
    define("AUTORUN_ICON_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_ICON_NAME));
    define("AUTORUN_FILE_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_FILENAME));

    define("CDROM_FILE_NAME",                   "PhET-Installer_cdrom.zip");
    define("CDROM_FILE_DEST",                   OUTPUT_DIR.CDROM_FILE_NAME);

    // *****************************************************************************
    // Website Ripper Configuration

    define("PHET_HOSTNAME_NO_COLONS", preg_replace('/:/', '_', PHET_HOSTNAME)); // Needed for cases where URL contains
                                                                                // something like ":8080".
    define("RIPPED_WEBSITE_ROOT", file_cleanup_local_filename(TEMP_DIR."website/"));
    define("RIPPED_WEBSITE_SIMS_PARENT_DIR",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT.PHET_HOSTNAME_NO_COLONS.'/'));
    define("RIPPED_WEBSITE_INSTALLER_DIR",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT.PHET_HOSTNAME_NO_COLONS.'/installer/'));

    // The ripper executable itself:

    // The ripper executables per OS:
    define("RIPPER_EXE_Linux",   "httrack");
    define("RIPPER_EXE_WINNT",   "httrack.exe");
    define("RIPPER_EXE_Darwin",  "httrack");

    // The location of the httrack executable.
    define("RIPPER_DIR",  file_cleanup_local_filename("/usr/local/httrack/bin/"));
    define("RIPPER_EXE",  RIPPER_DIR.GET_OS_BOUND_NAME("RIPPER_EXE"));

    // User agent to indicate when ripping.  These are used to make the web site
    // react somewhat differently (generally filtering out some links) when it
    // is being ripped for the installers.
    define("RIPPER_USER_AGENT_PHET",  '"httrack-web-offline-en"');
    define("RIPPER_USER_AGENT_YF",    '"httrack-web-mirror-yf"');
    define("RIPPER_USER_AGENT_KSU",   '"httrack-web-mirror-ar"');

    // Command-line args for the ripper.
    define("RIPPER_OPTIONS", " -j %q0 -%e0 -r10 -s0 -A100000000 --disable-security-limits ");
    define("RIPPER_ARGS_PHET", '"'.PHET_ROOT_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER_PHET." -F ".RIPPER_USER_AGENT_PHET.RIPPER_OPTIONS);
    define("RIPPER_ARGS_YF", '"'.PHET_ROOT_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER_YF." -F ".RIPPER_USER_AGENT_YF.RIPPER_OPTIONS);
    define("RIPPER_ARGS_KSU", '"'.PHET_ROOT_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER_KSU." -F ".RIPPER_USER_AGENT_KSU.RIPPER_OPTIONS);
    define("RIPPER_VERSION_ARGS", " --version");

    // Command-line args for a quicker, lighter, less complete rip of the web
    // site.  These exist primarily for testing purposes.
    define("RIPPER_ARGS_SUBSET_PHET", '"'.PHET_ROOT_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_LITE_RIPPER_FILTER." -F ".RIPPER_USER_AGENT_PHET.' -j %q0 -%e0 -r10');
    define("RIPPER_ARGS_SUBSET_YF", '"'.PHET_ROOT_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_LITE_RIPPER_FILTER." -F ".RIPPER_USER_AGENT_YF.' -j %q0 -%e0 -r10');
    define("RIPPER_ARGS_SUBSET_KSU", '"'.PHET_ROOT_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_LITE_RIPPER_FILTER." -F ".RIPPER_USER_AGENT_KSU.' -j %q0 -%e0 -r10');

    // File used for preventing simultaneous builds.
    define("LOCK_FILE_STEM_NAME", "installer-builder");

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
    define("BITROCK_CODEBASE_MACRO",  '@@CODEBASE@@');

    // Custom Installer Note: Some of the bitrock files - such as the executable
    // itself - are maintained in the main installer directory, and some of the
    // custom-specific files are maintained in a separate location.  Hence, some
    // of the contants below point to one place and some point to another.
    define("BITROCK_DIR",               file_cleanup_local_filename(PARENT_DIR."BitRock/"));
    define("BITROCK_CUSTOM_DIR",        file_cleanup_local_filename(ROOT_DIR."BitRock/"));
    define("BITROCK_BUILDFILE_DIR",     file_cleanup_local_filename(BITROCK_CUSTOM_DIR."projects/"));
    // Project files for building the "web mirror installer", meaning the
    // installer that allows for installation on a server that can then serve
    // the mirror's contents to the web.
    define("BITROCK_YF_WEB_MIRROR_BUILDFILE", file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."yf-web-mirror-installer-buildfile.xml"));
    define("BITROCK_KSU_WEB_MIRROR_BUILDFILE", file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."ksu-web-mirror-installer-buildfile.xml"));
    define("BITROCK_KSU_LOCAL_MIRROR_BUILDFILE", file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."ksu-local-mirror-installer-buildfile.xml"));
    define("BITROCK_PHET_LOCAL_MIRROR_BUILDFILE", file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."phet-local-mirror-installer-buildfile.xml"));
    define("BITROCK_EXE_DIR",           file_cleanup_local_filename(BITROCK_DIR));
    define("BITROCK_EXE_Linux",         "bitrock.sh");
    define("BITROCK_EXE_WINNT",         "bitrock.bat");
    define("BITROCK_EXE_Darwin",        "bitrock.sh");
    define("BITROCK_EXE",               GET_OS_BOUND_NAME("BITROCK_EXE"));

    define("BITROCK_DIST_DIR",          file_cleanup_local_filename(BITROCK_CUSTOM_DIR."output/"));

    define("BITROCK_DIST_PREFIX",       BITROCK_PRODUCT_SHORTNAME."-Installer_");

    define("BITROCK_DISTNAME_WINNT",      BITROCK_DIST_PREFIX.BITROCK_PLATFORM_WINDOWS.BITROCK_PLATFORM_EXEC_SUFFIX_WINDOWS);
    define("BITROCK_DISTNAME_Linux",      BITROCK_DIST_PREFIX.BITROCK_PLATFORM_LINUX.BITROCK_PLATFORM_EXEC_SUFFIX_LINUX);
    define("BITROCK_DISTNAME_Darwin",     BITROCK_DIST_PREFIX.BITROCK_PLATFORM_OSX.BITROCK_PLATFORM_EXEC_SUFFIX_OSX);

    define("BITROCK_DIST_SRC_WINNT",      file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_WINNT));
    define("BITROCK_DIST_SRC_Linux",      file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_Linux));
    define("BITROCK_DIST_SRC_Darwin",     file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_Darwin));

    define("BITROCK_DIST_DEST_WINNT",      file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_WINNT));
    define("BITROCK_DIST_DEST_Linux",      file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_Linux));
    define("BITROCK_DIST_DEST_Darwin",     file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_Darwin));

    define("BITROCK_PRE_ARGS",            " build ");

    define("BITROCK_OUTPUT_DIR",          file_cleanup_local_filename(ROOT_DIR."BitRock/output/"));

    $g_bitrock_dists = array(
        BITROCK_PLATFORM_WINDOWS => BITROCK_DIST_SRC_WINNT,
        BITROCK_PLATFORM_LINUX      => BITROCK_DIST_SRC_Linux,
        BITROCK_PLATFORM_OSX      => BITROCK_DIST_SRC_Darwin
    );

    // *****************************************************************************
    // Installation Configuration

    define("DISTRIBUTION_TAG_KSU", "ksu-custom");
    define("DISTRIBUTION_TAG_YF",  "yf-custom");
    define("MARKER_FILE_NAME", "phet-installation.properties");
    define("CREATION_TIMESTAMP_FILE_NAME", "installer-creation-timestamp.txt");
    define("VERSION_INFO_FILE_NAME", "version.properties");
    define("VERSION_INFO_HTML_FILE_NAME", "version.html");
    define("LINUX_INSTALLER_FILE_NAME", "PhET-Installer_linux.bin");
    define("WINDOWS_INSTALLER_FILE_NAME", "PhET-Installer_windows.exe");
    define("OSX_INSTALLER_FILE_NAME", "PhET-Installer_osx.zip");
    define("CD_ROM_INSTALLER_FILE_NAME", "PhET-Installer_cdrom.zip");

    // *****************************************************************************
    // JAR file signing config information.
    define("CONFIG_FILE", "signing-config.ini");
?>
