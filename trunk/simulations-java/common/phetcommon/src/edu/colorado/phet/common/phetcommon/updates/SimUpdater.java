package edu.colorado.phet.common.phetcommon.updates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
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

    private String UPDATER_ADDRESS = "http://phet.colorado.edu/phet-dist/updater/updater.jar";

    public void updateSim( String project, String sim, String locale ) {
        TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATE_NOW_PRESSED );
        try {
            File updaterJAR = downloadUpdaterJAR();
            File simJAR = getSimJAR( sim );
            File tempJARLocation = File.createTempFile( simJAR.getName(), "_.jar" );

            //TODO: disable opening a webpage unless someone asks for this feature
            //OpenWebPageToNewVersion.openWebPageToNewVersion( project, sim );

            download( project, sim, locale, tempJARLocation );

            startBootstrap( updaterJAR, tempJARLocation, simJAR );
            System.exit( 0 );//presumably, jar must exit before it can be overwritten
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
    }

    /*
    * Downloads the JAR for the specified simulation to the specified location.
    */
    private void download( String project, String flavor, String locale, File targetLocation ) throws FileNotFoundException {
//        String localeSuffix = locale.equals( "en" ) ? "" : "_" + locale;
//        println( "Downloading " + "http://phet.colorado.edu/sims/" + project + "/" + flavor + localeSuffix + ".jar" + " to " + targetLocation.getAbsolutePath() );
//        DownloadUtils.download( "http://phet.colorado.edu/sims/" + project + "/" + flavor + localeSuffix + ".jar", targetLocation );
//
        String jarURL = HTMLUtils.getSimJarURL( project, flavor, "&", locale );
        println( "Downloading " + jarURL );
        NetworkUtils.download( jarURL, targetLocation );
    }

    private void startBootstrap( File bootstrapUpdater, File src, File dst ) throws IOException {
        String[] cmd = new String[]{getJavaPath(), "-jar", bootstrapUpdater.getAbsolutePath(), src.getAbsolutePath(), dst.getAbsolutePath()};//todo support for locales
        println( "Starting updater with command: \n" + Arrays.asList( cmd ).toString() );
        Process p = Runtime.getRuntime().exec( cmd );
        //todo: read output from process in case helpful debug information is there in case of problem
    }

    private File getSimJAR( String sim ) throws IOException {
        File location = PhetUtilities.getCodeSource();
        if ( !location.getName().toLowerCase().endsWith( ".jar" ) ) {
            location = File.createTempFile( "" + sim, ".jar" );
            println( "Not running from a jar, downloading to: " + location );
        }
        return location;
    }

    private File downloadUpdaterJAR() throws IOException {
        File updaterJAR = File.createTempFile( "updater", ".jar" );
        NetworkUtils.download( UPDATER_ADDRESS, updaterJAR );
        println( "downloaded updater to: \n" + updaterJAR.getAbsolutePath() );
        return updaterJAR;
    }

    private String getJavaPath() {
        return System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
    }

    private void println( String message ) {
        DebugLogger.println( getClass().getName() + "> " + message );
    }

}
