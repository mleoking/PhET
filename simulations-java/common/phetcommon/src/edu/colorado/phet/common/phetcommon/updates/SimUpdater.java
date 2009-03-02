package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.jar.JarFile;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.dialogs.DownloadProgressDialog;
import edu.colorado.phet.common.phetcommon.dialogs.ErrorDialog;
import edu.colorado.phet.common.phetcommon.files.PhetInstallation;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateErrorDialog;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;
import edu.colorado.phet.common.phetcommon.util.DownloadThread;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.logging.USLogger;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Updates the simulations by running the PhET "updater", which downloads the new version
 * of the sim, replaces the running version, and restarts the new version.
 *
 * @author Sam Reid
 */
public class SimUpdater {
    
    // updater basename
    private static final String UPDATER_BASENAME = "phet-updater";
    
    private static final String UPDATER_JAR = UPDATER_BASENAME + ".jar";
    
    // where the updater lives on the PhET site
    private static final String UPDATER_ADDRESS = "http://phet.colorado.edu/phet-dist/phet-updater/" + UPDATER_JAR;
    
    // localized strings
    private static final String ERROR_WRITE_PERMISSIONS = PhetCommonResources.getString( "Common.updates.errorWritePermissions" );
    private static final String ERROR_MISSING_JAR = PhetCommonResources.getString( "Common.updates.errorMissingJar" );
    
    private final File tmpDir;
    
    public SimUpdater() {
        tmpDir = new File( System.getProperty( "java.io.tmpdir" ) );
    }
    
