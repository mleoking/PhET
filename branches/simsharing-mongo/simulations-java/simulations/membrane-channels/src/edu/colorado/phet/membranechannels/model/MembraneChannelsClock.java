// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * The clock for this simulation. The simulation time change (dt) on each
 * clock tick is constant, regardless of when (in wall time) the ticks
 * actually happen.
 *
 * @author John Blanco
 */
public class MembraneChannelsClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MembraneChannelsClock( int framesPerSecond, double dt ) {
        super( 1000 / framesPerSecond, dt );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
}
