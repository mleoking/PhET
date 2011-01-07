// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * The clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 */
public class GravityAndOrbitsClock extends ConstantDtClock {
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double DAYS_PER_TICK = 1;
    public static final int SECONDS_PER_DAY = 86400;
    public static final double DEFAULT_DT = DAYS_PER_TICK * SECONDS_PER_DAY;

    public GravityAndOrbitsClock( double dt ) {
        this( CLOCK_FRAME_RATE, dt );
    }

    public GravityAndOrbitsClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }
}
