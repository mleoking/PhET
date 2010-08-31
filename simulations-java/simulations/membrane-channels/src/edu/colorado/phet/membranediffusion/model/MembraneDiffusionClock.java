/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * The clock for this simulation. The simulation time change (dt) on each
 * clock tick is constant, regardless of when (in wall time) the ticks
 * actually happen.
 *
 * @author John Blanco
 */
public class MembraneDiffusionClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MembraneDiffusionClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
}
