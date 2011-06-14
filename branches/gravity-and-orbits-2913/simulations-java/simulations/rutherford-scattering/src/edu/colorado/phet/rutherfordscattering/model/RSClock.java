// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.rutherfordscattering.RSConstants;


/**
 * RSClock is the clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSClock extends SwingClock {

    /* Wall time between clock ticks (milliseconds) */
    private static final int WALL_DT = ( 1000 / RSConstants.CLOCK_FRAME_RATE );
    
    /* Sim time between ticks (dt) */
    private static final double SIM_DT = RSConstants.CLOCK_STEP;
    
    public RSClock() {
        super( WALL_DT, SIM_DT );
    }
}
