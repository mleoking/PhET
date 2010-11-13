package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;

/**
 * @author Sam Reid
 */
public class FluidFlowModule extends FluidPressureAndFlowModule<FluidFlowModel> {
    public FluidFlowModule() {
        super( "Flow", new FluidFlowModel() );
    }
}
