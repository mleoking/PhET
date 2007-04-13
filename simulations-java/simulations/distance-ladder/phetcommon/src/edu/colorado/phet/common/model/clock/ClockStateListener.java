package edu.colorado.phet.common.model.clock;

import edu.colorado.phet.common.model.ThreadPriority;

/**
 * User: Sam Reid
 * Date: Jun 11, 2003
 * Time: 10:45:32 AM
 * Copyright (c) Jun 11, 2003 by Sam Reid
 */
public interface ClockStateListener {
    void delayChanged( int waitTime );

    void dtChanged( double dt );

    void threadPriorityChanged( ThreadPriority tp );
}
