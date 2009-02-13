<?php

    //========================================================================
    // This file contains functions for performing manipulations on Java
    // Archive (JAR) files.
    //
    // NOTE: This file relies on the presence of a configuration file in the
    // same directory where it is executed in order to perform some of its
    // functions.
    //========================================================================

    require_once("global.php");
    require_once("file-util.php");

    //------------------------------------------------------------------------
    // Function to sign the specified JAR file.
    //------------------------------------------------------------------------
    function sign_jar( $jar_path_and_name ) {
        flushing_echo( "function sign_jar called, name = ".$jar_path_and_name );
    }

    //------------------------------------------------------------------------
    // Function to each of the JAR files in the provided list.
    //------------------------------------------------------------------------
    function sign_multiple_jars( $list_of_jar_files ) {
        flushing_echo( "function sign_multiple_jars called" );
    }

    //------------------------------------------------------------------------
    // Function to create a list of all JAR files located below the specified
    // directory.
    //------------------------------------------------------------------------
    function find_jar_files( $initial_directory ) {
        flushing_echo( "function find_jar_files called, directory = ".$initial_directory );
        $file_list = file_list_in_directory( $initial_directory, "*.jar" );
        foreach ($file_list as $file){
            flushing_echo( $file );
        }
        return $file_list;
    }

    //------------------------------------------------------------------------
    // Test harness - change this as needed when adding and verifying new
    // functions.
    //------------------------------------------------------------------------
    function test_functions(){
        find_jar_files( "./temp/website/phet.colorado.edu/sims" );
    }

    //------------------------------------------------------------------------
    // Entry point when run directly.  This intended primarily for testing
    // of the functions within this file.
    //------------------------------------------------------------------------
    test_functions();
?>
