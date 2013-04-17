// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Clock for all simulations in this project.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEClock extends ConstantDtClock {

    public BCEClock() {
        super( 1000 / 5, 1 );
    }
}