    /**
     * Updates the sim that this is called from.
     * The updater bootstrap and new sim JAR are downloaded.
     * Then the bootstrap handles replacing the running JAR with the new JAR.
     * 
     * @param simInfo
     * @param newVersion
     */
    public void updateSim( ISimInfo simInfo, PhetVersion newVersion ) {
        
        if ( !tmpDir.canWrite() ) {
            handleErrorWritePermissions( tmpDir );
        }
        else {
            try {
                File simJAR = getSimJAR( simInfo.getFlavor() );
                if ( simJAR == null ) {
                    handleErrorMissingJar( simJAR );
                }
                else if ( !simJAR.canWrite() ) {
                    handleErrorWritePermissions( simJAR );
                }
                else {
                    String jarURL = getJarURL(simInfo);
                    log( "requesting update via URL=" + jarURL );
                    File tempSimJAR = getTempSimJAR( simJAR );
                    File tempUpdaterJAR = getTempUpdaterJAR();
                    boolean success = downloadFiles( UPDATER_ADDRESS, tempUpdaterJAR, jarURL, tempSimJAR, simInfo.getName(), newVersion );
                    if ( success ) {
                        
                        // validate the downloaded JAR files
                        validateJAR( tempUpdaterJAR );
                        validateJAR( tempSimJAR );
                        
                        startUpdaterBootstrap( tempUpdaterJAR, tempSimJAR, simJAR );
                        System.exit( 0 ); //presumably, jar must exit before it can be overwritten
                    }
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
                showException( e );
            }
        }
    }

    /**
     * Determines which JAR URL should be used to update this simulation, 
     * depends on whether we're in a phet installation (which gives <project>_all.jar)
     * or an offline simulation (which gives <sim>_<locale>.jar).
     * @param simInfo
     * @return
     */
    private String getJarURL( ISimInfo simInfo ) {
        if ( DeploymentScenario.getInstance() == DeploymentScenario.PHET_INSTALLATION ) {
            return HTMLUtils.getProjectJarURL( simInfo.getProjectName() );
        }
        else {
            return HTMLUtils.getSimJarURL( simInfo.getProjectName(), simInfo.getFlavor(), "&", simInfo.getLocale() );
        }
    }

    /**
     * Displays an update exception in a dialog.
     * @param e
     */
    private void showException( Exception e ) {
        JDialog dialog = new UpdateErrorDialog( PhetApplication.getInstance().getPhetFrame(), e );
        dialog.setVisible( true );
    }
    
    /*
     * Downloads files and displays a progress bar.
     * The files downloaded are the updater bootstrap jar, and the sim's new jar.
     */
    private boolean downloadFiles( String updaterSrc, File updaterDst, String simSrc, File simDst, String simName, PhetVersion newVersion ) throws IOException {
        
        // download requests
        DownloadThread downloadThread = new DownloadThread();
        downloadThread.addRequest( PhetCommonResources.getString( "Common.updates.downloadingBootstrap" ), updaterSrc, updaterDst );
        downloadThread.addRequest( PhetCommonResources.getString( "Common.updates.downloadingSimJar" ), simSrc, simDst );
        
        // progress dialog
        String title = PhetCommonResources.getString( "Common.updates.progressDialogTitle" );
        Object[] args = { simName, newVersion.formatMajorMinor() };
        String message = MessageFormat.format( PhetCommonResources.getString( "Common.updates.progressDialogMessage" ), args );
        DownloadProgressDialog dialog = new DownloadProgressDialog( null, title, message, downloadThread );
        
        // start the download
        downloadThread.start();
        dialog.setVisible( true );
        
        return downloadThread.getSucceeded();
    }
    
    /*
     * Runs the updater bootstrap in a separate JVM.
     */
    private void startUpdaterBootstrap( File updaterBootstrap, File src, File dst ) throws IOException {
        String[] cmdArray = new String[] { PhetUtilities.getJavaPath(), "-jar", updaterBootstrap.getAbsolutePath(), src.getAbsolutePath(), dst.getAbsolutePath() };
        log( "Starting updater bootstrap with cmdArray=" + Arrays.asList( cmdArray ).toString() );
        Runtime.getRuntime().exec( cmdArray );
        // It would be nice to read output from the Process returned by exec.
        // However, the simulation JAR must exit so that it can be overwritten.
    }

    /*
     * Gets the running simulation's JAR file.
     */
    private File getSimJAR( String sim ) throws IOException {
        File location = null;
        if ( DeploymentScenario.getInstance() == DeploymentScenario.PHET_INSTALLATION ) {
            location = PhetInstallation.getInstance().getInstalledJarFile();
        }
        else {
            location = FileUtils.getCodeSource();
            if ( !FileUtils.hasSuffix( location, "jar" ) ) {
                // So that this works in IDEs, where we aren't running a JAR.
                // In general, we only support running JAR files.
                location = File.createTempFile( sim, ".jar" );
                log( "Not running from a JAR, you are likely running from an IDE, update will be installed at " + location );
            }
            log( "running sim JAR is " + location.getAbsolutePath() );
        }
        return location;
    }
    
    /*
     * Gets a temporary file for downloading the sim's jar.
     */
    private File getTempSimJAR( File simJAR ) throws IOException {
        String basename = FileUtils.getBasename( simJAR );
        File file = File.createTempFile( basename, ".jar" );
        log( "temporary JAR is " + file.getAbsolutePath() );
        return file;
    }

    /*
     * Gets a temporary file for downloading the updater bootstrap jar.
     * Tries to use updater.jar, so that we don't accumulate lots of JAR files in the temp directory.
     * If that's not possible, download to a uniquely named file.
     */
    private File getTempUpdaterJAR() throws IOException {
        File updaterJAR = new File( tmpDir.getAbsolutePath() + System.getProperty( "file.separator" ) + UPDATER_JAR );
        if ( updaterJAR.exists() && !updaterJAR.canWrite() ) {
            updaterJAR = File.createTempFile( UPDATER_BASENAME, ".jar" );
        }
        log( "Downloading updater to " + updaterJAR.getAbsolutePath() );
        return updaterJAR;
    }
    
    /*
     * Verifies that a downloaded file is actually a JAR file.
     * If an error was experienced on the server, the file will be a text file containing an error message.
     * The format of the error message is not well-defined or suitable for displaying to the user.
     */
    private void validateJAR( File file ) throws IOException {
        new JarFile( file ); // throws IOException if not a jar file
    }

    private void log( String message ) {
        USLogger.log( getClass().getName() + ": " + message );
    }
    
    private static void handleErrorWritePermissions( File file ) {
        Object[] args = { file.getAbsolutePath() };
        String message = MessageFormat.format( ERROR_WRITE_PERMISSIONS, args );
        displayError( null, message );
    }
    
    private static void handleErrorMissingJar( File file ) {
        Object[] args = { file.getAbsolutePath() };
        String message = MessageFormat.format( ERROR_MISSING_JAR, args );
        displayError( null, message );
    }
    
    private static void displayError( Frame parent, String message ) {
        JDialog d = new ErrorDialog( parent, message );
        d.setVisible( true ); 
    }
}
