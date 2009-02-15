package edu.colorado.phet.phetupdater;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.swing.*;

/**
 * Replaces and restarts specified JAR, see SimUpdater.
 */
public class UpdaterBootstrap {

    // do not enabled debug output for public releases, the log file will grow indefinitely!
    private static final boolean DEBUG_OUTPUT_ENABLED = true;

    // retry for copy from src to dst
    private static final int NUMBER_OF_RETRIES = 100;
    private static final int TIME_BETWEEN_RETRIES = 100; // ms

    //TODO: localize
    private static final String COPY_FAILED_MESSAGE =
            "<html>" +
            "Failed to update your simulation.<br>" +
            "<br>" +
            "Could not copy {0} <br>" +
            "to {1}.<br>" +
            "Check your file permissions and try again.<br>" +
            "<br>" +
            "If this problem persists, contact phethelp@colorado.edu.<br>" +
            "</html>";

    //TODO: localize
    private static final String LAUNCH_ERROR_MESSAGE =
            "<html>" +
            "Your simulation was updated, but it could not be restarted.<br>" +
            "Restart the simulation by running <br>" +
            "{0}.<br>" +
            "<br>" +
            "If this problem persists, contact phethelp@colorado.edu.<br>" +
            "</html>";

    private final File src;
    private final File dst;

    public UpdaterBootstrap( File src, File dst ) {
        this.src = src;
        this.dst = dst;
    }

    private void replaceAndLaunch() throws IOException {

        try {
            copy();
        }
        catch( IOException e ) {
            showErrorDialog( getCopyErrorMessage() );
            throw e;
        }

        try {
            launch();
        }
        catch( IOException e ) {
            showErrorDialog( getLaunchErrorMessage() );
            throw e;
        }

        // if we get to here, the sim has been updated and restarted,
        // and there's no need to bother the user with problems encountered during cleanup.
        cleanup();
    }

    /*
     * Tries to copy src to dst until it succeeds or we reach some max number of attempts.
     * This is necessary because the original JAR (dst) may not yet be released by
     * the sim that started this bootstrapper.
     */
    private void copy() throws IOException {
        println( "copying " + src.getAbsolutePath() + " to " + dst.getAbsolutePath() );
        IOException failureResult = null;
        for ( int i = 0; i < NUMBER_OF_RETRIES; i++ ) {
            try {
                UpdaterUtils.copyTo( src, dst );
                break;
            }
            catch( IOException e ) {
                System.err.println( e.getMessage() );
                failureResult = e;
                if ( i % 10 == 0 ) {
                    println( "copy failed at iteration: " + i );
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
        println( "copy finished" );
    }

    /*
     * Launches the simulation (as specified by a JAR file) with the specified language code.
     */
    private void launch() throws IOException {
        String[] cmdArray = new String[]{getJavaPath(), "-jar", dst.getAbsolutePath()};
        println( "launching sim with cmdArray=" + Arrays.asList( cmdArray ) );
        Process p = Runtime.getRuntime().exec( cmdArray );
        // It's not worth reading output from the above process because we really can't do anything with it.
        println( "launch finished" );
    }

    private void cleanup() throws IOException {

        println( "cleaning up" );

        // delete the downloaded sim JAR file
        src.deleteOnExit();

        // delete the bootstrap JAR
        File thisJar = UpdaterUtils.getCodeSource();
        if ( UpdaterUtils.hasSuffix( thisJar, "jar" ) ) { // When running in IDEs (Eclipse, IDEA,...) we aren't running a JAR.
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

    private String getCopyErrorMessage() {
        Object[] args = {src.getAbsoluteFile(), dst.getAbsoluteFile()};
        return MessageFormat.format( COPY_FAILED_MESSAGE, args );
    }

    private String getLaunchErrorMessage() {
        Object[] args = {src.getAbsoluteFile()};
        return MessageFormat.format( LAUNCH_ERROR_MESSAGE, args );
    }

    private static void showErrorDialog( String message ) {
        JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.ERROR_MESSAGE );
    }

    /*
     * This main is invoked by the Updater in UpdateButton or equivalent.
     * Arguments must be as in this example:
     * java -jar updater.jar C:/temp/alpha-decay0123.jar C:/user/phet/alpha-decay.jar
     */
    public static void main( String[] args ) {

        if ( Arrays.asList( args ).contains( "-version" ) ) {
            System.out.println( "PhET Updater: " + UpdaterBootstrap.class.getName() + ", version: " + getVersion() );
        }
        else {

            String src = args[0];
            String dst = args[1];
            println( "Started updater version: " + getVersion() );
            println( "starting updater, src=" + src + ", target=" + dst );
            try {
                new UpdaterBootstrap( new File( src ), new File( dst ) ).replaceAndLaunch();
            }
            catch( IOException e ) {
                e.printStackTrace();
                println( UpdaterUtils.stackTraceToString( e ) );
                System.exit( 1 ); // indicate abnormal exit
            }
        }
    }

    private static String getVersion() {
        Properties properties = new Properties();
        try {
            properties.load( Thread.currentThread().getContextClassLoader().getResourceAsStream( "phet-updater/phet-updater.properties" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        //return a plain text version; don't bring in phetcommon because it will increas the jar size from 7kb to 200kb
        //See #972

        return properties.getProperty( "version.major" ) + "." + properties.getProperty( "version.minor" ) + "." +
               properties.getProperty( "version.dev" ) + " (" + properties.getProperty( "version.revision" ) + ") " +
               new SimpleDateFormat( "MMM d, yyyy" ).format( new Date( Integer.parseInt( properties.getProperty( "version.timestamp" ) ) * 1000L ) );
    }
}
