package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class UpdateManager {
    private String project;
    private PhetVersion currentVersion;
    private IVersionChecker versionChecker = new DefaultVersionChecker();
    private ArrayList listeners = new ArrayList();
    private PhetVersion remoteVersion;

    public UpdateManager( String project, PhetVersion currentVersion ) {
        this.project = project;
        this.currentVersion = currentVersion;
    }

    public void checkForUpdates() {
        try {
//            throw new IOException( "test error" );
            remoteVersion = versionChecker.getVersion( project );
            notifyDiscoveredRemoteVersion( remoteVersion );

            if ( remoteVersion.isGreaterThan( currentVersion ) ) {
                notifyUpdateAvailable( currentVersion, remoteVersion );
            }
            else {
                notifyNoUpdateAvailable( currentVersion, remoteVersion );
            }
        }
        catch( IOException e ) {
            System.err.println( "ERROR: UpdateManager.checkForUpdates: " + e.toString() );
//            e.printStackTrace();
            notifyExceptionInUpdateCheck( e );
        }

    }

    private void notifyExceptionInUpdateCheck( IOException e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).exceptionInUpdateCheck( e );
        }
    }

    private void notifyNoUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).noUpdateAvailable( currentVersion, remoteVersion );
        }
    }

    private void notifyUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).updateAvailable( currentVersion, remoteVersion );
        }
    }

    private void notifyDiscoveredRemoteVersion( PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (UpdateListener) listeners.get( i ) ).discoveredRemoteVersion( remoteVersion );
        }
    }

    public static interface UpdateListener {
        
        public void discoveredRemoteVersion( PhetVersion remoteVersion );

        public void updateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion );

        public void exceptionInUpdateCheck( IOException e );

        public void noUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion );
    }
    
    public static class UpdateAdapter implements UpdateListener {
        
        public void discoveredRemoteVersion( PhetVersion remoteVersion ) {}

        public void updateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {}

        public void exceptionInUpdateCheck( IOException e ) {}

        public void noUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {}
    }

    public void addListener( UpdateListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( UpdateListener listener ) {
        listeners.remove( listener );
    }
}
