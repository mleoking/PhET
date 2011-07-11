// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Clock used in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPClock extends ConstantDtClock {

    public MPClock() {
        super( 25 );
    }
}
