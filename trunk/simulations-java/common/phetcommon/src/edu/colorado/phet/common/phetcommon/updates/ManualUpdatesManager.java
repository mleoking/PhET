package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.dialogs.ManualSimUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.NoSimUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateErrorDialog;

/**
 * Handles manual requests for update checks.
 * <p>
 * If an update is found, an dialog is displayed that allows the user to perform the update.
 * If no update is found, a dialog notifies the user.
 */
public class ManualUpdatesManager {
    
    private static ManualUpdatesManager instance;
    
    private String projectName;
    private String sim;
    private PhetVersion currentVersion;
    private String humanReadableSimName;
    private Locale locale;
    private Frame frame;
    
    private ManualUpdatesManager( PhetApplication app ) {
        this.frame = app.getPhetFrame();
        ISimInfo simInfo = app.getSimInfo();
        this.projectName = simInfo.getProjectName();
        this.sim = simInfo.getFlavor();
        this.currentVersion = simInfo.getVersion();
        this.humanReadableSimName = simInfo.getName();
        this.locale = simInfo.getLocale();
    }
    
    public static ManualUpdatesManager initInstance( PhetApplication app ) {
        if ( instance != null ) {
            throw new RuntimeException( "instance is already initialized" );
        }
        instance = new ManualUpdatesManager( app );
        return instance;
    }
    
    public static ManualUpdatesManager getInstance() {
        return instance;
    }

    public void checkForSimUpdates() {
        UpdateNotifier updateNotifier = new UpdateNotifier( projectName, sim, currentVersion );
        UpdateNotifier.UpdateListener listener = new UpdateNotifier.UpdateAdapter() {

            public void updateAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
                JDialog dialog = new ManualSimUpdateDialog( frame, projectName, sim, humanReadableSimName, currentVersion, remoteVersion, locale);
                dialog.setVisible( true );
            }

            public void noUpdateAvailable( PhetVersion currentVersion ) {
                JDialog dialog = new NoSimUpdateDialog( frame, currentVersion.formatForTitleBar(), humanReadableSimName );
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
    
    public void checkForInstallerUpdates() {
        //TODO implement
    }
}
