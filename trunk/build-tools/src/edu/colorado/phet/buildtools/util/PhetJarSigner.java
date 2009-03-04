/* Copyright 2009, University of Colorado */

package edu.colorado.phet.buildtools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.SignJar;
import org.apache.tools.ant.taskdefs.VerifyJar;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.MyAntTaskRunner;

/**
 * This class can be used to sign JAR files, and is intended to be used within
 * the PhET build environment.
 * 
 * @author John Blanco
 */
public class PhetJarSigner {

    // these keys must be in a properties file
    public static final String KEY_KEYSTORE = "jarsigner.keystore";
    public static final String KEY_PASSWORD = "jarsigner.password";
    public static final String KEY_ALIAS = "jarsigner.alias";

    private final File configFile;
    
    private AntTaskRunner antTaskRunner;

    /**
     * Constructor
     * 
     * @param configPath
     */
    public PhetJarSigner( String configPath, AntTaskRunner antTaskRunner ) {
        configFile = new File( configPath );
        this.antTaskRunner = antTaskRunner;
    }

    /**
     * Sign the specified jar file.
     * 
     * @param jarPath - Full path to the jar file to be signed.
     * @return true if successful, false if problems are encountered.
     */
    public boolean signJar( String jarPath ) {

        // Verify that the properties file exists and can be loaded.
        Properties jarsignerProperties = new Properties();
        if ( configFile.exists() ) {
            try {
                jarsignerProperties.load( new FileInputStream( configFile ) );
            }
            catch ( IOException e ) {
                System.err.println( "Error: Unable to load signing configuration file." );
                e.printStackTrace();
                return false;
            }
        }
        else {
            System.err.println( "Error: Signing config file does not exist: " + configFile.getAbsolutePath() );
            return false;
        }

        // Make sure the needed properties are present.
        String keystoreFileName = jarsignerProperties.getProperty( KEY_KEYSTORE );
        String password = jarsignerProperties.getProperty( KEY_PASSWORD );
        String alias = jarsignerProperties.getProperty( KEY_ALIAS );
        if ( ( keystoreFileName == null ) || ( password == null ) || ( alias == null ) ) {
            System.err.println( "Error: Missing one or more properties needed for signing, aborting." );
            return false;
        }
        
        // Sign the JAR
        return signJar( keystoreFileName, password, alias, jarPath);
    }

    /*
     * Signs a jar file.
     */
    private boolean signJar( String keystoreFileName, String password, String alias, String jarPath ) {

        // Make sure that the specified JAR file can be located.
        File jarFile = new File( jarPath );
        if ( !jarFile.exists() ) {
            System.err.println( "Error: jar does not exist: " + jarFile.getAbsolutePath() );
            return false;
        }

        System.out.println("Signing JAR...");
        
        // Sign the JAR using the ant task
        SignJar signer = new SignJar();
        signer.setAlias( alias );
        signer.setKeystore( keystoreFileName );
        signer.setStoretype( "pkcs12" );
        signer.setStorepass( password );
        signer.setJar( jarFile );

        try{
            antTaskRunner.runTask( signer );
        }
        catch (Exception e){
        	System.err.println("Exception caught while attempting to sign jar.");
        	e.printStackTrace();
        	return false;
        }
        
        // If we made it to this point, signing succeeded.
        System.out.println( "Signing succeeded." );
        return true;
    }
    
    /**
     * Verifies a signed jar file.
     */
    public boolean verifyJar( String jarPath ) {
        
    	boolean success = true;
    	
        // Make sure that the specified JAR file can be located.
        File jarFile = new File( jarPath );
        if ( !jarFile.exists() ) {
            System.err.println( "Error: jar does not exist: " + jarFile.getAbsolutePath() );
            return false;
        }
        
        // verify the signed jar
        System.out.println( "Verifying signed JAR..." );
        try {
        	VerifyJar verifier = new VerifyJar();
        	verifier.setJar( jarFile );
        	antTaskRunner.runTask( verifier );
        }
        catch ( Exception e ) {
            System.out.println( "Exception while attempting to verify signed JAR:" );
            e.printStackTrace();
            return false;
        }

        System.out.println( "Verification succeeded." );
        return success;
    }
    
    /**
     * Main routine, which is used for testing and also for signing JAR files
     * from the command line.
     * 
     * @param args config-file jar-to-be-signed
     */
    public static void main( String[] args ) {

        if ( args.length != 2 ) {
            System.err.println( "usage: PhetJarSigner config-file jar-to-be-signed" );
            System.exit( -1 );
        }
        
        String jarFilePath = args[0];

        PhetJarSigner signer = new PhetJarSigner( jarFilePath, new MyAntTaskRunner() );
        boolean result = signer.signJar( args[1] );

        System.out.println( "Done, result = " + result + "." );
    }
}
