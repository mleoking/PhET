// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * ABSClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AABSClock extends ConstantDtClock {

    public static final int DEFAULT_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double DEFAULT_DT = 1;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AABSClock() {
        this( DEFAULT_FRAME_RATE, DEFAULT_DT );
    }
    
    protected AABSClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }
}
