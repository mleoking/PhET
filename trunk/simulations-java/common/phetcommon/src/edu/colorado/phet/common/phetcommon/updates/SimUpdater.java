package edu.colorado.phet.common.phetcommon.updates;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.util.NetworkUtils;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.logging.DebugLogger;

public class SimUpdater {

    //todo: move to tigercat
    private String UPDATER_ADDRESS = "http://www.colorado.edu/physics/phet/dev/temp/updater.jar";

    public void updateSim( String project, String sim, String locale ) {
        TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATE_NOW_PRESSED );
        try {
            File updaterJAR = downloadUpdaterJAR();
            File simJAR = getSimJAR( sim );

            //TODO: disable opening a webpage unless someone asks for this feature
            //OpenWebPageToNewVersion.openWebPageToNewVersion( project, sim );

            startUpdaterProcess( project, sim, locale, updaterJAR, simJAR );
            System.exit( 0 );//presumably, jar must exit before it can be overwritten
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
    }

    private void startUpdaterProcess( String project, String sim, String locale, File updaterJAR, File location ) throws IOException {
        String[] cmd = new String[]{getJavaPath(), "-jar", updaterJAR.getAbsolutePath(), project, sim, locale, location.getAbsolutePath()};//todo support for locales
        println( "Starting updater with command: \n" + Arrays.toString( cmd ) );
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
