package edu.colorado.phet.common.phetcommon.preferences;

import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.UpdateManager;

public class DefaultManualCheckForUpdates implements IManuallyCheckForUpdates {
    private PhetVersion currentVersion;
    private String humanReadableSimName;
    private String projectName;

    public DefaultManualCheckForUpdates( String projectName, PhetVersion currentVersion, String humanReadableSimName ) {
        this.projectName = projectName;
        this.currentVersion = currentVersion;
        this.humanReadableSimName = humanReadableSimName;
    }

    public void checkForUpdates() {
        UpdateManager updateManager = new UpdateManager( projectName, currentVersion );
        UpdateManager.Listener listener = new UpdateManager.Listener() {
            public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
            }

            public void newVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JOptionPane.showMessageDialog( null, "Your current version of " + humanReadableSimName + " is " + currentVersion.formatForTitleBar() + ".  A newer version (" + remoteVersion.formatForTitleBar() + ") is available.\n" +
                                                     "A web browser is being opened to the PhET website, where you can get the new version." );
            }

            public void exceptionInUpdateCheck( IOException e ) {
            }

            public void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JOptionPane.showMessageDialog( null, "You have the current version of " + humanReadableSimName + " " + currentVersion.formatForTitleBar() );
            }
        };
        updateManager.addListener( listener );
        updateManager.checkForUpdates();
        updateManager.removeListener( listener );
    }
}
