// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure;

import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.PRESSURE;

/**
 * @author Sam Reid
 */
public class FluidPressureModule extends FluidPressureAndFlowModule<FluidPressureModel> {
    public FluidPressureModule() {
        super( PRESSURE, new FluidPressureModel() );
        setSimulationPanel( new FluidPressureCanvas( this ) );
    }
}
