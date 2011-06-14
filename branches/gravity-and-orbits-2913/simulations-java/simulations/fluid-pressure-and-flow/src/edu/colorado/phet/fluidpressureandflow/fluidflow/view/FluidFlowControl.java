// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow.view;

import java.util.HashMap;

import edu.colorado.phet.fluidpressureandflow.common.view.SliderControl;
import edu.colorado.phet.fluidpressureandflow.common.view.TickLabel;
import edu.colorado.phet.fluidpressureandflow.fluidflow.FluidFlowModule;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.FLOW;

/**
 * Allows the user to view and modify the flow rate within the pipe.
 *
 * @author Sam Reid
 */
public class FluidFlowControl extends PNode {
    public FluidFlowControl( final FluidFlowModule module ) {
        //Create the slider

        final SliderControl fluidDensityControl =
                new SliderControl( FLOW, "L/sec/ft^2", 1E-6, 10,
                                   module.getFluidPressureAndFlowModel().getPipe().k,
//                                   new ScaledDoubleProperty( module.getFluidPressureAndFlowModel().getPipe().k, module.getFluidFlowModel().units.getValue().flow.siToUnit( 1.0 ) ),
                                   new HashMap<Double, TickLabel>() );
        //Add children
        addChild( fluidDensityControl );
    }
}
