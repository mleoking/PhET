package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery.VersionInfoQueryResponse;
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
        
        final PhetInstallerVersion currentInstallerVersion = new PhetInstallerVersion( 0 ); //TODO get this from phet-installation.properties
        final VersionInfoQuery query = new VersionInfoQuery( simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion(), currentInstallerVersion, true /* automaticRequest */ );
        
        query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {
            
            public void done( final VersionInfoQueryResponse result ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        
                        // installer update
                        installerAskMeLaterStrategy.setDuration( result.getInstallerAskMeLaterDuration() );
                        if ( DeploymentScenario.getInstance() == DeploymentScenario.PHET_INSTALLATION && result.isInstallerUpdateRecommended() && installerAskMeLaterStrategy.isDurationExceeded() ) {
                            new InstallerAutomaticUpdateDialog( parentFrame, installerAskMeLaterStrategy ).setVisible( true );
                        }
                        
                        // sim update
                        simAskMeLaterStrategy.setDuration( result.getSimAskMeLaterDuration() );
                        PhetVersion remoteVersion = result.getSimVersion();
                        if ( result.isSimUpdateRecommended() && !simVersionSkipper.isSkipped( remoteVersion.getRevisionAsInt() ) && simAskMeLaterStrategy.isDurationExceeded() ) {
                            new SimAutomaticUpdateDialog( parentFrame, simInfo, remoteVersion, simAskMeLaterStrategy, simVersionSkipper ).setVisible( true );
                        }
                    }
                } );
            }
            
            public void exception( Exception e ) {
                //TODO handle differently?
                e.printStackTrace();
            }
        });
        
        // send query in separate thread
        Thread t = new Thread( new Runnable() {
            public void run() {
                query.send();
            }
        } );
        t.start();
    }
}
