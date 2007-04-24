package edu.colorado.phet.distanceladder.common.model.clock;

import edu.colorado.phet.distanceladder.common.model.ThreadPriority;

/**
 * User: Sam Reid
 * Date: Jun 11, 2003
 * Time: 10:45:32 AM
 *
 */
public interface ClockStateListener {
    void delayChanged( int waitTime );

    void dtChanged( double dt );

    void threadPriorityChanged( ThreadPriority tp );
}
