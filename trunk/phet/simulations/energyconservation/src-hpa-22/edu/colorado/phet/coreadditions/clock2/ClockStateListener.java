package edu.colorado.phet.coreadditions.clock2;

/**
 * Listens for changes in the execution state of the clock, ie whether it is running or paused.
 */
public interface ClockStateListener {
    public void clockStarted( AbstractClock source );

    public void clockStopped( AbstractClock source );

    public void clockDestroyed( AbstractClock source );

    public void clockDelayChanged( AbstractClock source, int newDelay );
}
