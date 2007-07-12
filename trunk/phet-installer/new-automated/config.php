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

	define("ROOT_DIR", file_cleanup_local_filename(dirname(__FILE__)."/"));
	define("TEMP_DIR", file_cleanup_local_filename(ROOT_DIR."temp/"));

	function GET_OS_BOUND_REL_PATH($constantPrefix) {
	    return file_cleanup_local_filename(ROOT_DIR."${constantPrefix}/".PHP_OS."/");
	}

	function GET_OS_BOUND_NAME($constantPrefix) {
	    return constant("${constantPrefix}_".PHP_OS);
	}


	// *****************************************************************************
	// PhET Website Configuration
	define("PHET_VERSION",              		"1.0");
	define("PHET_ROOT_URL",             		"http://phet.colorado.edu/");
	define("PHET_WEBSITE_URL",          		PHET_ROOT_URL."web-pages/");
	define("PHET_RIPPER_FILTER",        		'"+*phet.colorado.edu/web-pages*" "+*phet.colorado.edu/simulations*"');
	define("PHET_WEBSITE_ROOT_PARTIAL_PATTERN", '[^"]+colorado\.edu');
	define("PHET_WEBSITE_ROOT_PATTERN", 		'/'.PHET_WEBSITE_ROOT_PARTIAL_PATTERN.'/');
	define("PHET_AUTORUN_ICON",         		file_cleanup_local_filename(ROOT_DIR."Installer-Resources/Install-Path/Phet-logo-48x48.ico"));

	// *****************************************************************************
	// Website Ripper Configuration

	// The name of the HTTrack directory:
	define("RIPPER_DIR_NAME",    "HTTrack");

	define("RIPPED_WEBSITE_ROOT", file_cleanup_local_filename(TEMP_DIR."website/"));
	define("RIPPED_WEBSITE_TOP",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT."phet.colorado.edu/"));

	// The ripper executable itself:

	// The ripper executables per OS:
	define("RIPPER_EXE_LINUX",   "httrack");
	define("RIPPER_EXE_WINNT",   "httrack.exe");
	define("RIPPER_EXE_Darwin",  "httrack");

	define("RIPPER_DIR",  GET_OS_BOUND_REL_PATH(RIPPER_DIR_NAME));
	define("RIPPER_EXE",  RIPPER_DIR.GET_OS_BOUND_NAME("RIPPER_EXE")); 

	// Command-line args of the ripper:
	define("RIPPER_ARGS", '"'.PHET_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER.' -j ');

	// *****************************************************************************
	// BitRock Configuration

	define("BITROCK_DIR",           file_cleanup_local_filename(ROOT_DIR."BitRock/"));
	define("BITROCK_BUILDFILE_DIR", file_cleanup_local_filename(BITROCK_DIR."projects/"));
	define("BITROCK_BUILDFILE",     file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."phet-installer-buildfile.xml"));
	define("BITROCK_EXE_DIR",       file_cleanup_local_filename(BITROCK_DIR));
	define("BITROCK_EXE_Linux",     "bitrock.sh");
	define("BITROCK_EXE_WINNT",     "bitrock.bat");
	define("BITROCK_EXE_Darwin",    "bitrock.sh");	
	define("BITROCK_EXE",           GET_OS_BOUND_NAME("BITROCK_EXE"));

	define("BITROCK_DIST_DIR",      file_cleanup_local_filename(BITROCK_DIR."output/"));

	define("BITROCK_DIST_PREFIX",  "PhET-".PHET_VERSION."-");
	define("BITROCK_DIST_POSTFIX", "-installer.");

	define("BITROCK_DIST_WINNT",  file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DIST_PREFIX."windows".BITROCK_DIST_POSTFIX."exe"));
	define("BITROCK_DIST_Linux",  file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DIST_PREFIX."linux".BITROCK_DIST_POSTFIX."bin"));
	define("BITROCK_DIST_Darwin", file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DIST_PREFIX."osx".BITROCK_DIST_POSTFIX."app"));

	$g_bitrock_dists = array("windows" => BITROCK_DIST_WINNT, "linux" => BITROCK_DIST_Linux, "osx" => BITROCK_DIST_Darwin);

	define("BITROCK_PRE_ARGS",    " build ");

?>
