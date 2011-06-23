// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.unfuddle;

import edu.colorado.phet.unfuddle.UnfuddleEmailNotifier.Listener;

/**
 * Sometimes the Unfuddle notifier crashes partway through a run.  This outer loop checks to see if the notifier failed to run, if so, it kills the Java Process.
 * An outer wrapper script will then immediately restart the UnfuddleEmailNotifier main class.
 *
 * @author Sam Reid
 */
public class UnfuddleCrashWorkaround {
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
                        Thread.sleep( 10000 );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    long timeSinceLastBatch = System.currentTimeMillis() - lastBatchCompleteTime;
                    System.out.println( "Minutes since last batch complete: " + timeSinceLastBatch / 1000.0 / 60.0 );
                    if ( timeSinceLastBatch > emailNotifier.getTimerDelay() * 2.5 ) {//missed 2 or so
                        System.out.println( "It's been a long time since the email notifier finished a batch; perhaps it has halted" );
                        System.out.println( "Shutting down the process" );
                        System.exit( 0 );
                    }
                }
            }
        } );
        thread.start();
    }

    public void start() {
        System.out.println( "Started Unfuddle Crash Workaround" );
    }
}
