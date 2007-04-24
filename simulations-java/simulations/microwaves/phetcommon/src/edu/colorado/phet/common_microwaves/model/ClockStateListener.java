package edu.colorado.phet.common_microwaves.model;

/**
 * User: Sam Reid
 * Date: Jun 11, 2003
 * Time: 10:45:32 AM
 *
 */
public interface ClockStateListener {
    void waitTimeChanged(int waitTime);

    void dtChanged(double dt);

    void threadPriorityChanged(ThreadPriority tp);
}
