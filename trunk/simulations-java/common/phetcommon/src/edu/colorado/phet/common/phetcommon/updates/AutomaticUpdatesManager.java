package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.dialogs.SimAutomaticUpdateDialog;

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
        
        final UpdateNotifier updateNotifier = new UpdateNotifier( simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion() );
        updateNotifier.addListener( new UpdateNotifier.UpdateAdapter() {

            public void updateAvailable( PhetVersion currentVersion, final PhetVersion remoteVersion ) {
                if ( !simVersionSkipper.isSkipped( remoteVersion.getRevisionAsInt() ) ) {
                    //show UI in swing thread after new thread has found a new version
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            new SimAutomaticUpdateDialog( parentFrame, simInfo, remoteVersion, simAskMeLaterStrategy, simVersionSkipper ).setVisible( true );
                        }
                    } );
                }
            }
        } );

        //do check in new thread
        Thread t = new Thread( new Runnable() {
            public void run() {
                updateNotifier.checkForUpdates();
            }
        } );
        t.start();
    }
}
