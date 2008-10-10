package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.preferences.IManualUpdateChecker;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.dialogs.NoUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateErrorDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateInstructionsDialog.ManualUpdateInstructionsDialog;

public class DefaultManualCheckForUpdates implements IManualUpdateChecker {
    private String sim;
    private PhetVersion currentVersion;
    private String humanReadableSimName;
    private Frame frame;
    private String projectName;

    public DefaultManualCheckForUpdates( Frame frame, String projectName, String sim, PhetVersion currentVersion, String humanReadableSimName ) {
        this.frame = frame;
        this.projectName = projectName;
        this.sim = sim;
        this.currentVersion = currentVersion;
        this.humanReadableSimName = humanReadableSimName;
    }

    public void checkForUpdates() {
        UpdateManager updateManager = new UpdateManager( projectName, currentVersion );
        UpdateManager.Listener listener = new UpdateManager.Listener() {
            public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
            }

            public void newVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JDialog dialog = new ManualUpdateInstructionsDialog( frame, projectName, sim, humanReadableSimName, currentVersion.formatForTitleBar(), remoteVersion.formatForTitleBar() );
                dialog.setVisible( true );
            }

            public void exceptionInUpdateCheck( final IOException e ) {
                JDialog dialog = new UpdateErrorDialog( frame, e );
                dialog.setVisible( true );
            }

            public void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JDialog dialog = new NoUpdateDialog( frame, currentVersion.formatForTitleBar(), humanReadableSimName );
                dialog.setVisible( true );
            }
        };
        updateManager.addListener( listener );
        updateManager.checkForUpdates();
        updateManager.removeListener( listener );
    }
}
