/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simexample.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * The clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimExampleClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SimExampleClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }
}
