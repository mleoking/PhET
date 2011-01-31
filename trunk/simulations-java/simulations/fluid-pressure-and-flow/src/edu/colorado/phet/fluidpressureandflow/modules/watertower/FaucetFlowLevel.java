// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class FaucetFlowLevel {
    public final Property<Double> flow = new Property<Double>( 0.0 );
    public final Property<Boolean> automatic = new Property<Boolean>( false );

    public void reset() {
        flow.reset();
        automatic.reset();
    }
}
