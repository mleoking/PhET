package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

/**
 * Notifies clients about sim updates.
 */
public class SimUpdateNotifier {
    
    private final String project;
    private String simulation;
    private final PhetVersion currentVersion;
    private final SimVersionChecker versionChecker = new SimVersionChecker();
    private final ArrayList listeners = new ArrayList();

    public SimUpdateNotifier( String project, String simulation,PhetVersion currentVersion ) {
        this.project = project;
        this.simulation = simulation;
        this.currentVersion = currentVersion;
    }

    public void checkForUpdates() {
        try {
            PhetVersion latestVersion = versionChecker.getVersion( project,simulation );
            notifyDiscoveredRemoteVersion( latestVersion );

            if ( latestVersion.isGreaterThan( currentVersion ) ) {
                notifyUpdateAvailable( currentVersion, latestVersion );
            }
            else {
                notifyNoUpdateAvailable( currentVersion );
            }
        }
        catch( IOException e ) {
            System.err.println( "ERROR: UpdateNotifier.checkForUpdates: " + e.toString() );
//            e.printStackTrace();
            notifyExceptionInUpdateCheck( e );
        }

    }

    private void notifyExceptionInUpdateCheck( IOException e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).exceptionInUpdateCheck( e );
        }
    }

    private void notifyNoUpdateAvailable( PhetVersion currentVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).noUpdateAvailable( currentVersion );
        }
    }

    private void notifyUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).updateAvailable( currentVersion, remoteVersion );
        }
    }

    private void notifyDiscoveredRemoteVersion( PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).discoveredLatestVersion( remoteVersion );
        }
    }

    public static interface UpdateListener {
        
        /**
         * Tells us what the latest version is.
         * @param latestVersion
         */
        public void discoveredLatestVersion( PhetVersion latestVersion );

        /**
         * Indicates that an update is available, the latest version is newer than the current version.
         * @param currentVersion
         * @param latestVersion
         */
        public void updateAvailable( PhetVersion currentVersion, PhetVersion latestVersion );

        /**
         * Indicates that no update is available, the latest version is the same as the current version.
         * @param currentVersion
         */
        public void noUpdateAvailable( PhetVersion currentVersion );
        
        /**
         * Indicates an exception occurred during the update check.
         * @param e
         */
        public void exceptionInUpdateCheck( IOException e );
    }
    
    public static class UpdateAdapter implements UpdateListener {
        
        public void discoveredLatestVersion( PhetVersion latestVersion ) {}

        public void updateAvailable( PhetVersion currentVersion, PhetVersion latestVersion ) {}

        public void exceptionInUpdateCheck( IOException e ) {}

        public void noUpdateAvailable( PhetVersion currentVersion ) {}
    }

    public void addListener( UpdateListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( UpdateListener listener ) {
        listeners.remove( listener );
    }
}
