package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetInfoQuery;
import edu.colorado.phet.common.phetcommon.application.PhetInfoQuery.PhetInfoQueryResult;
import edu.colorado.phet.common.phetcommon.updates.dialogs.InstallerManualUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateErrorDialog;

/**
 * Handles manual requests for update checks.
 * <p>
 * If an update is found, an dialog is displayed that allows the user to perform the update.
 * If no update is found, a dialog notifies the user.
 */
public class ManualUpdatesManager {
    
    private static ManualUpdatesManager instance;
    
    private final ISimInfo simInfo;
    private Frame frame;
    
    private ManualUpdatesManager( PhetApplication app ) {
        this.frame = app.getPhetFrame();
        this.simInfo = app.getSimInfo();
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
        
        final long currentPhetInstallationTimestamp = 0; //TODO get this from phet-installation.properties
        final PhetInfoQuery query = new PhetInfoQuery( simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion(), currentPhetInstallationTimestamp );
        query.addListener( new PhetInfoQuery.Listener() {
            public void queryDone( final PhetInfoQueryResult result ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        if ( result.isInstallerUpdateRecommended() ) {
                            new InstallerManualUpdateDialog( frame ).setVisible( true );
                        }
                    }
                } );
            }
            
            public void exception( Exception e ) {
                JDialog dialog = new UpdateErrorDialog( frame, e );
                dialog.setVisible( true );
            }
        });
        query.start();
    }
    
    public void checkForInstallerUpdates() {
        final long currentPhetInstallationTimestamp = 0; //TODO get this from phet-installation.properties
        final PhetInfoQuery query = new PhetInfoQuery( simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion(), currentPhetInstallationTimestamp );
        query.addListener( new PhetInfoQuery.Listener() {
            public void queryDone( final PhetInfoQueryResult result ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        if ( result.isInstallerUpdateRecommended() ) {
                            new InstallerManualUpdateDialog( frame ).setVisible( true );
                        }
                        else {
                            JOptionPane.showMessageDialog( frame, "No update for you." );//TODO make a real dialog
                        }
                    }
                } );
            }
            
            public void exception( Exception e ) {
                JDialog dialog = new UpdateErrorDialog( frame, e );
                dialog.setVisible( true );
            }
        });
        query.start();
    }
}
