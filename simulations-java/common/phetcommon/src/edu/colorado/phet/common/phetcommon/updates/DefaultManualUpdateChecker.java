package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.dialogs.ManualSimUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.NoUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateErrorDialog;

/**
 * Handles manual requests for update checks.
 * <p>
 * If an update is found, an dialog is displayed that allows the user to perform the update.
 * If no update is found, a dialog notifies the user.
 */
public class DefaultManualUpdateChecker implements IManualUpdateChecker {
    
    private String sim;
    private PhetVersion currentVersion;
    private String humanReadableSimName;
    private Locale locale;
    private Frame frame;
    private String projectName;
    
    public DefaultManualUpdateChecker( Frame frame, ISimInfo simInfo ) {
        this( frame, simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion(), simInfo.getName(), simInfo.getLocale() );
    }

    public DefaultManualUpdateChecker( Frame frame, String projectName, String sim, PhetVersion currentVersion, String humanReadableSimName, Locale locale ) {
        this.frame = frame;
        this.projectName = projectName;
        this.sim = sim;
        this.currentVersion = currentVersion;
        this.humanReadableSimName = humanReadableSimName;
        this.locale = locale;
    }

    public void checkForUpdates() {
        UpdateNotifier updateNotifier = new UpdateNotifier( projectName, sim, currentVersion );
        UpdateNotifier.UpdateListener listener = new UpdateNotifier.UpdateAdapter() {

            public void updateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JDialog dialog = new ManualSimUpdateDialog( frame, projectName, sim, humanReadableSimName, currentVersion, remoteVersion, locale);
                dialog.setVisible( true );
            }

            public void noUpdateAvailable( PhetVersion currentVersion ) {
                JDialog dialog = new NoUpdateDialog( frame, currentVersion.formatForTitleBar(), humanReadableSimName );
                dialog.setVisible( true );
            }

            public void exceptionInUpdateCheck( final IOException e ) {
                JDialog dialog = new UpdateErrorDialog( frame, e );
                dialog.setVisible( true );
            }
        };
        updateNotifier.addListener( listener );
        updateNotifier.checkForUpdates();
        updateNotifier.removeListener( listener );
    }
}
