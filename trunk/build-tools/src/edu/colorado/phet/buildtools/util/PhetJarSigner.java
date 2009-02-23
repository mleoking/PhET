/* Copyright 2009, University of Colorado */

package edu.colorado.phet.buildtools.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * This class can be used to sign JAR files, and is intended to be used within
 * the PhET build environment.
 * 
 * @author John Blanco
 */
public class PhetJarSigner {
	
    public static final String KEYSTORE_PROPERTY_NAME = "signing-config.keystore";
    public static final String KEYSTORE_PASSWORD_PROPERTY_NAME = "signing-config.keystore-password";
    public static final String CERTIFICATE_ALIAS_PROPERTY_NAME = "signing-config.alias";

	String pathToJarSigner;
	String pathToConfigFile;
	String pathToJarFile;
	Properties signingConfigProperties;
	
	public PhetJarSigner( String pathToJarsigner, String pathToConfigFile, String pathToJarFile ) {
		this.pathToJarSigner = pathToJarsigner;
		this.pathToConfigFile = pathToConfigFile;
		this.pathToJarFile = pathToJarFile;
		
	}

	public boolean signJar(){

		// Verify that the specified properties file exists and can be loaded.
        
		File signingConfigFile = new File( pathToConfigFile );
		signingConfigProperties = new Properties();
        if ( signingConfigFile.exists() ) {
            try {
            	signingConfigProperties.load( new FileInputStream( signingConfigFile ) );
            }
            catch( IOException e ) {
            	System.err.println("Error: Unable to load signing configuration file, aborting.");
                e.printStackTrace();
                return false;
            }
        }
        else{
        	System.err.println("Error: Signing config file does not exist: " + pathToConfigFile);
        }
        
        // Make sure the needed properties are present.
        String keystoreFileName = signingConfigProperties.getProperty( KEYSTORE_PROPERTY_NAME );
        String keystorePassword = signingConfigProperties.getProperty( KEYSTORE_PASSWORD_PROPERTY_NAME );
        String alias = signingConfigProperties.getProperty( CERTIFICATE_ALIAS_PROPERTY_NAME );
        
        if ((keystoreFileName == null) ||
        	(keystorePassword == null) ||
        	(alias == null)){
        	
        	System.err.println("Error: Missing one or more properties needed for signing, aborting.");
        	return false;
        }
        
        // Make sure that the specified JAR file can be located.
		File jarFile = new File( pathToJarFile );
		if ( !jarFile.exists() ){
			System.err.println("Error: Could not locate specified JAR file, aborting.");
			return false;
		}
        
        // Create and execute the signing command.

        String signingCommand = pathToJarSigner + " -keystore " + keystoreFileName + " -storetype pkcs12 " +
 	    	" -storepass " + keystorePassword + " \"" + pathToJarFile + "\" " + alias;
        
        System.out.println("About to execute signing command:");
        System.out.println(signingCommand);
        
        try {
        	// Execute the signing command.
            Process p = Runtime.getRuntime().exec( signingCommand );
            
            // Obtain the standard and error output.
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Output the results echoed by the execution of the command.
            String s = null;
            System.out.println("Standard output from signing (if any):\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.out.println("Standard error from signing (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        catch (IOException e) {
            System.out.println("Exception while attempting to sign JAR:");
            e.printStackTrace();
            return false;
        }
        
        // Create and execute the verification command.
        
        String verifyCommand = pathToJarSigner + " -verify " + " -certs " + pathToJarFile;
    
        System.out.println("About to execute the verification command:");
        System.out.println(verifyCommand);
    
	    try {
	    	// Execute the signing command.
	        Process p = Runtime.getRuntime().exec( verifyCommand );
	        
	        // Obtain the standard and error output.
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	
	        // Output the results echoed by the execution of the command.
	        String s = null;
	        System.out.println("Standard output from verify:\n");
	        while ((s = stdInput.readLine()) != null) {
	            System.out.println(s);
	        }
	
	        System.out.println("Standard error from verify (if any):\n");
	        while ((s = stdError.readLine()) != null) {
	            System.out.println(s);
	        }
	    }
	    catch (IOException e) {
	        System.out.println("Exception while attempting to sign JAR:");
	        e.printStackTrace();
	        return false;
	    }
        
        
        return true;
	}

	public static void main(String[] args) {
		if (args.length != 3){
			System.err.println("PhET JAR Signer: Not enough arguments, aborting.");
			System.exit( -1 );
		}
		
		System.out.print("Executing Jar Signer Test, args = ");
		for ( int i = 0; i < 3; i++ ){
			System.out.print(args[i] + " ");
		}
		System.out.println();
		
		PhetJarSigner signer = new PhetJarSigner( args[0], args[1], args[2] );
		
		signer.signJar();
	}
}
