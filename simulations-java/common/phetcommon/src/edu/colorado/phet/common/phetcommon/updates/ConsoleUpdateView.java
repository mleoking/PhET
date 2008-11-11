package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class ConsoleUpdateView implements UpdateManager.UpdateListener {
    public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
        System.out.println( "ConsoleViewForUpdates.discoveredRemoteVersion: " + remoteVersion );
    }

    public void updateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        System.out.println( "ConsoleViewForUpdates.updateAvailable: currentVersion=" + currentVersion + ", remoteVersion=" + remoteVersion );
    }

    public void exceptionInUpdateCheck( IOException e ) {
        System.out.println( "ConsoleViewForUpdates.exceptionInUpdateCheck: " + e );
        e.printStackTrace();
    }

    public void noUpdateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        System.out.println( "ConsoleViewForUpdates.noUpdateAvailable: currentVersion=" + currentVersion + ", remoteVersion=" + remoteVersion );
    }
}
