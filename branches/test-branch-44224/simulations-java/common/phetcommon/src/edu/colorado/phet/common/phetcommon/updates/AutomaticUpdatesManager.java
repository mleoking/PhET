package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.net.UnknownHostException;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.InstallerResponse;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.Response;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.SimResponse;
import edu.colorado.phet.common.phetcommon.files.PhetInstallation;
import edu.colorado.phet.common.phetcommon.resources.PhetInstallerVersion;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.dialogs.InstallerAutomaticUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.dialogs.SimAutomaticUpdateDialog;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;

/**
 * Handles automatic checking for updates when the simulation starts. 
 */
public class AutomaticUpdatesManager {

    /* singleton */
    private static AutomaticUpdatesManager instance;
    
    private final ISimInfo simInfo;
    private final Frame parentFrame;
    private final IVersionSkipper simVersionSkipper;
    private final IAskMeLaterStrategy simAskMeLaterStrategy;
    private final IAskMeLaterStrategy installerAskMeLaterStrategy;
    private boolean started;
    
    /* singleton */
    private AutomaticUpdatesManager( PhetApplication app ) {
        simInfo = app.getSimInfo();
        parentFrame = app.getPhetFrame();
        simVersionSkipper = new SimVersionSkipper( simInfo.getProjectName(), simInfo.getFlavor() );
        simAskMeLaterStrategy = new SimAskMeLaterStrategy( simInfo.getProjectName(), simInfo.getFlavor() );
        installerAskMeLaterStrategy = new InstallerAskMeLaterStrategy();
        started = false;
    }
    
    public static AutomaticUpdatesManager initInstance( PhetApplication app ) {
        if ( instance != null ) {
            throw new RuntimeException( "instance is already initialized" );
        }
        instance = new AutomaticUpdatesManager( app );
        return instance;
    }
    
    public static AutomaticUpdatesManager getInstance() {
        return instance;
    }

    public void start() {
        // this method should only be called once
        if ( started ) {
            throw new IllegalStateException( "attempted to call start more than once" );
        }
        started = true;
        if ( simInfo.isUpdatesEnabled() && simAskMeLaterStrategy.isDurationExceeded() ) {
            runUpdateCheckThread();
        }
    }

    private void runUpdateCheckThread() {
        
        VersionInfoQuery query = null;
        if ( DeploymentScenario.getInstance() == DeploymentScenario.PHET_INSTALLATION ) {
            // get info for sim and installer
            PhetInstallerVersion currentInstallerVersion = PhetInstallation.getInstance().getInstallerVersion();
            query = new VersionInfoQuery( simInfo, currentInstallerVersion, true /* automaticRequest */ );
        }
        else {
            // get info for sim only
            query = new VersionInfoQuery( simInfo, true /* automaticRequest */ );
        }
        
        query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {
            
            public void done( final Response response ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        
                        // installer update
                        InstallerResponse installerResponse = response.getInstallerResponse();
                        if ( installerResponse != null ) {
                            installerAskMeLaterStrategy.setDuration( installerResponse.getAskMeLaterDuration() );
                            if ( DeploymentScenario.getInstance() == DeploymentScenario.PHET_INSTALLATION && installerResponse.isUpdateRecommended() && installerAskMeLaterStrategy.isDurationExceeded() ) {
                                PhetInstallerVersion currentInstallerVersion = response.getQuery().getCurrentInstallerVersion();
                                PhetInstallerVersion newInstallerVersion = installerResponse.getVersion();
                                JDialog dialog = new InstallerAutomaticUpdateDialog( parentFrame, installerAskMeLaterStrategy, currentInstallerVersion, newInstallerVersion );
                                dialog.setVisible( true );
                            }
                        }
                        
                        // sim update
                        SimResponse simResponse = response.getSimResponse();
                        if ( simResponse != null ) {
                            simAskMeLaterStrategy.setDuration( simResponse.getAskMeLaterDuration() );
                            PhetVersion remoteVersion = simResponse.getVersion();
                            if ( simResponse.isUpdateRecommended() && !simVersionSkipper.isSkipped( remoteVersion.getRevisionAsInt() ) && simAskMeLaterStrategy.isDurationExceeded() ) {
                                JDialog dialog = new SimAutomaticUpdateDialog( parentFrame, simInfo, remoteVersion, simAskMeLaterStrategy, simVersionSkipper );
                                dialog.setVisible( true );
                            }
                        }
                    }
                } );
            }
            
            public void exception( Exception e ) {
                if ( e instanceof UnknownHostException ) {
                    // user is probably not connected to the Internet
                    System.out.println( getClass().getName() + ": cannot connect, " + e.toString() );
                }
                else {
                    e.printStackTrace(); //TODO handle differently?
                }
            }
        });
        
        // send query in separate thread
        final VersionInfoQuery finalQuery = query;
        Thread t = new Thread( new Runnable() {
            public void run() {
                finalQuery.send();
            }
        } );
        t.start();
    }
}
