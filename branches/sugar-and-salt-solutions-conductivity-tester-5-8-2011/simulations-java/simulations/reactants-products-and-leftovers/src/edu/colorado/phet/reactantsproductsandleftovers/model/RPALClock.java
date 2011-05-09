// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Clock for all simulations in this project.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALClock extends ConstantDtClock {

    public RPALClock() {
        super( 1000 / 5, 1 );
    }
}
