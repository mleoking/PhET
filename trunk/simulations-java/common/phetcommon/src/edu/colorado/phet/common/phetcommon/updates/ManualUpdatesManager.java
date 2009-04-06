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
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.InstallerResponse;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.Response;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.SimResponse;
import edu.colorado.phet.common.phetcommon.dialogs.ErrorDialog;
import edu.colorado.phet.common.phetcommon.files.PhetInstallation;
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
        
        final ISimInfo simInfo = app.getSimInfo();
        final Frame parentFrame = app.getPhetFrame();
        
        final VersionInfoQuery query = new VersionInfoQuery( simInfo, false /* automaticRequest */ );
        query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {
            
            public void done( final Response response ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        SimResponse simResponse = response.getSimResponse();
                        if ( simResponse != null && simResponse.isUpdateRecommended() ) {
                            new SimManualUpdateDialog( parentFrame, simInfo, simResponse.getVersion() ).setVisible( true );
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
        
        final PhetInstallerVersion currentInstallerVersion = PhetInstallation.getInstance().getInstallerVersion();
        final Frame parentFrame = app.getPhetFrame();
        
        final VersionInfoQuery query = new VersionInfoQuery( currentInstallerVersion, false /* automaticRequest */ );
        query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {
            
            public void done( final Response response ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        InstallerResponse installerResponse = response.getInstallerResponse();
                        if ( installerResponse != null && installerResponse.isUpdateRecommended() ) {
                            PhetInstallerVersion newInstallerVersion = installerResponse.getVersion();
                            JDialog dialog = new InstallerManualUpdateDialog( parentFrame, currentInstallerVersion, newInstallerVersion );
                            dialog.setVisible( true );
                        }
                        else {
                            JDialog dialog = new InstallerNoUpdateDialog( parentFrame, currentInstallerVersion );
                            dialog.setVisible( true );
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
