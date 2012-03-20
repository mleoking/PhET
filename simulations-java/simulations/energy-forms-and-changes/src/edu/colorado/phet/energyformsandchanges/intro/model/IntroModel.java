// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Primary model class for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class IntroModel {
    protected final ConstantDtClock clock = new ConstantDtClock( 30.0 );


    public void reset() {
        // TODO.
    }

    public IClock getClock() {
        return clock;
    }
}
