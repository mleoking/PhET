package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.util.NetworkUtils;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

public class SimUpdater {

    public void updateSim( String project, String sim, String locale ) {
        TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATE_NOW_PRESSED );
        //download the updater
        try {
            File f = File.createTempFile( "updater", ".jar" );
            NetworkUtils.download( "http://www.colorado.edu/physics/phet/dev/temp/updater.jar", f );
            println( "downloaded updater to: \n" + f.getAbsolutePath() );

            String javaPath = System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
            File location = PhetUtilities.getCodeSource();
            if ( !location.getName().toLowerCase().endsWith( ".jar" ) ) {
                println( "Not running from a jar" );
                location = File.createTempFile( "" + sim, ".jar" );
                println( "CHanged download location to: " + location );
            }
            String[] cmd = new String[]{javaPath, "-jar", f.getAbsolutePath(), project, sim, locale, location.getAbsolutePath()};//todo support for locales

            println( "Starting updater with command: \n" + Arrays.toString( cmd ) );
            //TODO: disable opening a webpage unless someone asks for this feature
//                    OpenWebPageToNewVersion.openWebPageToNewVersion( project, sim );
            try {
                Thread.sleep( 10000 );
            }
            catch( InterruptedException e1 ) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Process p = Runtime.getRuntime().exec( cmd );
            System.exit( 0 );

            //todo: updater should allow 5 seconds or so for this to exit
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
    }

    private void println( String message ) {
        DebugLogger.println( getClass().getName() + "> " + message );
    }

}
