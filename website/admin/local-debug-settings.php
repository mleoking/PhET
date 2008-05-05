<?php

// This file is for setting of whatever debug options you'd like.
//
// Needless to say, this file should NOT be checked into SVN.
//
// It is NOT recommended to make this file include any other file.
//
// Everything is in a function, so it is easy to turn everything
// on or off.  Everything should be in the function and set whatever
// is necessary.  The function call is at the bottom of this file.
//

function setup_local_debug_settings() {
    //
    // Globals section
    //
    // Turn on and off things that you may check in code

    // You should leave this alone, turn off the global debug
    // switch only if you know what you are doing
    $GLOBALS["DEBUG"] = true;

    // If you want caching even if you are running locally
    $GLOBALS["DEBUG_FORCE_LOCAL_CACHE"] = true;

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

}

// Uncomment the below to set all the debug options
//setup_local_debug_settings();

?>