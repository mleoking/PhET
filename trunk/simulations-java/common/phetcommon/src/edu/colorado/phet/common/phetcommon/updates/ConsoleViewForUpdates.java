package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class ConsoleViewForUpdates implements UpdateManager.Listener {
    public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
        System.out.println( "ConsoleViewForUpdates.discoveredRemoteVersion: " + remoteVersion );
    }

    public void newVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        System.out.println( "ConsoleViewForUpdates.newVersionAvailable: currentVersion=" + currentVersion + ", remoteVersion=" + remoteVersion );
    }

    public void exceptionInUpdateCheck( IOException e ) {
        System.out.println( "ConsoleViewForUpdates.exceptionInUpdateCheck: " + e );
        e.printStackTrace();
    }

    public void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
        System.out.println( "ConsoleViewForUpdates.noNewVersionAvailable: currentVersion=" + currentVersion + ", remoteVersion=" + remoteVersion );
    }
}
