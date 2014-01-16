/* Copyright 2009, University of Colorado */

package edu.colorado.phet.buildtools.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.jar.Pack200;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import org.apache.tools.ant.taskdefs.VerifyJar;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.JarsignerInfo;
import edu.colorado.phet.buildtools.MyAntTaskRunner;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

import static java.util.jar.Pack200.Packer;
import static java.util.jar.Pack200.Unpacker;

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
     */
    public PhetJarSigner( BuildLocalProperties buildProperties ) {
        this.buildProperties = buildProperties;
        antTaskRunner = new MyAntTaskRunner();
    }

    /**
     * Sign (and verify) the specified jar file.
     *
     * @param jdkHome only necessary if the commands like 'java', 'jar' and 'jarsigner' need an absolute path, like on spot.  Otherwise null.
     * @param jarFile - Full path to the jar file to be signed.
     * @return true if successful, false if problems are encountered.
     */
    public boolean signJar( String jdkHome, File jarFile ) {

        // Make sure that the specified JAR file can be located.
        if ( !jarFile.exists() ) {
            logger.severe( "Error: jar does not exist: " + jarFile.getAbsolutePath() );
            return false;
        }

        // Sign the JAR using the ant task
        logger.info( "Signing JAR " + jarFile.getAbsolutePath() + "..." );
        JarsignerInfo jarsignerInfo = buildProperties.getJarsignerInfo();

        String[] cmdArray = {
                jdkHome == null ? "jarsigner" : jdkHome + "/bin/jarsigner",
                "-J-Djsse.enableSNIExtension=false", // workaround for Java 7 jarsigner / TSA buggy interaction. see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7127374
                "-keystore", jarsignerInfo.getKeystore(),
                "-storetype",
                "pkcs12",
                "-storepass", jarsignerInfo.getPassword(),
                "-tsa",
                jarsignerInfo.getTsaUrl(), jarFile.getAbsolutePath(),
                jarsignerInfo.getAlias()
        };

        // store success and output information, in case something goes wrong
        boolean success = true;
        String output = "";
        String error = "";

        try {
            Process process = Runtime.getRuntime().exec( cmdArray );

            ProcessOutputReader outputReader = new ProcessOutputReader( process.getInputStream() );
            ProcessOutputReader errorReader = new ProcessOutputReader( process.getErrorStream() );
            outputReader.start();
            errorReader.start();

            try {
                process.waitFor();
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }

            output = outputReader.getOutput();
            error = errorReader.getOutput();

            // TODO: no buffered streams need to be closed in the ProcessOutputReader instances?

            if ( process.exitValue() != 0 ) {
                success = false;
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
            success = false;
        }

        if ( !success ) {
            System.out.println( "Error encountered!" );
            //Show how to construct the equivalent command line call for debugging purposes
            //because failure explanations during antTaskRunner.runTask are poorly explained
            //Keep this commented out except during debugging so that our private data doesn't inadvertently end up in a public log file somewhere
            //maybe not too much of a problem.
//            System.out.println( "command-line: = " + FunctionalUtils.mkString( Arrays.asList( cmdArray ), " " ) );
            System.out.println( "output:\n**********\n" + output + "**********" );
            System.out.println( "error:\n**********\n" + error + "**********" );
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
     * Use Pack200 compression to pack and sign the JAR. Given an input of example.jar, it will repack and sign
     * example.jar, and create a packed example.jar.pack.gz.
     *
     * @param jarFile The JAR file to pack
     * @return Success
     */
    public boolean createExtraJars( String jdkHome, File jarFile ) {
        try {
            String jarName = jarFile.getName();
            File baseDir = jarFile.getParentFile();

            File packedFile = new File( baseDir, jarName + ".pack.gz" );
            File temporaryFile = new File( baseDir, jarName + ".pack.temp" );

            Packer packer = Pack200.newPacker();
            Unpacker unpacker = Pack200.newUnpacker();

            setPack200Settings( packer.properties() );
            setPack200Settings( unpacker.properties() );

            System.out.println( "Repacking JAR: " + jarFile.getAbsolutePath() );

            // repack it using a temporary file. this will normalize the order of class files / etc.
            boolean packSuccess = packFile( packer, jarFile, temporaryFile );
            if ( packSuccess ) {
                unpackFile( unpacker, temporaryFile, jarFile );
            }
            else {
                System.out.println( "Skipping unpacking step due to packing failure" );
            }
            safeDelete( temporaryFile );

            // sign the repacked JAR
            boolean success = signJar( jdkHome, jarFile );
            if ( !success ) {
                return false;
            }

            System.out.println( "Packing signed JAR: " + jarFile.getAbsolutePath() );

            // make a packed copy
            if ( packSuccess ) {
                // if we fail here, something must be wrong because it essentially worked above!
                packAndCompressFile( packer, jarFile, packedFile );
            }
            else {
                System.out.println( "Skipping pack-and-compress step due to previous packing failure" );
            }

            /*---------------------------------------------------------------------------*
            * now add an all-permissions file
            *----------------------------------------------------------------------------*/

            System.out.println( "Creating the installer all-jar with all-permissions" );

            // where our installer result file will go
            File installerFile = new File( baseDir, jarName.substring( 0, jarName.length() - ".jar".length() ) + "_installer.jar" );

            String jarCommand = jdkHome == null ? "jar" : jdkHome + "/bin/jar";

            // a temporary directory where an extracted version of the intial JAR will go
            File extractionDir = new File( jarFile.getParent(), "jar-extraction" );
            extractionDir.mkdir();

            // extract the JAR into our temporary directory
            commandAndWait( jarCommand + " xf " + jarFile.getAbsolutePath(), extractionDir );

            // read the manifest from the original JAR
            JarFile installerJar = new JarFile( jarFile );
            Manifest manifest = installerJar.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            File manifestFile = new File( baseDir, "MANIFEST.MF" );
            installerJar.close(); // clean up input streams

            // create a temporary manifest file that will be poked in, with the changes made
            mainAttributes.putValue( "Permissions", "all-permissions" ); // overwrite with all-permissions
            FileOutputStream manifestOutputStream = new FileOutputStream( manifestFile );
            manifest.write( manifestOutputStream );
            manifestOutputStream.close();

            // build the JAR with the exploded contents, but with our new manifest file
            commandAndWait( jarCommand + " cfm " + installerFile.getName() + " MANIFEST.MF -C " + extractionDir.getName() + "/ .", baseDir );

            // clean up extras
            FileUtils.delete( extractionDir );
            safeDelete( manifestFile );

            // sign and verify our new all-permissions JAR
            signJar( jdkHome, installerFile );
            verifyJar( installerFile );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return false;
        }

        return true; // success
    }

    private static void commandAndWait( String cmd, File workingDir ) {
        logger.warning( "Running command: " + cmd );
        try {
            Process p = Runtime.getRuntime().exec( cmd, null, workingDir );
            p.waitFor();
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
        //TODO: redirect output to console
    }

    private void setPack200Settings( SortedMap<String, String> map ) {
        map.put( Packer.EFFORT, "7" );
        map.put( Packer.SEGMENT_LIMIT, "-1" );
        map.put( Packer.KEEP_FILE_ORDER, Packer.FALSE );
        //map.put( Packer.MODIFICATION_TIME, Packer.LATEST );
        //map.put( Packer.DEFLATE_HINT, Packer.FALSE );
        map.put( Packer.UNKNOWN_ATTRIBUTE, Packer.ERROR );
    }

    /**
     * Pack the JAR into a non-gzipped pack200 file, and return success
     *
     * @param packer  Our packer
     * @param inFile  JAR file
     * @param outFile Pack200 file (not gzipped)
     * @return Success
     * @throws IOException
     */
    private boolean packFile( Packer packer, File inFile, File outFile ) throws IOException {
        boolean success = true;
        safeDelete( outFile );
        FileOutputStream out = new FileOutputStream( outFile );
        try {
            JarFile readFile = new JarFile( inFile, false ); // do not verify the jar file here, since it may have changed
            try {
                packer.pack( readFile, out );
            }
            finally {
                readFile.close();
            }
        }
        catch( IOException e ) {
            // probably failed due to a corrupt scala attribute
            // beforehand: com.sun.java.util.jar.pack.Attribute$FormatException: class.ScalaSig: unknown in scala/LowPriorityImplicits
            success = false;
            System.out.println( "Received the following error during pack200 compression." );
            e.printStackTrace();
            if ( outFile.exists() ) {
                outFile.delete(); // attempt to delete!
            }
        }
        finally {
            out.close();
        }
        return success;
    }

    private void packAndCompressFile( Packer packer, File inFile, File outFile ) throws IOException {
        safeDelete( outFile );
        OutputStream out = new GZIPOutputStream( new FileOutputStream( outFile ) ) {{
            def.setLevel( Deflater.BEST_COMPRESSION );
        }};
        try {
            JarFile readFile = new JarFile( inFile );
            try {
                packer.pack( readFile, out );
            }
            finally {
                readFile.close();
            }
        }
        finally {
            out.close();
        }
    }

    private void unpackFile( Unpacker unpacker, File inFile, File outFile ) throws IOException {
        safeDelete( outFile );
        JarOutputStream jarOutputStream = new JarOutputStream( new FileOutputStream( outFile ) );
        try {
            unpacker.unpack( inFile, jarOutputStream );
        }
        finally {
            jarOutputStream.close();
        }
    }

    private void safeDelete( File file ) {
        if ( file.exists() ) {
            boolean success = file.delete();
            if ( !success ) {
                // probably indicates permission problem, so throw an exception instead
                throw new RuntimeException( "could not delete " + file.getAbsolutePath() );
            }
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
            System.err.println( "usage: PhetJarSigner path-to-trunk jar-to-be-signed" );
            System.exit( -1 );
        }

        File trunkDir = new File( args[0] );
        File jarToBeSigned = new File( args[1] );

        BuildLocalProperties buildProperties = BuildLocalProperties.initRelativeToTrunk( trunkDir );
        PhetJarSigner signer = new PhetJarSigner( buildProperties );
        boolean result = signer.createExtraJars( "C:\\Program Files\\Java\\jdk1.7.0_51", jarToBeSigned );

        System.out.println( "Done, result = " + result + "." );
    }
}
