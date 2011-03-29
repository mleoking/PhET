// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Clock for all simulations in this project.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLClock extends ConstantDtClock {

    private static final int FRAMES_PER_SECOND = 25;
    private static final double DT = 1;

    public CLClock() {
        super( 1000 / FRAMES_PER_SECOND, DT );
    }
}
