<?php

    //============================================================================
    // Contains a simple PHP script for unlocking the lock file that is used to
    // prevent simultaneous builds.  This should only be used if something went
    // wrong during the build and the file was inappropriately left locked.
    //============================================================================


    //--------------------------------------------------------------------------
    // Inclusions of other PHP files.
    //--------------------------------------------------------------------------

    require_once("config.php");
    require_once("global.php");
    require_once("file-util.php");
    require_once("web-util.php");
    require_once("jar-util.php");
    require_once("jnlp.php");
    require_once("installer-util.php");
    require_once("xml-util.php");
    require_once("ripper-util.php");

    //--------------------------------------------------------------------------
    // Entry point for this PHP file.
    //--------------------------------------------------------------------------
    flushing_echo("Forcing an unlock of the lock file.");
    file_unlock( LOCK_FILE_STEM_NAME );

?>
