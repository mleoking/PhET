// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model class for the rate of faucet flow into the water tower, and whether it is on automatic or manual.
 *
 * @author Sam Reid
 */
public class FaucetFlowRate {
    public final Property<Double> flow = new Property<Double>( 0.0 );
    public final Property<Boolean> automatic = new Property<Boolean>( false );

    public void reset() {
        flow.reset();
        automatic.reset();
    }
}
