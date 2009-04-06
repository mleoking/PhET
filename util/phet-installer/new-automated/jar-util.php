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
    define('JARSIGNER', '/usr/local/java/bin/jarsigner'); // Full path to jarsigner util.

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
    // Function to sign each of the JAR files in the provided list.
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
    // Function to locate the primary JAR files for the various projects that
    // exist below the provided directory path.  The "primary JAR file" is the
    // one that contains all the supported languages and is accessed via the
    // JNLP file when the user launches the simulation directly from the web
    // site.
    //
    // Note that this file is located by matching the naming convention that
    // came into use in March 2009, which is <project>_all.jar.  If this
    // convention ever changes, this function will need to be modified.
    //------------------------------------------------------------------------
    function find_primary_project_jar_files( $initial_directory ) {
        flushing_echo( "function find_primary_project_jar_files called, directory = ".$initial_directory );
        $file_list = file_list_in_directory( $initial_directory, "*_all.jar" );
        return $file_list;
    }

    //------------------------------------------------------------------------
    // Function to determine whether a given JAR file is signed.  Returns
    // true if so and false if not.
    //------------------------------------------------------------------------
    function is_jar_signed( $path_to_jar_file ) {
        flushing_echo( "function is_jar_signed called, file = ".$path_to_jar_file );
        $verify_command = JARSIGNER." -verify ".$path_to_jar_file;
        $result = system( $verify_command, $retval );

        if ( ( $retval == 0 ) && ( stristr( $result, "verified" ) ) ){
            echo "JAR is signed.\n";
            return true;
        }
        else {
            echo "JAR is unsigned.\n";
            return false;
        }
    }

    //------------------------------------------------------------------------
    // Test harness - change this as needed when adding and verifying new
    // functions.
    //------------------------------------------------------------------------
    function test_harness(){
        $primary_project_jar_files = find_primary_project_jar_files( "./temp/website/phet.colorado.edu/sims" );
        foreach ( $primary_project_jar_files as $primary_project_jar_file ) {
            echo "File: ".$primary_project_jar_file."\n";
            is_jar_signed( $primary_project_jar_file );
        }
    }

    //------------------------------------------------------------------------
    // Entry point when run directly.  This intended primarily for testing
    // of the functions within this file.
    //------------------------------------------------------------------------
    test_harness();
?>
