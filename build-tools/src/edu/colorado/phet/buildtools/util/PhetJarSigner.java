/* Copyright 2009, University of Colorado */

package edu.colorado.phet.buildtools.util;

import java.io.*;
import java.util.Properties;

/**
 * This class can be used to sign JAR files, and is intended to be used within
 * the PhET build environment.
 * 
 * @author John Blanco
 */
public class PhetJarSigner {

    // these keys must be in a properties file
    public static final String KEY_JARSIGNER_PATH = "jarsigner.path";
    public static final String KEY_KEYSTORE = "jarsigner.keystore";
    public static final String KEY_PASSWORD = "jarsigner.password";
    public static final String KEY_ALIAS = "jarsigner.alias";

    private final File configFile;

    /**
     * Constructor
     * 
     * @param configPath
     */
    public PhetJarSigner( String configPath ) {
        configFile = new File( configPath );
    }

    /**
     * Sign the specified jar file.
     * 
     * @param jarPath - Full path to the jar file to be signed.
     * @return true if successful, false if problems are encountered.
     */
    public boolean signJar( String jarPath ) {

        // Make sure that the specified JAR file can be located.
        File jarFile = new File( jarPath );
        if ( !jarFile.exists() ) {
            System.err.println( "Error: jar does not exist: " + jarFile.getAbsolutePath() );
            return false;
        }
        
        // Verify that the specified properties file exists and can be loaded.
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
        String jarsignerPath = jarsignerProperties.getProperty( KEY_JARSIGNER_PATH );
        String keystoreFileName = jarsignerProperties.getProperty( KEY_KEYSTORE );
        String password = jarsignerProperties.getProperty( KEY_PASSWORD );
        String alias = jarsignerProperties.getProperty( KEY_ALIAS );
        if ( ( jarsignerPath == null ) || ( keystoreFileName == null ) || ( password == null ) || ( alias == null ) ) {
            System.err.println( "Error: Missing one or more properties needed for signing, aborting." );
            return false;
        }
        
        // sign and verify the jar
        boolean success = signJar( jarsignerPath, keystoreFileName, password, alias, jarPath );
        if ( success ) {
            success = verifyJar( jarsignerPath, jarPath );
        }
        return success;
    }

    /*
     * Signs a jar file.
     */
    private boolean signJar( String jarsignerPath, String keystoreFileName, String password, String alias, String jarPath ) {

        boolean success = false;
        
        // sign the jar
        String signingCommand = jarsignerPath + " -keystore " + keystoreFileName + " -storetype pkcs12 " + " -storepass " + password + ' ' + jarPath + ' ' + alias;
        System.out.println( "Signing jar:" + signingCommand );
        try {
            // Execute the signing command.
            Process p = Runtime.getRuntime().exec( signingCommand );
            echoProcessOutput( p, "signing" );
            success = true;
        }
        catch ( IOException e ) {
            System.out.println( "Exception while attempting to sign JAR:" );
            e.printStackTrace();
        }
        
        return success;
    }
    
    /*
     * Verifies a signed jar file.
     */
    private boolean verifyJar( String jarsignerPath, String jarPath ) {
        
        boolean success = false;

        // verify the signed jar
        String verifyCommand = jarsignerPath + " -verify " + " -certs " + jarPath;
        System.out.println( "Verifying signed jar:" + verifyCommand );
        try {
            Process p = Runtime.getRuntime().exec( verifyCommand );
            echoProcessOutput( p, "verifying" );
            success = true;
        }
        catch ( IOException e ) {
            System.out.println( "Exception while attempting to verify signed JAR:" );
            e.printStackTrace();
        }

        return success;
    }
    
    /*
     * Reads from a Process' stdout and stderr streams.
     */
    private void echoProcessOutput( Process p, String processName ) throws IOException {
        echoProcessOutput( p.getInputStream(), processName, "stdout" );
        echoProcessOutput( p.getErrorStream(), processName, "stderr" );
    }
    
    /*
     * Reads from an input stream, assumed to be associated with a Process.
     */
    private void echoProcessOutput( InputStream inputStream, String processName, String streamName ) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
        System.out.println( "Reading " + streamName + " from " + processName + ":" );
        String s = null;
        while ( ( s = reader.readLine() ) != null ) {
            System.out.println( s );
        }
        if ( s != null ) {
            System.out.print( "\n" );
        }
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

        PhetJarSigner signer = new PhetJarSigner( args[0] );
        boolean result = signer.signJar( args[1] );

        System.out.println( "Done, result = " + result + "." );
    }
}
