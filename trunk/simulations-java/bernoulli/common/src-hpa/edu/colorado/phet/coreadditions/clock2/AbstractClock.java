package edu.colorado.phet.coreadditions.clock2;

/**
 * An abstract clock can register listeners, start, stop and kill itself.
 */
public interface AbstractClock {

    /**Registers a listener for tick events.*/
    public void addTickListener(TickListener listener);

    /**Makes this clock start firing tick events.*/
    public void start();

    /**Pauses this clock.*/
    public void stop();

    /**Release all resources used by this clock object.*/
    void kill();

    void removeTickListener(TickListener t);

    void addClockStateListener(ClockStateListener listener);

    void setRequestedDelay(int requestedWaitTime);

    int getRequestedDelay();

    boolean isRunning();

}
