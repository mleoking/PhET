/* Copyright 2009, University of Colorado */

package edu.colorado.phet.buildtools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.tools.ant.taskdefs.SignJar;

/**
 * This class can be used to sign JAR files, and is intended to be used within
 * the PhET build environment.
 * 
 * @author John Blanco
 */
public class PhetJarSigner {

	String pathToJarsigner;
	String pathToConfigFile;
	String pathToJarFile;
	
	
	

	public PhetJarSigner( String pathToJarsigner, String pathToConfigFile, String pathToJarFile ) {
		this.pathToJarsigner = pathToJarsigner;
		this.pathToConfigFile = pathToConfigFile;
		this.pathToJarFile = pathToJarFile;
	}

	public boolean signJar(){

		System.out.println("signJar called.");
        String s = null;

        try {
            
            Process p = Runtime.getRuntime().exec( pathToJarsigner );
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            
            System.out.println("Standard output from signing:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            
            // read any errors from the attempted command

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
        
        return true;
	}

	public static void main(String[] args) {
		if (args.length != 3){
			System.err.println("JAR Signer Test: Not enough arguments, aborting.");
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
