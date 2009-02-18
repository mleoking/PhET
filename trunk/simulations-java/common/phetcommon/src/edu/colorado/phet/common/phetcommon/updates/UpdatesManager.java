package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.statistics.SessionMessage;
import edu.colorado.phet.common.phetcommon.updates.dialogs.AutomaticSimUpdateDialog;

/**
 * Handles automatic checking for updates when the simulation starts. 
 */
public class UpdatesManager {

    /* singleton */
    private static UpdatesManager instance;
    
    private final ISimInfo simInfo;
    private final IVersionSkipper versionSkipper;
    private final IUpdateTimer updateTimer;
    private boolean applicationStartedCalled;
    
    /* singleton */
    private UpdatesManager( ISimInfo simInfo ) {
        this.simInfo = simInfo;
        versionSkipper = new DefaultVersionSkipper( simInfo.getProjectName(), simInfo.getFlavor() );
        updateTimer = new DefaultUpdateTimer( simInfo.getProjectName(), simInfo.getFlavor() );
        applicationStartedCalled = false;
    }
    
    public static UpdatesManager initInstance( ISimInfo simInfo ) {
        if ( instance != null ) {
            throw new RuntimeException( "UpdatesManager instance is already initialized" );
        }
        instance = new UpdatesManager( simInfo );
        return instance;
    }
    
    public static UpdatesManager getInstance() {
        return instance;
    }

    public void applicationStarted( Frame frame, SessionMessage sessionMessage ) {
        // this method should only be called once
        if ( applicationStartedCalled ) {
            throw new IllegalStateException( "attempted to call applicationStarted more than once" );
        }
        applicationStartedCalled = true;
        if ( simInfo.isUpdatesEnabled() && updateTimer.isDurationExceeded() ) {
            autoCheckForUpdates( frame, sessionMessage );
        }
    }

    private void autoCheckForUpdates( final Frame frame,  final SessionMessage sessionMessage ) {
        final UpdateNotifier updateNotifier = new UpdateNotifier( simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getVersion() );
        updateNotifier.addListener( new UpdateNotifier.UpdateAdapter() {

            public void updateAvailable( PhetVersion currentVersion, final PhetVersion remoteVersion ) {
                if ( !versionSkipper.isSkipped( remoteVersion.getRevisionAsInt() ) ) {
                    //show UI in swing thread after new thread has found a new version
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            new AutomaticSimUpdateDialog( frame, simInfo, remoteVersion, updateTimer, versionSkipper ).setVisible( true );
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
