/* Copyright 2009, University of Colorado */

package edu.colorado.phet.buildtools.util;

import java.io.File;
import java.util.logging.Logger;

import org.apache.tools.ant.taskdefs.SignJar;
import org.apache.tools.ant.taskdefs.VerifyJar;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.JarsignerInfo;
import edu.colorado.phet.buildtools.MyAntTaskRunner;

/**
 * This class can be used to sign JAR files, and is intended to be used within
 * the PhET build environment.
 *
 * @author John Blanco
 */
public class PhetJarSigner {

    private final BuildLocalProperties buildProperties;
    private final AntTaskRunner antTaskRunner;

    private static final Logger logger = Logger.getLogger( PhetJarSigner.class.getName() );

    /**
     * Constructor
     *
     * @param configPath
     */
    public PhetJarSigner( BuildLocalProperties buildProperties ) {
        this.buildProperties = buildProperties;
        antTaskRunner = new MyAntTaskRunner();
    }

    /**
     * Sign (and verify) the specified jar file.
     *
     * @param jarFile - Full path to the jar file to be signed.
     * @return true if successful, false if problems are encountered.
     */
    public boolean signJar( File jarFile ) {

        // Make sure that the specified JAR file can be located.
        if ( !jarFile.exists() ) {
            logger.severe( "Error: jar does not exist: " + jarFile.getAbsolutePath() );
            return false;
        }

        // Sign the JAR using the ant task
        logger.info( "Signing JAR " + jarFile.getAbsolutePath() + "..." );
        JarsignerInfo jarsignerInfo = buildProperties.getJarsignerInfo();
        SignJar signer = new SignJar();
        signer.setKeystore( jarsignerInfo.getKeystore() );
        signer.setStoretype( "pkcs12" );
        signer.setStorepass( jarsignerInfo.getPassword() );
        signer.setJar( jarFile );
        signer.setAlias( jarsignerInfo.getAlias() );
        signer.setTsaurl( jarsignerInfo.getTsaUrl() );
        try {
            antTaskRunner.runTask( signer );
        }
        catch (Exception e) {
            //Show how to construct the equivalent command line call for debugging purposes
            //because failure explanations during antTaskRunner.runTask are poorly explained
            //Keep this commented out except during debugging so that our private data doesn't inadvertently end up in a public log file somewhere
            //maybe not too much of a problem.
//            String commandLine = "jarsigner -keystore "+jarsignerInfo.getKeystore()+" -storetype "+"pkcs12"+" -storepass "+jarsignerInfo.getPassword()+" -tsa "+jarsignerInfo.getTsaUrl()+" "+jarFile.getAbsolutePath()+" "+jarsignerInfo.getAlias();
//            System.err.println( "Exception caught while attempting to sign jar, command line is:\n "+commandLine);

            e.printStackTrace();
            return false;
        }

        // If we made it to this point, signing succeeded.
        logger.info( "Signing succeeded." );

        // Verify the JAR.
        return verifyJar( jarFile );
    }

    /**
     * Verifies a signed jar file.
     */
    private boolean verifyJar( File jarFile ) {

        boolean success = true;

        // Make sure that the specified JAR file can be located.
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
        catch( Exception e ) {
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
            System.err.println( "usage: PhetJarSigner path-to-trunk jar-to-be-signed" );
            System.exit( -1 );
        }

        File trunkDir = new File( args[0] );
        File jarToBeSigned = new File( args[1] );

        BuildLocalProperties buildProperties = BuildLocalProperties.initRelativeToTrunk( trunkDir );
        PhetJarSigner signer = new PhetJarSigner( buildProperties );
        boolean result = signer.signJar( jarToBeSigned );

        System.out.println( "Done, result = " + result + "." );
    }
}
