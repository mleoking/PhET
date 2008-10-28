package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.DefaultTrackingPreferences;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

public class TrackingManager {
    private PhetApplicationConfig config;

    public static TrackingManager instance;
    private Vector messageQueue = new Vector();
    private TrackingThread trackingThread = new TrackingThread();
    private static final Object monitor = new Object();

    public TrackingManager( PhetApplicationConfig config ) {
        this.config = config;
        trackingThread.start();
    }

    public static void initInstance( PhetApplicationConfig config ) {
        if ( instance != null ) {
            throw new RuntimeException( "TrackingManager already initialized" );
        }
        instance = new TrackingManager( config );
    }

    public static void postMessage( TrackingMessage trackingMessage ) {
        //check for tracking enabled before message construction
        // because may construction may cause java.security.AccessControlException under web start.
        if ( instance.isTrackingEnabled() ) {
            instance.postMessageImpl( trackingMessage );
        }
    }

    private void postMessageImpl( final TrackingMessage trackingMessage ) {
        if ( isTrackingEnabled() ) {
            messageQueue.add( trackingMessage );
            synchronized( monitor ) {
                monitor.notifyAll();
            }
        }
    }

    private class TrackingThread extends Thread {
        private TrackingThread() {
            super( new TrackingRunnable() );
        }
    }

    public class TrackingRunnable implements Runnable {
        public void run() {
            while ( true ) {
                postAllMessages();
                synchronized( monitor ) {
                    try {
                        monitor.wait();
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void postAllMessages() {
        try {
            while ( TrackingManager.this.messageQueue.size() > 0 ) {
                TrackingMessage m = (TrackingMessage) TrackingManager.this.messageQueue.remove( 0 );
                new Tracker().postMessage( m );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private boolean isTrackingEnabled() {
        return isTrackingCommandLineOptionSet() && isTrackingAllowed();
    }

    private boolean isTrackingAllowed() {
        //todo: perhaps we should use PhetPreferences.isTrackingEnabled(String,String)
        return new DefaultTrackingPreferences().isEnabled();
    }

    public boolean isTrackingCommandLineOptionSet() {
        return Arrays.asList( config.getCommandLineArgs() ).contains( "-tracking" ) && !PhetServiceManager.isJavaWebStart();
    }

    public static void postActionPerformedMessage( TrackingMessage.MessageType messageType ) {
        postMessage( new ActionPerformedMessage( new SessionID( instance.config ), messageType.getName() ) );
    }

    public static void postStateChangedMessage( String name, boolean oldValue, boolean newValue ) {
        postStateChangedMessage( name, Boolean.valueOf( oldValue ), Boolean.valueOf( newValue ) );
    }

    public static void postStateChangedMessage( String name, Object oldValue, Object newValue ) {
        postMessage( new StateChangedMessage( new SessionID( instance.config ), name, oldValue.toString(), newValue.toString() ) );
    }
}
