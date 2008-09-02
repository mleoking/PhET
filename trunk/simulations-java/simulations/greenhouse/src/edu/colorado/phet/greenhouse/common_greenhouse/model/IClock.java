/**
 * Class: IClock
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Jul 21, 2003
 */
package edu.colorado.phet.greenhouse.common_greenhouse.model;

public interface IClock extends Runnable {
    void addClockStateListener( ClockStateListener csl );

    double getRunningTime();

    double getTimeLimit();

    void setTimeLimit( double timeLimit );

    double getRequestedDT();

    int getRequestedWaitTime();

    void setRequestedDT( double requestedDT );

    void setRequestedWaitTime( int requestedWaitTime );

//    void setThreadPriority( ThreadPriority tp );

    boolean isActiveAndRunning();

    void setTimeIncrement( double dt );

    void start();

    void setAlive( boolean b );

    void setRunning( boolean b );

    void stop();

    void reset();

    void tickOnce( double dt );

    void run();

    String toString();

    ThreadPriority getThreadPriority();

    boolean isStarted();

    void removeClockTickListener( ClockTickListener listener );

    void addClockTickListener( ClockTickListener tickListener );

    void setParent( ApplicationModel parent );
}
