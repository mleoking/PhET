package edu.colorado.phet.updater;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Copies and launches specified JAR, see SimUpdater.
 */
public class UpdaterBootstrap {

    // do not enabled debug output for public releases, the log file will grow indefinitely!
    private static final boolean DEBUG_OUTPUT_ENABLED = true;
    
    // retry for copy from src to dst
    private static final int NUMBER_OF_RETRIES = 100;
    private static final int TIME_BETWEEN_RETRIES = 100; // ms
    
    private final File src;
    private final File dst;

    public UpdaterBootstrap( File src, File dst ) {
        this.src = src;
        this.dst = dst;
    }

    private void replaceAndLaunch() throws IOException {
        copy();
        launch();
        cleanup();
        println( "finished launch" );
    }

    /*
     * Tries to copy src to dst until it succeeds or we reach some max number of attempts.
     * This is necessary because the original JAR (dst) may not yet be released by
     * the sim that started this bootstrapper.
     */
    private void copy() throws IOException {
        IOException failureResult = null;
        for ( int i = 0; i < NUMBER_OF_RETRIES; i++ ) {
            try {
                FileUtils.copyTo( src, dst );
                break;
            }
            catch( IOException e ) {
                System.err.println( e.getMessage() );
                failureResult = e;
                if ( i % 10 == 0 ) {
                    println( "copy failed at iteration: " + i );//message every second
                }
                try {
                    Thread.sleep( TIME_BETWEEN_RETRIES );
                }
                catch( InterruptedException ex ) {
                    ex.printStackTrace();
                }
            }
        }
        if ( failureResult != null ) {
            throw failureResult;
        }
    }

    /*
     * Launches the simulation (as specified by a JAR file) with the specified language code.
     */
    private void launch() throws IOException {
        //TODO: add support for language code when we have added support for non-English offline JARs
        String[] cmdArray = new String[]{getJavaPath(), "-jar", dst.getAbsolutePath()};
        println( "restarting sim with cmdArray=" + Arrays.asList( cmdArray ) );
        Process p = Runtime.getRuntime().exec( cmdArray );
        //TODO: display output from this process in case any errors occur
    }
    
    private void cleanup() throws IOException {
        
        // delete the downloaded sim JAR file
        src.deleteOnExit();
        
        // delete the bootstrap JAR
        File thisJar = FileUtils.getCodeSource();
        if ( FileUtils.hasSuffix( thisJar, "jar" ) ) { // When running in IDEs (Eclipse, IDEA,...) we aren't running a JAR.
            thisJar.deleteOnExit(); // this doesn't work (but causes no problems) on Windows, can't delete an open file.
        }
    }

    public static String getJavaPath() {
        return System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
    }

    private static void println( String ms ) {
        if ( DEBUG_OUTPUT_ENABLED ) {
            DebugLogger.println( UpdaterBootstrap.class.getName() + "> " + ms );
        }
    }

    /*
     * This main is invoked by the Updater in UpdateButton or equivalent.
     * Arguments must be as in this example:
     * java -jar updater.jar C:/temp/alpha-decay0123.jar C:/user/phet/alpha-decay.jar
     */
    public static void main( String[] args ) {

        String src = args[0];
        String dst = args[1];
        println( "starting updater, src=" + src + ", target=" + dst );
        try {
            new UpdaterBootstrap( new File( src ), new File( dst ) ).replaceAndLaunch();
        }
        catch( IOException e ) {
            e.printStackTrace();
            println( e.getMessage() );
            //TODO: open an error dialog here?
            System.exit( 1 ); // indicate abnormal exit
        }
    }
}
