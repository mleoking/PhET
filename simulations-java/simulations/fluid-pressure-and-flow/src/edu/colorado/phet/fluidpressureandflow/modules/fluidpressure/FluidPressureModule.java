package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;

/**
 * @author Sam Reid
 */
public class FluidPressureModule extends FluidPressureAndFlowModule<FluidPressureModel> {
    public FluidPressureModule() {
        super( "Pressure", new FluidPressureModel() );
        setSimulationPanel( new FluidPressureCanvas( this ) );
    }
}
