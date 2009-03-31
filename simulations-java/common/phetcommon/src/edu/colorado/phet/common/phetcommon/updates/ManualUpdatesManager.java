package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.net.UnknownHostException;
import java.text.MessageFormat;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.VersionInfoQueryResponse;
import edu.colorado.phet.common.phetcommon.dialogs.ErrorDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetInstallerVersion;
import edu.colorado.phet.common.phetcommon.updates.dialogs.InstallerManualUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.SimManualUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateErrorDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.NoUpdateDialog.InstallerNoUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.NoUpdateDialog.SimNoUpdateDialog;

/**
 * Handles manual requests for update checks.
 * <p>
 * If an update is found, an dialog is displayed that allows the user to perform the update.
 * If no update is found, a dialog notifies the user.
 */
public class ManualUpdatesManager {
    
    private static final String ERROR_INTERNET_CONNECTION = PhetCommonResources.getString( "Common.updates.error.internetConnection" );
    
    private static ManualUpdatesManager instance;
    
    private final PhetApplication app;
    
    private ManualUpdatesManager( PhetApplication app ) {
        this.app = app;
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
        
        final PhetInstallerVersion currentInstallerVersion = new PhetInstallerVersion( 0 ); // don't care, since this query is for the sim
        final ISimInfo simInfo = app.getSimInfo();
        final Frame parentFrame = app.getPhetFrame();
        
        final VersionInfoQuery query = new VersionInfoQuery( simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion(), currentInstallerVersion, false /* automaticRequest */ );
        query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {
            
            public void done( final VersionInfoQueryResponse result ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        if ( result.isSimUpdateRecommended() ) {
                            new SimManualUpdateDialog( parentFrame, simInfo, result.getSimVersion() ).setVisible( true );
                        }
                        else {
                            new SimNoUpdateDialog( parentFrame, simInfo.getName(), simInfo.getVersion() ).setVisible( true );
                        }
                    }
                } );
            }
            
            public void exception( Exception e ) {
                handleException( parentFrame, e );
            }
        });
        query.send();
    }
    
    public void checkForInstallerUpdates() {
        
        final PhetInstallerVersion currentInstallerVersion = new PhetInstallerVersion( 0 ); //TODO get this from phet-installation.properties
        ISimInfo simInfo = app.getSimInfo();
        final Frame parentFrame = app.getPhetFrame();
        
        final VersionInfoQuery query = new VersionInfoQuery( simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion(), currentInstallerVersion, false /* automaticRequest */ );
        query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {
            
            public void done( final VersionInfoQueryResponse result ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        if ( result.isInstallerUpdateRecommended() ) {
                            new InstallerManualUpdateDialog( parentFrame ).setVisible( true );
                        }
                        else {
                            new InstallerNoUpdateDialog( parentFrame, currentInstallerVersion ).setVisible( true );
                        }
                    }
                } );
            }
            
            public void exception( Exception e ) {
                handleException( parentFrame, e );
            }
        });
        
        // OK that this blocks, since the user initiated the request
        query.send();
    }
    
    private void handleException( Frame parentFrame, Exception e ) {
        if ( e instanceof UnknownHostException ) {
            // user is probably not connected to the Internet
            Object[] args = { PhetCommonConstants.PHET_HOME_URL };
            String message = MessageFormat.format( ERROR_INTERNET_CONNECTION, args );
            JDialog dialog = new ErrorDialog( parentFrame, message );
            dialog.setVisible( true );
        }
        else {
            JDialog dialog = new UpdateErrorDialog( parentFrame, e );
            dialog.setVisible( true );
        }
    }
}
