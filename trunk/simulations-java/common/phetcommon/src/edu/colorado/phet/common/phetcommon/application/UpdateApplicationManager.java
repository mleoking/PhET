package edu.colorado.phet.common.phetcommon.application;

import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.preferences.DefaultUpdatePreferences;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.updates.UpdateManager;
import edu.colorado.phet.common.phetcommon.updates.dialogs.AutomaticUpdateDialog;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;

public class UpdateApplicationManager {
    private PhetApplicationConfig config;

    public UpdateApplicationManager( PhetApplicationConfig config ) {
        this.config = config;
    }

    private boolean isUpdatesAllowed() {
        //todo: perhaps we should use PhetPreferences.isUpdatesEnabled(String,String)
        boolean enabledForSelection = new DefaultUpdatePreferences().isEnabled();
//        System.out.println( "updates allowed = " + enabledForSelection );
        return enabledForSelection;
    }

    public void applicationStarted( PhetApplication app ) {
        if ( isUpdatesEnabled() && isUpdatesAllowed() && hasEnoughTimePassedSinceAskMeLater() ) {
            autoCheckForUpdates( app );
        }
    }

    private boolean hasEnoughTimePassedSinceAskMeLater() {
        long lastTimeUserPressedAskMeLaterForAnySim = PhetPreferences.getInstance().getAskMeLater( config.getProjectName(), config.getFlavor() );
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastTimeUserPressedAskMeLaterForAnySim;
        int millisecondsDelayBeforeAskingAgain = 1000 * 60 * 60 * 24;
//        System.out.println( "elapsedTime/1000.0 = " + elapsedTime / 1000.0+" sec" );
        return elapsedTime > millisecondsDelayBeforeAskingAgain || lastTimeUserPressedAskMeLaterForAnySim == 0;
    }

    private void autoCheckForUpdates( final PhetApplication app ) {
        TrackingManager.postMessage( TrackingMessage.AUTO_CHECK_FOR_UPDATES );
        final UpdateManager updateManager = new UpdateManager( config.getProjectName(), config.getVersion() );
        updateManager.addListener( new UpdateManager.Listener() {
            public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
            }

            public void newVersionAvailable( PhetVersion currentVersion, final PhetVersion remoteVersion ) {
                int remoteVersionSVN = remoteVersion.getRevisionAsInt();
                int requestedSkipSVN = PhetPreferences.getInstance().getSkipUpdate( config.getProjectName(), config.getFlavor() );
//                System.out.println( "remoteVersionSVN = " + remoteVersionSVN + ", requestedSkipSVN=" + requestedSkipSVN );
                if ( remoteVersionSVN > requestedSkipSVN )
                //show UI in swing thread after new thread has found a new version
                {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            new AutomaticUpdateDialog( app, remoteVersion ).setVisible( true );
                        }
                    } );
                }
            }

            public void exceptionInUpdateCheck( IOException e ) {
            }

            public void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
            }
        } );

        //do check in new thread
        Thread t = new Thread( new Runnable() {
            public void run() {
                updateManager.checkForUpdates();
            }
        } );
        t.start();
    }

    public boolean isUpdatesEnabled() {
        return Arrays.asList( config.getCommandLineArgs() ).contains( "-updates" ) && !PhetServiceManager.isJavaWebStart();
    }

}
