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

    define('JAR', '/usr/bin/jar');             // Full path to jar util.
    define('JARSIGNER', '/usr/bin/jarsigner'); // Full path to jarsigner util.

    //------------------------------------------------------------------------
    // Function to create a list of all JAR files located below the specified
    // directory.
    //------------------------------------------------------------------------
    function find_jar_files( $initial_directory ) {
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
        $file_list = file_list_in_directory( $initial_directory, "*_all.jar" );
        return $file_list;
    }

    //------------------------------------------------------------------------
    // Function to determine whether a given JAR file is signed.  Returns
    // true if so and false if not.
    //------------------------------------------------------------------------
    function is_jar_signed( $path_to_jar_file ) {
        $verify_command = JARSIGNER." -verify ".$path_to_jar_file;
        $result = system( $verify_command, $retval );

        if ( ( $retval == 0 ) && ( stristr( $result, "verified" ) ) ){
            return true;
        }
        else {
            return false;
        }
    }

    //------------------------------------------------------------------------
    // Function to sign the specified JAR file.
    //------------------------------------------------------------------------
    function sign_jar( $jar_path_and_name ) {
        $config_params = parse_ini_file( CONFIG_FILE );
        $keystore = $config_params[ 'keystore' ];
        $storepass = $config_params[ 'storepass' ];
        $alias = $config_params[ 'alias' ];
        $command =  JARSIGNER." -keystore $keystore -storetype pkcs12 -storepass $storepass $jar_path_and_name $alias";
        print $command."\n";
        exec( $command );
    }

    //------------------------------------------------------------------------
    // Function to extract the given file from the given jar.
    //------------------------------------------------------------------------
    function extract_file_from_jar( $path_to_jar_file, $path_to_file_in_jar, $dest_dir='.' ) {
        // Change to the temporary directory.
        $original_dir = getcwd();
        chdir($dest_dir);

        // Create and exectue the command.
        $extract_command = JAR.' xvf '.$path_to_jar_file.' '.$path_to_file_in_jar;
        print "Command = ".$extract_command."\n";
        $result = system( $extract_command, $retval );
        print "Result = ".$result."\n";

        // Change back to the orignal directory.
        chdir($original_dir);

        // Return true if it appears to have worked.
        if ( $retval == 0  ){
            return true;
        }
        else {
            return false;
        }
    }

    //------------------------------------------------------------------------
    // Function to add the given file from the given jar.  This will replace
    // and existing version of the file.
    //------------------------------------------------------------------------
    function add_file_to_jar( $path_to_jar_file, $path_to_file_to_be_added ) {

        // Create and exectue the command.
        $add_command = JAR.' uf '.$path_to_jar_file.' '.$path_to_file_to_be_added;
        print "Command = ".$add_command."\n";
        $result = system( $add_command, $retval );
        print "retval = ".$retval."\n";
        print "Result = ".$result."\n";

        // Return true if it appears to have worked.
        if ( $retval == 0  ){
            return true;
        }
        else {
            return false;
        }
    }

    //------------------------------------------------------------------------
    // Test harness - change this as needed when adding and verifying new
    // functions.
    //------------------------------------------------------------------------
    function test_harness(){
        $primary_project_jar_files = find_primary_project_jar_files( "./temp/website/phet-server.colorado.edu_8080/sims" );
        foreach ( $primary_project_jar_files as $primary_project_jar_file ) {
            echo "File: ".$primary_project_jar_file."\n";
            is_jar_signed( $primary_project_jar_file );
        }
    }

    //------------------------------------------------------------------------
    // Entry point when run directly.  This intended primarily for testing
    // of the functions within this file.
    //------------------------------------------------------------------------

    //test_harness();
?>
