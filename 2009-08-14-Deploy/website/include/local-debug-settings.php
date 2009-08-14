<?php

// This file is for setting of whatever debug options you'd like.
//
// It is NOT recommended to make this file include any other file.
//
// Everything is in a function, so it is easy to turn everything
// on or off.  Everything should be in the function and set whatever
// is necessary.  The function call is at the bottom of this file.
//

function setup_local_debug_settings() {
    //
    // Defines section
    //
    // Set defines that you want to take preceedance over
    // standard defines
    if (!defined("PORTAL_ROOT")) define("PORTAL_ROOT", "../".SITE_ROOT);

    //
    // Globals section
    //
    // Turn on and off things that you may check in code

    // You should leave this alone, turn off the global debug
    // switch only if you know what you are doing
    $GLOBALS["DEBUG"] = true;

    // True if you want caching even if you are running locally
    $GLOBALS["DEBUG_FORCE_LOCAL_CACHE"] = false;

    //
    // Settings section
    //
    // All settings to PHP directly should go here
    error_reporting(E_ERROR | E_ALL | E_STRICT);
    assert_options(ASSERT_ACTIVE, 1);
    assert_options(ASSERT_BAIL, 1);


    //
    // Custom section
    //
    // All custom stuff should go here

    // Override Google Analytics code?  Options are:
    //    "GA_YES"    use GA JavaScript from Google
    //    "GA_NO"     don't include GA JavaScript from anywhere
    //    "GA_LOCAL"  use local version of GA JavaScript
    //    unset will default to GA_YES
    $GLOBALS["OVERRIDE_GOOGLE_ANALYTICS"] = "GA_NO";

    // True will force installer behavior, how website will look when used in the installer
    $GLOBALS["DEBUG_FORCE_INSTALLER_BEHAVIOR"] = false;

    // True will turn OFF meta refresh (will have to explicitly click the link to take you to the next page)
    $GLOBALS["DEBUG_DISABLE_META_REFRESH"] = false;
}

// Uncomment the below to set all the debug options
setup_local_debug_settings();

?>