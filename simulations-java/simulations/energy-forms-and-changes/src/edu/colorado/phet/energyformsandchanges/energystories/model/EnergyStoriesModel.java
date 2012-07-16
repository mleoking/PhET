// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energystories.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * @author John Blanco
 */
public class EnergyStoriesModel {
    // Main model clock.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    public IClock getClock() {
        return clock;
    }
}
