// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.common;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * NuclearPhysicsClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NuclearPhysicsClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NuclearPhysicsClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /**
     * Reset the clock when dt is changed.
     * 
     * @param dt
     */
    public void setDt( double dt ) {
        super.setDt( dt );
        resetSimulationTime();
    }
}
