package edu.colorado.phet.common.phetcommon.updates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.NetworkUtils;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.logging.DebugLogger;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Updates the simulations by running the PhET "updater", which downloads the new version
 * of the sim, replaces the running version, and restarts the new version.
 *
 * @author Sam Reid
 */
public class SimUpdater {
    
    // do not enabled debug output for public releases, the log file will grow indefinitely!
    private static final boolean DEBUG_OUTPUT_ENABLED = true;

    // updater basename
    private static final String UPDATER_BASENAME = "phet-updater";
    
    private static final String UPDATER_JAR = UPDATER_BASENAME + ".jar";
    
    // where the updater lives on the PhET site
    private static final String UPDATER_ADDRESS = "http://phet.colorado.edu/phet-dist/updater/" + UPDATER_JAR;
    
    private final File tmpDir;
    
    public SimUpdater() {
        tmpDir = new File( System.getProperty( "java.io.tmpdir" ) );
    }
    
    /**
     * Updates the sim that this is called from.
     * The updater bootstrap and new sim JAR are downloaded.
     * Then the bootstrap handles replacing the running JAR with the new JAR.
     * 
     * @param project
     * @param sim
     * @param languageCode
     */
    public void updateSim( String project, String sim, String languageCode ) {
        
        if ( !tmpDir.canWrite() ) {
            handleErrorWritePermissions( tmpDir );
        }
        else {
            try {
                File simJAR = getSimJAR( sim );
                if ( !simJAR.canWrite() ) {
                    handleErrorWritePermissions( simJAR );
                }
                else {
                    File tempJAR = getTempJAR( simJAR );
                    File updaterJAR = downloadUpdaterJAR();
                    downloadSimJAR( project, sim, languageCode, tempJAR );
                    startUpdaterBootstrap( updaterJAR, tempJAR, simJAR );
                    System.exit( 0 ); //presumably, jar must exit before it can be overwritten
                }
            }
            catch ( IOException e1 ) {
                //TODO: alert the user
                e1.printStackTrace();
            }
        }
    }

    /*
     * Downloads the JAR for the specified simulation to the specified location.
     */
    private void downloadSimJAR( String project, String flavor, String languageCode, File targetLocation ) throws FileNotFoundException {
        String jarURL = HTMLUtils.getSimJarURL( project, flavor, "&", languageCode );
        println( "Downloading sim JAR from " + jarURL );
        NetworkUtils.download( jarURL, targetLocation );
    }

    /*
     * Runs the updater bootstrap in a separate JVM.
     */
    private void startUpdaterBootstrap( File updaterBootstrap, File src, File dst ) throws IOException {
        String[] cmdArray = new String[] { PhetUtilities.getJavaPath(), "-jar", updaterBootstrap.getAbsolutePath(), src.getAbsolutePath(), dst.getAbsolutePath() };//TODO: support for language code
        println( "Starting updater bootstrap with cmdArray=" + Arrays.asList( cmdArray ).toString() );
        Process p = Runtime.getRuntime().exec( cmdArray );
        //TODO: read output from process in case helpful debug information is there in case of problem
    }

    /*
     * Gets the running simulation's JAR file.
     */
    private File getSimJAR( String sim ) throws IOException {
        File location = FileUtils.getCodeSource();
        if ( !FileUtils.hasSuffix( location, "jar" ) ) {
            // So that this works in IDEs, where we aren't running a JAR.
            // In general, we only support running JAR files.
            location = File.createTempFile( sim, ".jar" );
            println( "Not running from a JAR, you are likely running from an IDE, update will be installed at " + location );
        }
        println( "running sim JAR is " + location.getAbsolutePath() );
        return location;
    }
    
    /*
     * Gets a temporary name for the sim JAR file to be downloaded.
     */
    private File getTempJAR( File simJAR ) throws IOException {
        String basename = FileUtils.getBasename( simJAR );
        File location = File.createTempFile( basename, ".jar" );
        println( "temporary JAR is " + location.getAbsolutePath() );
        return location;
    }

    /*
     * Tries to download to updater.jar, so that we don't accumulate lots of JAR files in the temp directory.
     * If that's not possible, download to a uniquely named file.
     */
    private File downloadUpdaterJAR() throws IOException {
        File updaterJAR = new File( tmpDir.getAbsolutePath() + System.getProperty( "file.separator" ) + UPDATER_JAR );
        if ( updaterJAR.exists() && !updaterJAR.canWrite() ) {
            updaterJAR = File.createTempFile( UPDATER_BASENAME, ".jar" );
        }
        println( "Downloading updater to " + updaterJAR.getAbsolutePath() );
        NetworkUtils.download( UPDATER_ADDRESS, updaterJAR );
        //TODO: make something delete the updater.jar when everything is done
        return updaterJAR;
    }

    private void println( String message ) {
        if ( DEBUG_OUTPUT_ENABLED ) {
            DebugLogger.println( getClass().getName() + "> " + message );
        }
    }
    
    private static void handleErrorWritePermissions( File file ) {
        Object[] args = { file.getAbsolutePath() };
        String message = MessageFormat.format( PhetCommonResources.getString( "Common.updates.errorWritePermissions" ), args );
        JOptionPane.showMessageDialog( null, message );
    }
}
