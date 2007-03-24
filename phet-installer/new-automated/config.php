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

define("ROOT_DIR", cleanup_local_filename(dirname(__FILE__)."/"));
define("TEMP_DIR", cleanup_local_filename(ROOT_DIR."temp/"));

function GET_OS_BOUND_REL_PATH($constantPrefix) {
    return cleanup_local_filename(ROOT_DIR."${constantPrefix}/".strtoupper(PHP_OS)."/");
}

function GET_OS_BOUND_NAME($constantPrefix) {
    return constant("${constantPrefix}_".strtoupper(PHP_OS));
}


// *****************************************************************************
// PhET Website Configuration
define("PHET_VERSION",              "1.0");
define("PHET_ROOT_URL",             "http://phet.colorado.edu/");
define("PHET_WEBSITE_URL",          PHET_ROOT_URL."web-pages/");
define("PHET_RIPPER_FILTER",        '"+*phet.colorado.edu/web-pages*" "+*phet.colorado.edu/simulations*"');
define("PHET_WEBSITE_ROOT_PATTERN", '/.+colorado\.edu/');
define("PHET_CODEBASE_MACRO",       'file:///$CODEBASE$/');
define("PHET_AUTORUN_ICON",         cleanup_local_filename(ROOT_DIR."Installer-Resources/Install-Path/Phet-logo-48x48.ico"));

// *****************************************************************************
// Website Ripper Configuration

// The name of the HTTrack directory:
define("RIPPER_DIR_NAME",    "HTTrack");

define("RIPPED_WEBSITE_ROOT", cleanup_local_filename(TEMP_DIR."website/"));
define("RIPPED_WEBSITE_TOP",  cleanup_local_filename(RIPPED_WEBSITE_ROOT."phet.colorado.edu/"));

// The ripper executable itself:

// The Windows and Linux executables:
define("RIPPER_EXE_LINUX",   "httrack");
define("RIPPER_EXE_WINNT",   "httrack.exe");

define("RIPPER_DIR",  GET_OS_BOUND_REL_PATH(RIPPER_DIR_NAME));
define("RIPPER_EXE",  RIPPER_DIR.GET_OS_BOUND_NAME("RIPPER_EXE")); 

// Command-line args of the ripper:
define("RIPPER_ARGS", '"'.PHET_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER.' -j ');

// *****************************************************************************
// BitRock Configuration

define("BITROCK_DIR",           cleanup_local_filename(ROOT_DIR."BitRock/"));
define("BITROCK_BUILDFILE_DIR", cleanup_local_filename(BITROCK_DIR."projects/"));
define("BITROCK_BUILDFILE",     cleanup_local_filename(BITROCK_BUILDFILE_DIR."phet-installer-buildfile.xml"));
define("BITROCK_EXE_DIR",       cleanup_local_filename(BITROCK_DIR));
define("BITROCK_EXE_LINUX",     "bitrock.sh");
define("BITROCK_EXE_WINNT",     "bitrock.bat");
define("BITROCK_EXE",           GET_OS_BOUND_NAME("BITROCK_EXE"));

define("BITROCK_DIST_DIR",      cleanup_local_filename(BITROCK_DIR."output/"));

define("BITROCK_DIST_PREFIX",  "PhET-".PHET_VERSION."-");
define("BITROCK_DIST_POSTFIX", "-installer.");

define("BITROCK_DIST_WINNT",  cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DIST_PREFIX."windows".BITROCK_DIST_POSTFIX."exe"));
define("BITROCK_DIST_LINUX",  cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DIST_PREFIX."linux".BITROCK_DIST_POSTFIX."bin"));
define("BITROCK_DIST_DARWIN", cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DIST_PREFIX."osx".BITROCK_DIST_POSTFIX."app"));

$g_bitrock_dists = array("windows" => BITROCK_DIST_WINNT, "linux" => BITROCK_DIST_LINUX, "osx" => BITROCK_DIST_DARWIN);

define("BITROCK_PRE_ARGS",    " build ");

?>
