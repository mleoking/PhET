package edu.colorado.phet.coreadditions.clock2;

/**
 * Signifies that a class listenens for tick events.
 */
public interface TickListener {
    public void clockTicked( AbstractClock source );
}
