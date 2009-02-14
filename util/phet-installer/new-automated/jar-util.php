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

    define('CONFIG_FILE', 'keystore-config.ini');

    //------------------------------------------------------------------------
    // Function to sign the specified JAR file.
    //------------------------------------------------------------------------
    function sign_jar( $jar_path_and_name ) {
        $config_params = parse_ini_file( CONFIG_FILE );
        $keystore = $config_params[ 'keystore' ];
        $storepass = $config_params[ 'storepass' ];
        $alias = $config_params[ 'alias' ];
        exec( "/usr/local/java/bin/jarsigner -keystore $keystore -storetype pkcs12 -storepass $storepass $jar_path_and_name $alias" );
    }

    //------------------------------------------------------------------------
    // Function to each of the JAR files in the provided list.
    //------------------------------------------------------------------------
    function sign_multiple_jars( $list_of_jar_files ) {
        flushing_echo( "function sign_multiple_jars called" );
        foreach ( $list_of_jar_files as $jar_file ){
            sign_jar( $jar_file );
        }
    }

    //------------------------------------------------------------------------
    // Function to create a list of all JAR files located below the specified
    // directory.
    //------------------------------------------------------------------------
    function find_jar_files( $initial_directory ) {
        flushing_echo( "function find_jar_files called, directory = ".$initial_directory );
        $file_list = file_list_in_directory( $initial_directory, "*.jar" );
        return $file_list;
    }

    //------------------------------------------------------------------------
    // Test harness - change this as needed when adding and verifying new
    // functions.
    //------------------------------------------------------------------------
    function test_functions(){
        $jar_files = find_jar_files( "./temp/website/phet.colorado.edu/sims" );
        sign_multiple_jars( $jar_files );
    }

    //------------------------------------------------------------------------
    // Entry point when run directly.  This intended primarily for testing
    // of the functions within this file.
    //------------------------------------------------------------------------
    test_functions();
?>
