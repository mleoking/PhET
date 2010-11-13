package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.view.FluidPressureCanvas;

/**
 * @author Sam Reid
 */
public class FluidPressureModule<T extends FluidPressureAndFlowModel> extends FluidPressureAndFlowModule<FluidPressureModel> {
    public FluidPressureModule() {
        super( "Pressure", new FluidPressureModel() );
        setSimulationPanel( new FluidPressureCanvas( this ) );
    }
}
