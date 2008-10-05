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
            e.printStackTrace();
            notifyExceptionInUpdateCheck( e );
        }

    }

    private void notifyExceptionInUpdateCheck( IOException e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).exceptionInUpdateCheck( e );
        }
    }

    private void notifyNoUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).noNewVersionAvailable( currentVersion, remoteVersion );
        }
    }

    private void notifyUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).newVersionAvailable( currentVersion, remoteVersion );
        }
    }

    private void notifyDiscoveredRemoteVersion( PhetVersion remoteVersion ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).discoveredRemoteVersion( remoteVersion );
        }
    }

    public static interface Listener {
        void discoveredRemoteVersion( PhetVersion remoteVersion );

        void newVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion );

        void exceptionInUpdateCheck( IOException e );

        void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}
