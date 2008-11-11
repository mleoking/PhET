package edu.colorado.phet.common.phetcommon.updates;

import java.awt.Frame;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.DefaultUpdatePreferences;
import edu.colorado.phet.common.phetcommon.preferences.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.dialogs.AutomaticUpdateDialog;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

public class UpdateApplicationManager {
    
    private final PhetApplicationConfig config;
    private final IVersionSkipper versionSkipper;
    private final IUpdateTimer updateTimer;

    public UpdateApplicationManager( PhetApplicationConfig config ) {
        this.config = config;
        versionSkipper = new DefaultVersionSkipper( config.getProjectName(), config.getFlavor() );
        updateTimer = new DefaultUpdateTimer( config.getProjectName(), config.getFlavor() );
    }

    private boolean isUpdatesAllowed() {
        //todo: perhaps we should use PhetPreferences.isUpdatesEnabled(String,String)
        boolean enabledForSelection = new DefaultUpdatePreferences().isEnabled();
//        System.out.println( "updates allowed = " + enabledForSelection );
        return enabledForSelection;
    }

    public void applicationStarted( Frame frame,ISimInfo simInfo,ITrackingInfo trackingInfo) {
        if ( isUpdatesEnabled() && isUpdatesAllowed() && updateTimer.isDurationExceeded() ) {
            autoCheckForUpdates( frame, simInfo, trackingInfo );
        }
    }

    private void autoCheckForUpdates( final Frame frame, final ISimInfo simInfo, final ITrackingInfo trackingInfo ) {
        System.out.println( "UpdateApplicationManager.autoCheckForUpdate" );//XXX
        TrackingManager.postActionPerformedMessage( TrackingMessage.AUTO_CHECK_FOR_UPDATES );
        final UpdateNotifier updateNotifier = new UpdateNotifier( config.getProjectName(), config.getVersion() );
        updateNotifier.addListener( new UpdateNotifier.UpdateAdapter() {

            public void updateAvailable( PhetVersion currentVersion, final PhetVersion remoteVersion ) {
                System.out.println( "UpdateApplicationManager.updateAvailable" );//XXX
                if ( !versionSkipper.isSkipped( remoteVersion.getRevisionAsInt() ) ) {
                    //show UI in swing thread after new thread has found a new version
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            new AutomaticUpdateDialog( frame, simInfo, trackingInfo, remoteVersion, updateTimer, versionSkipper ).setVisible( true );
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

    public boolean isUpdatesEnabled() {
        return Arrays.asList( config.getCommandLineArgs() ).contains( "-updates" ) && !PhetUtilities.isRunningFromWebsite();
    }

}
