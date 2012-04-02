// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;

/**
 * Faucet node for pressure tab, to enable teachers/students to make predictions about the pressure before filling up the tank and revealing the value.
 *
 * @author Sam Reid
 */
public class FluidPressureFaucetNode extends PNode {
    public FluidPressureFaucetNode( final Property<Double> flowRatePercentage, final ObservableProperty<Boolean> enabled ) {
        addChild( new FaucetNode( UserComponents.fluidPressureFaucet, flowRatePercentage, enabled, 500, true ) );
    }
}