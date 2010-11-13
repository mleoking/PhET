package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import edu.colorado.phet.fluidpressureandflow.view.FluidPressureAndFlowCanvas;

/**
 * @author Sam Reid
 */
public class FluidPressureCanvas extends FluidPressureAndFlowCanvas {
    private FluidPressureModule module;

    public FluidPressureCanvas( final FluidPressureModule module ) {
        super( module );
        this.module = module;
    }
}
