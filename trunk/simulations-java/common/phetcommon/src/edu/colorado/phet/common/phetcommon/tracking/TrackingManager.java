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
        // check for tracking enabled before message construction
        // because construction may cause java.security.AccessControlException under web start.
        if ( instance.isTrackingEnabled() ) {
            instance.postMessageImpl( trackingMessage );
        }
    }

    /**
     * Blocks until all queued messages have been sent, up to a maximum of maxWaitTime milliseconds.
     */
    public static void waitFor( long maxWaitTimeMillis ) {
        instance._waitFor( maxWaitTimeMillis );
    }

    //Currently implemented with polling, should probably be converted to non-polling solution
    private void _waitFor( long maxWaitTimeMillis ) {
        long startTime = System.currentTimeMillis();
        while ( true ) {
            if ( messageQueue.isEmpty() || System.currentTimeMillis() - startTime >= maxWaitTimeMillis ) {
                return;
            }
            else {
                try {
                    Thread.sleep( 10 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
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
            while ( messageQueue.size() > 0 ) {
                TrackingMessage m = (TrackingMessage) messageQueue.get( 0 );
                new Tracker().postMessage( m );
                messageQueue.remove( m ); // remove message from queue after it has been sent, so that messageQueue won't be considered empty prematurely
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

    public static void postActionPerformedMessage( String actionName ) {
        postMessage( new ActionPerformedMessage( new SessionID( instance.config ), actionName ) );
    }

    public static void postStateChangedMessage( String name, boolean oldValue, boolean newValue ) {
        postStateChangedMessage( name, Boolean.valueOf( oldValue ), Boolean.valueOf( newValue ) );
    }

    public static void postStateChangedMessage( String name, Object oldValue, Object newValue ) {
        postMessage( new StateChangedMessage( new SessionID( instance.config ), name, oldValue.toString(), newValue.toString() ) );
    }

    public static void postSessionEndedMessage() {
        postMessage( new SessionEndedMessage( new SessionID( instance.config ) ) );
    }
}
