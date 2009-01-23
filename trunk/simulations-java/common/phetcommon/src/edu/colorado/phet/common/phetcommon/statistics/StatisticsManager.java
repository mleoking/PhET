package edu.colorado.phet.common.phetcommon.statistics;

import java.io.IOException;
import java.util.Vector;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;

/**
 * Manages the delivery of statistics messages.
 * 
 * @author Sam Reid
 */
public class StatisticsManager {
    
    private static final Object MONITOR = new Object();
    
    /* singleton */
    public static StatisticsManager instance;
    
    private final ISimInfo simInfo;
    private final Vector messageQueue = new Vector();
    private final StatisticsThread statisticsThread = new StatisticsThread();
    private final IStatisticsService statisticsService = new PHPStatisticsService();

    /* singleton */
    private StatisticsManager( ISimInfo simInfo ) {
        this.simInfo = simInfo;
        statisticsThread.start();
    }

    public static StatisticsManager initInstance( ISimInfo simInfo ) {
        if ( instance != null ) {
            throw new RuntimeException( "StatisticsManager instance is already initialized" );
        }
        instance = new StatisticsManager( simInfo );
        return instance;
    }

    public static StatisticsManager getInstance() {
        return instance;
    }
    
    /**
     * Blocks until all queued messages have been sent, up to a maximum of maxWaitTime milliseconds.
     */
    public static void waitFor( long maxWaitTimeMillis ) {
        if ( isStatisticsEnabled() ) {
            instance._waitFor( maxWaitTimeMillis );
        }
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

    private void postMessageImpl( final StatisticsMessage statisticsMessage ) {
        if ( isStatisticsEnabled() ) {
            messageQueue.add( statisticsMessage );
            synchronized( MONITOR ) {
                MONITOR.notifyAll();
            }
        }
    }

    private class StatisticsThread extends Thread {
        private StatisticsThread() {
            super( new StatisticsRunnable() );
        }
    }

    public class StatisticsRunnable implements Runnable {
        public void run() {
            while ( true ) {
                postAllMessages();
                synchronized( MONITOR ) {
                    try {
                        MONITOR.wait();
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
                StatisticsMessage m = (StatisticsMessage) messageQueue.get( 0 );
                statisticsService.postMessage( m );
                messageQueue.remove( m ); // remove message from queue after it has been sent, so that messageQueue won't be considered empty prematurely
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    public static boolean isStatisticsEnabled() {
        return instance != null && instance.simInfo.isStatisticsEnabled();
    }

    public static void postMessage( StatisticsMessage statisticsMessage ) {
        // check for statistics enabled before message construction
        // because construction may cause java.security.AccessControlException under web start.
        if ( isStatisticsEnabled() ) {
            instance.postMessageImpl( statisticsMessage );
        }
    }
}
