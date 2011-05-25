// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Primary model class for the tab that depicts torque on a plank, a.k.a. a teeter totter.
 *
 * @author John Blanco
 */
public class TeeterTotterTorqueModel {

    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    public ConstantDtClock getClock() {
        return clock;
    }
}
