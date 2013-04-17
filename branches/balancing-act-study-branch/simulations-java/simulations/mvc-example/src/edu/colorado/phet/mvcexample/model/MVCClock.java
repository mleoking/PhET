// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * GlaciersClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MVCClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MVCClock( int frameRate, double dt, boolean running ) {
        super( 1000 / frameRate, dt );
        setRunning( running );
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
