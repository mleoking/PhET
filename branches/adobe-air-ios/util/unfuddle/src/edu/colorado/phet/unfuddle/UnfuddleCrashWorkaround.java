// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.unfuddle;

import java.util.logging.Logger;

import edu.colorado.phet.unfuddle.UnfuddleEmailNotifier.Listener;

/**
 * Sometimes the Unfuddle notifier crashes partway through a run.  This outer loop checks to see if the notifier failed to run, if so, it kills the Java Process.
 * An outer wrapper script will then immediately restart the UnfuddleEmailNotifier main class.
 *
 * @author Sam Reid
 */
public class UnfuddleCrashWorkaround {
    private final static Logger LOGGER = UnfuddleLogger.getLogger( UnfuddleAccountCurl.class );
    private long lastBatchCompleteTime = System.currentTimeMillis();

    public UnfuddleCrashWorkaround( final UnfuddleEmailNotifier emailNotifier ) {
        emailNotifier.addListener( new Listener() {
            public void batchComplete() {
                lastBatchCompleteTime = System.currentTimeMillis();
            }
        } );
        Thread thread = new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        Thread.sleep( 60000 );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    long timeSinceLastBatch = System.currentTimeMillis() - lastBatchCompleteTime;
                    LOGGER.info( "Minutes since last batch complete: " + timeSinceLastBatch / 1000.0 / 60.0 );

                    //If it is 25% over the anticipated batch time, then kill this process and allow the outer loop to restart the UnfuddleEmailNotifierAgain
                    if ( timeSinceLastBatch > emailNotifier.getTimerDelay() * 1.25 ) {
                        LOGGER.info( "It's been a long time since the email notifier finished a batch; perhaps it has halted" );
                        LOGGER.info( "Shutting down the process" );
                        System.exit( 0 );
                    }
                }
            }
        } );
        thread.start();
    }

    public void start() {
        LOGGER.info( "Started Unfuddle Crash Workaround" );
    }
}
