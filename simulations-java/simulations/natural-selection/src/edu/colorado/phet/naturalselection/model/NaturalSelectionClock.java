/* Copyright 2007, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * GlaciersClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NaturalSelectionClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public NaturalSelectionClock( int framesPerSecond, double dt ) {
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
