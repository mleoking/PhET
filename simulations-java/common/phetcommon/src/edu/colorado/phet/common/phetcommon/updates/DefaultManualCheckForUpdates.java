package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.preferences.IManualUpdateChecker;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.dialogs.ManualUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.NoUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateErrorDialog;

public class DefaultManualCheckForUpdates implements IManualUpdateChecker {
    private String sim;
    private PhetVersion currentVersion;
    private String humanReadableSimName;
    private String locale;
    private Frame frame;
    private String projectName;

    public DefaultManualCheckForUpdates( Frame frame, String projectName, String sim, PhetVersion currentVersion, String humanReadableSimName,String locale ) {
        this.frame = frame;
        this.projectName = projectName;
        this.sim = sim;
        this.currentVersion = currentVersion;
        this.humanReadableSimName = humanReadableSimName;
        this.locale = locale;
    }

    public void checkForUpdates() {
        UpdateManager updateManager = new UpdateManager( projectName, currentVersion );
        UpdateManager.Listener listener = new UpdateManager.Listener() {
            public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
            }

            public void newVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JDialog dialog = new ManualUpdateDialog( frame, projectName, sim, humanReadableSimName, currentVersion, remoteVersion ,locale);
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
